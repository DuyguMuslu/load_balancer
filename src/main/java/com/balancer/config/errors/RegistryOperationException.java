package com.balancer.config.errors;

public class RegistryOperationException extends Exception {

    private final String reason;

    public RegistryOperationException(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "RegistryOperationException{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
