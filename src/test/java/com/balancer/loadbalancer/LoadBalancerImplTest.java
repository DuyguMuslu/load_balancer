package com.balancer.loadbalancer;

import com.balancer.config.ConfigProvider;
import com.balancer.config.RegistryConfig;
import com.balancer.core.LoadBalancerBuilder;
import com.balancer.providers.Provider;
import com.balancer.providers.ProviderStatus;
import com.balancer.core.registration.ProviderRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerImplTest {

    @Test
    void testDefaultLoadBalancerBuild() {
        LoadBalancerImpl loadBalancer = (LoadBalancerImpl) new LoadBalancerBuilder().build();

        assertEquals(ConfigProvider.getConfig().getLoadBalancer().getDefaultStrategy(), loadBalancer.getInvocationType());
    }

    @Test
    void testCustomStrategyLoadBalancerBuild() {
        LoadBalancerImpl loadBalancer = (LoadBalancerImpl) new LoadBalancerBuilder().withBalancingStrategy(InvocationType.ROUND_ROBIN).build();

        assertEquals(InvocationType.ROUND_ROBIN, loadBalancer.getInvocationType());
    }

    @Test
    void testCustomRegistryLoadBalancerBuild() {

        ProviderRegistry testProviderRegistry = new ProviderRegistry() {
            @Override
            public void providerStatusChanged(Provider provider, ProviderStatus newStatus) {

            }

            @Override
            public void registerProvider(Provider provider) {
            }

            @Override
            public void deregisterProvider(Provider provider) {
            }

            @Override
            public List<Provider> getAvailableProviders() {
                return null;
            }

            @Override
            public RegistryConfig getCurrentConfig() {
                return null;
            }

        };

        LoadBalancerImpl loadBalancer = (LoadBalancerImpl) new LoadBalancerBuilder().withRegistry(testProviderRegistry).build();

        assertTrue(loadBalancer.getRegistry() == testProviderRegistry);
    }

}