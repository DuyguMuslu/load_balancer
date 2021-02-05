package com.balancer.core.registration;

import com.balancer.providers.ProviderImpl;
import com.balancer.providers.ProviderStatus;

public class ProviderMock extends ProviderImpl {

    public ProviderMock(ProviderStatusListener providerStatusListener) {
        super(providerStatusListener);
    }


    public void setStatus(ProviderStatus status) {
        super.status = status;
    }

}
