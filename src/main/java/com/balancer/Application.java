package com.balancer;

import com.balancer.config.ConfigProvider;
import com.balancer.config.errors.RegistryOperationException;
import com.balancer.config.errors.ServiceUnavailableException;
import com.balancer.core.registration.ProviderRegistry;
import com.balancer.loadbalancer.InvocationType;
import com.balancer.loadbalancer.LoadBalancer;
import com.balancer.core.LoadBalancerBuilder;
import com.balancer.providers.Provider;
import com.balancer.providers.ProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Start load balancer application");
        LoadBalancer loadBalancer = new LoadBalancerBuilder().build();
        ProviderRegistry registry = loadBalancer.getRegistry();

        logger.info("--------> Step 2 – Register a list of providers");
        for (int i = 0; i < 10; i++) {
            Provider provider = new ProviderImpl(registry);
            try {
                registry.registerProvider(provider);
            } catch (RegistryOperationException roe) {
                logger.error("Unable to register instance: " + roe.toString());
            }
        }


        logger.info("--------> Step 3 – Random invocation");
        callAndPrintOut(loadBalancer);


        logger.info("--------> Step 4 – Round Robin invocation");
        loadBalancer.clearDistribution();
        loadBalancer.setInvocationType(InvocationType.ROUND_ROBIN);
        callAndPrintOut(loadBalancer);


        logger.info("--------> Step 5 – Manual node exclusion / inclusion");
        List<Provider> availableProviders = registry.getAvailableProviders();
        Provider provider1 = availableProviders.get(0);
        Provider provider2 = availableProviders.get(1);

        logger.info("--------> Step 5 – Manual node exclusion / inclusion ---> Exclude two providers");
        registry.deregisterProvider(provider1);
        registry.deregisterProvider(provider2);

        provider1.closeProvider();
        provider2.closeProvider();
        callAndPrintOut(loadBalancer);

        logger.info("--------> Step 5 – Manual node exclusion / inclusion ---> Include  new  provider");
        Provider newProvider = new ProviderImpl(registry);
        try {
            registry.registerProvider(newProvider);
        } catch (RegistryOperationException e) {
            e.printStackTrace();
        }
        callAndPrintOut(loadBalancer);

        logger.info("--------> Step 5 – Manual node exclusion / inclusion --->Close all providers");
        for (int i = 0; i < registry.getAvailableProviders().size(); i++) {
            registry.getAvailableProviders().get(i).closeProvider();
        }

        logger.info("--------> Step 6 – 7 - Heart beat checker");
        // Checked at src/test/java/com/balancer/registration/ProviderRegistryImplTest.java

        // Step 8 – Cluster Capacity Limit
        logger.info("--------> Step 8 – Cluster Capacity Limit");
        loadBalancer = new LoadBalancerBuilder().build();
        ConfigProvider.getConfig().getLoadBalancer().setMaxConcurrentWorkersPerProvider(2);
        registry = loadBalancer.getRegistry();

        provider1 = new ProviderImpl(registry);
        provider2 = new ProviderImpl(registry);
        logger.info("--------> Step 8 – Cluster Capacity Limit--->Capacity check");
        try {
            registry.registerProvider(provider1);
            registry.registerProvider(provider2);
        } catch (RegistryOperationException e) {
            e.printStackTrace();
        }


        provider1.invokeProvider(() -> {
            while (true) {
                Thread.sleep(1000);
            }
        });
        provider1.invokeProvider(() -> {
            while (true) {
                Thread.sleep(1000);
            }
        });
        provider2.invokeProvider(() -> {
            while (true) {
                Thread.sleep(1000);
            }
        });
        provider2.invokeProvider(() -> {
            while (true) {
                Thread.sleep(1000);
            }
        });


        try {
            logger.info("--------> Step 8 – Cluster Capacity Limit--->Check sync get");
            handleSynchronousGet(loadBalancer);
            logger.info("--------> Step 8 – Cluster Capacity Limit--->Check sync done");
        } catch (ServiceUnavailableException e) {
            logger.info("STEP 8: Expected service unavailable exception raised");
        }

        for (int i = 0; i < registry.getAvailableProviders().size(); i++) {
            registry.getAvailableProviders().get(i).closeProvider();
        }

        System.exit(0);
    }

    private static void callAndPrintOut(LoadBalancer lb) {
        try {
            for (int i = 0; i < 100; i++) {
                handleSynchronousGet(lb);
            }
        } catch (Exception e) {
            logger.warn("Unable to process request: " + e.toString());
        }

        printDistributions(lb);
        logger.info("<-- Load Balancer Distribution Invocation Type -->"+lb.getInvocationType());
    }

    private static void printDistributions(LoadBalancer lb1) {
        Map<String, Integer> distributions = lb1.getDistribution();
        int totalTasks = 0;
        for (Map.Entry<String, Integer> entry : distributions.entrySet()) {
            totalTasks += entry.getValue();
            logger.info("# of requests for: " + entry.getKey() + " -- " + entry.getValue());
        }
        logger.info("total distributed tasks: " + totalTasks);
    }

    public static void handleSynchronousGet(LoadBalancer lb) throws ServiceUnavailableException {
        try {
            Future f1 = lb.get();
            f1.get();
        } catch (InterruptedException e) {
            logger.error("Connection interrupted by peer" + e.toString());
        } catch (ExecutionException e) {
            logger.error("Connection interrupted by peer" + e.toString());
        }
    }
}
