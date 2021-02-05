package com.balancer.core.registration;

import com.balancer.config.RegistryConfig;
import com.balancer.config.errors.RegistryOperationException;
import com.balancer.providers.Provider;
import com.balancer.providers.ProviderStatus;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProviderRegistryImpl implements ProviderRegistry {


    private final ExecutorService checkThreadPool = Executors.newCachedThreadPool();

    private final List<Provider> availableProviders;

    private final RegistryConfig registryConfig;

    public ProviderRegistryImpl(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        this.availableProviders = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public synchronized void registerProvider(Provider provider) throws RegistryOperationException {
        if (availableProviders.size() >= registryConfig.getMaxAllowedRegisteredProviders()) {
            throw new RegistryOperationException("Maximum providers limit reached");
        }

        if (availableProviders.contains(provider)) {
            throw new RegistryOperationException("Provider already registered");
        }

        availableProviders.add(provider);

        new ProviderHealthChecker(checkThreadPool, this, provider).start();
    }

    @Override
    public synchronized void deregisterProvider(Provider provider) {
        availableProviders.remove(provider);
    }

    @Override
    public List<Provider> getAvailableProviders() {
        return Collections.unmodifiableList(availableProviders);
    }

    @Override
    public RegistryConfig getCurrentConfig() {
        return registryConfig;
    }

    @Override
    public void providerStatusChanged(Provider provider, ProviderStatus newStatus) {
        if (newStatus == ProviderStatus.FULL_OF_SERVICE || newStatus == ProviderStatus.OUT_OF_SERVICE) {
            deregisterProvider(provider);
            return;
        }
        if (newStatus == ProviderStatus.IN_SERVICE) {
            availableProviders.add(provider);
            return;
        }
    }
}
