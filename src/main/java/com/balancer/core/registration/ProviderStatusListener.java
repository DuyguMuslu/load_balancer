package com.balancer.core.registration;

import com.balancer.providers.Provider;
import com.balancer.providers.ProviderStatus;

public interface ProviderStatusListener {
    void providerStatusChanged(Provider provider, ProviderStatus newStatus);
}
