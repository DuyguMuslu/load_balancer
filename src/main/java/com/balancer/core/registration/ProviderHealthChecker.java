package com.balancer.core.registration;

import com.balancer.config.errors.RegistryOperationException;
import com.balancer.core.ProviderActionFacade;
import com.balancer.core.ProviderActions;
import com.balancer.providers.Provider;
import com.balancer.providers.ProviderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProviderHealthChecker implements Runnable {

    private Logger logger = LoggerFactory.getLogger(ProviderHealthChecker.class);

    private final ExecutorService checkThreadPool;

    private final Provider provider;

    private ProviderRegistry registry;

    private final int healthCheckFrequencyMillis;

    private final int healthCheckRequestTimeoutMillis;


    public ProviderHealthChecker(ExecutorService checkThreadPool, ProviderRegistry registry, Provider provider) {
        this.checkThreadPool = checkThreadPool;
        this.registry = registry;
        this.provider = provider;
        this.healthCheckFrequencyMillis = registry.getCurrentConfig().getHealthCheckFrequencyMillis();
        this.healthCheckRequestTimeoutMillis = registry.getCurrentConfig().getHealthCheckRequestTimeoutMillis();
    }

    public void start() {
        this.checkThreadPool.submit(this);
    }

    @Override
    public void run() {
        int consecutiveSuccessesChecks = 0;
        while (!Thread.interrupted()) {
            ProviderStatus status = ProviderStatus.OUT_OF_SERVICE;
            try {
                status = (ProviderStatus) provider.invokeProvider(new ProviderActionFacade(provider)
                        .getProviderAction(ProviderActions.HEALTH_CHECK))
                        .get(healthCheckRequestTimeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warn("unable to reach check endpoint. timeout after " + healthCheckRequestTimeoutMillis + " ms");
            } catch (ExecutionException  e) {
                logger.warn("unable to reach check endpoint. timeout after " + healthCheckRequestTimeoutMillis + " ms");
            } catch (TimeoutException e) {
                logger.warn("unable to reach check endpoint. timeout after " + healthCheckRequestTimeoutMillis + " ms");
            }

            if (status != ProviderStatus.IN_SERVICE && status != ProviderStatus.FULL_OF_SERVICE) {
                this.registry.deregisterProvider(provider);
                consecutiveSuccessesChecks = 0;
            } else {
                    consecutiveSuccessesChecks++;
                    if (consecutiveSuccessesChecks >= 2) {
                        try {
                            this.registry.registerProvider(provider);
                        } catch (RegistryOperationException roe) {
                            logger.error("Unable to re-register instance: ",roe.toString());
                            provider.closeProvider();
                            return;
                        }
                    }
            }
            try {
                Thread.sleep(healthCheckFrequencyMillis);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
