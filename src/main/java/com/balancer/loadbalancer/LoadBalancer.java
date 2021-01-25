package com.balancer.loadbalancer;


import com.balancer.config.errors.ServiceUnavailableException;
import com.balancer.core.registration.ProviderRegistry;

import java.util.Map;
import java.util.concurrent.Future;

public interface LoadBalancer {

    Future get() throws ServiceUnavailableException;

    ProviderRegistry getRegistry();

    void setInvocationType(InvocationType invocationType);

    InvocationType getInvocationType();

    Map<String, Integer> getDistribution();

    void clearDistribution();
}
