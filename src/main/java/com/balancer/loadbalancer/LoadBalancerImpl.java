package com.balancer.loadbalancer;


import com.balancer.config.errors.ServiceUnavailableException;
import com.balancer.core.registration.ProviderRegistry;
import com.balancer.providers.Provider;
import com.balancer.core.ProviderActionFacade;
import com.balancer.core.ProviderActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancerImpl implements LoadBalancer {

    private final ProviderRegistry registry;

    private InvocationType invocationType;

    private final AtomicInteger currentProviderIndex = new AtomicInteger();

    private Map<String, Integer> threadDistribution;

    public LoadBalancerImpl(ProviderRegistry registry, InvocationType invocationType) {
        threadDistribution = new HashMap<>();
        this.registry = registry;
        this.invocationType = invocationType;
    }

    @Override
    public Future get() throws ServiceUnavailableException {
        Provider selected = selectNextAvailableProvider();
        String providerUuid = selected.get();
        if (threadDistribution.get(providerUuid) == null) {
            threadDistribution.put(providerUuid, 1);
        } else {
            threadDistribution.put(providerUuid, threadDistribution.get(providerUuid) + 1);
        }
        return selected.invokeProvider(new ProviderActionFacade(selected).getProviderAction(ProviderActions.GET));
    }

    private Provider selectNextAvailableProvider() throws ServiceUnavailableException {
        List<Provider> providers = registry.getAvailableProviders();
        if (providers.size() == 0) {
            throw new ServiceUnavailableException();
        }
        synchronized (providers) {
            Provider selected;
            switch (invocationType) {
                case ROUND_ROBIN: {
                    if (currentProviderIndex.get() >= providers.size()) {
                        currentProviderIndex.set(0);
                    }
                    selected = providers.get(currentProviderIndex.get());
                    currentProviderIndex.incrementAndGet();
                    break;
                }
                default: { // RANDOM
                    Random rand = new Random();
                    selected = providers.get(rand.nextInt(providers.size()));
                }
            }
            return selected;
        }
    }

    @Override
    public ProviderRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public Map<String, Integer> getDistribution() {
        return this.threadDistribution;
    }

    @Override
    public void clearDistribution() {
        this.threadDistribution.clear();
    }

    @Override
    public void setInvocationType(InvocationType invocationType) {
        this.invocationType = invocationType;
    }

    @Override
    public InvocationType getInvocationType() {
        return invocationType;
    }
}
