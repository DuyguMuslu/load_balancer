package com.balancer.core;

import com.balancer.providers.Provider;
import com.balancer.providers.ProviderStatus;

import java.util.concurrent.Callable;

public class ProviderActionFacade {

    private final Provider provider;

    public ProviderActionFacade(Provider provider) {
        this.provider = provider;
    }

    public Callable getProviderAction(ProviderActions action) {
        switch (action) {
            case HEALTH_CHECK:
                return actionCheckStatus();
            default:
                return actionGet();
        }
    }

    private Callable<ProviderStatus> actionCheckStatus(){
        return () -> provider.getStatus();
    }


    private Callable<String> actionGet() {
        return () -> provider.get();
    }
}
