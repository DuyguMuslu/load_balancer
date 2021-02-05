package com.balancer.config;


import com.balancer.loadbalancer.InvocationType;

public class LoadBalancerConfig {

    private InvocationType invocationType;

    private int maxConcurrentWorkersPerProvider;

    public InvocationType getInvocationType() {
        return invocationType;
    }

    public void setInvocationType(InvocationType invocationType) {
        this.invocationType = invocationType;
    }

    public int getMaxConcurrentWorkersPerProvider() {
        return maxConcurrentWorkersPerProvider;
    }

    public void setMaxConcurrentWorkersPerProvider(int maxConcurrentWorkersPerProvider) {
        this.maxConcurrentWorkersPerProvider = maxConcurrentWorkersPerProvider;
    }
}
