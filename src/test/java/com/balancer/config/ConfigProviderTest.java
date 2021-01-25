package com.balancer.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigProviderTest {
    @Test
    void testLoadConfig() {
        assertNotNull(ConfigProvider.getConfig());
        assertNotNull(ConfigProvider.getConfig().getLoadBalancer());
        assertNotNull(ConfigProvider.getConfig().getRegistry());
        assertTrue(ConfigProvider.getConfig() == ConfigProvider.getConfig());
    }
}