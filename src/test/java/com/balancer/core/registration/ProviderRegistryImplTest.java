package com.balancer.core.registration;

import com.balancer.config.ConfigProvider;
import com.balancer.config.RegistryConfig;
import com.balancer.config.errors.RegistryOperationException;
import com.balancer.providers.Provider;
import com.balancer.providers.ProviderImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderRegistryImplTest {

    @Test
    void testProviderRegistryAddRemove() throws RegistryOperationException {

        ProviderRegistryImpl registry = new ProviderRegistryImpl(ConfigProvider.getConfig().getRegistry());

        Provider p1 = new ProviderMock(null);
        Provider p2 = new ProviderMock(null);

        registry.registerProvider(p1);
        registry.registerProvider(p2);

        assertEquals(2, registry.getAvailableProviders().size());

        registry.deregisterProvider(p1);

        assertEquals(1, registry.getAvailableProviders().size());
        assertEquals(p2, registry.getAvailableProviders().get(0));

        registry.registerProvider(p1);
        assertEquals(2, registry.getAvailableProviders().size());

        assertThrows(RegistryOperationException.class, () -> {
            registry.registerProvider(p2);
        });
        assertEquals(2, registry.getAvailableProviders().size());
    }

    @Test
    void testProviderRegistryMaxLimitReached() {
        RegistryConfig config = ConfigProvider.getConfig().getRegistry();
        config.setMaxAllowedRegisteredProviders(10);

        ProviderRegistryImpl registry = new ProviderRegistryImpl(config);

        for (int i = 0; i < 10; i++) {
            Provider provider = new ProviderImpl(registry);
            try {
                registry.registerProvider(provider);
            } catch (RegistryOperationException roe) {
                fail("Unexpected exception occured " + roe);
            }
        }

        assertEquals(10, registry.getAvailableProviders().size());

        assertThrows(RegistryOperationException.class, () -> {
            registry.registerProvider(new ProviderImpl(registry));
        });

    }
}