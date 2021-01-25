package com.balancer.providers;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Provider {

    String get();

    ProviderStatus getStatus();

    void checkProvider();

    boolean closeProvider();

    Future invokeProvider(Callable func);

}
