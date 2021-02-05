package com.balancer.core;

import com.balancer.config.ApplicationConfig;
import com.balancer.config.ConfigProvider;
import com.balancer.loadbalancer.*;
import com.balancer.core.registration.ProviderRegistry;
import com.balancer.core.registration.ProviderRegistryImpl;

public class LoadBalancerBuilder {

    private ProviderRegistry registry;

    private InvocationType invocationType;

    public LoadBalancerBuilder() {
        ApplicationConfig config = ConfigProvider.getConfig();
        this.invocationType = config.getLoadBalancer().getInvocationType();
        this.registry = new ProviderRegistryImpl(config.getRegistry());
    }

    public LoadBalancerBuilder withBalancingInvocationType(InvocationType invocationType) {
        this.invocationType = invocationType;
        return this;
    }

    public LoadBalancerBuilder withRegistry(ProviderRegistry registry) {
        this.registry = registry;
        return this;
    }

    public LoadBalancer build() {
        return new LoadBalancerImpl(this.registry, this.invocationType);
    }
}
