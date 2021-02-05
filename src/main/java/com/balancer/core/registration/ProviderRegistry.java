package com.balancer.core.registration;

import com.balancer.config.RegistryConfig;
import com.balancer.config.errors.RegistryOperationException;
import com.balancer.providers.Provider;

import java.util.List;

public interface ProviderRegistry extends ProviderStatusListener {

    void registerProvider(Provider provider) throws RegistryOperationException;

    void deregisterProvider(Provider provider);

    List<Provider> getAvailableProviders();

    RegistryConfig getCurrentConfig();
}
