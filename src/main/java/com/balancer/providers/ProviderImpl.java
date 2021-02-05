package com.balancer.providers;

import com.balancer.config.ConfigProvider;
import com.balancer.core.ProviderStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ProviderImpl implements Provider, ThreadExecutionListener {

    private ProviderStatusListener providerStatusListener;

    private ProviderThreadPoolExecutor executor;

    protected ProviderStatus status;

    protected final UUID uuid;

    private Logger logger = LoggerFactory.getLogger(ProviderImpl.class);

    public ProviderImpl( ProviderStatusListener providerStatusListener) {
        this.uuid=UUID.randomUUID();
        this.providerStatusListener=providerStatusListener;
        status = ProviderStatus.IN_SERVICE;
        executor = new ProviderThreadPoolExecutor(ConfigProvider.getConfig().getLoadBalancer().getMaxConcurrentWorkersPerProvider(), this);
        logger.warn("new provider instance " + this.uuid.toString());
    }

    @Override
    public String get() {
        return uuid.toString();
    }

    @Override
    public ProviderStatus getStatus() {
        return this.status;
    }


    @Override
    public void checkProvider() {
        if (this.status == ProviderStatus.FULL_OF_SERVICE) {
            providerStatusListener.providerStatusChanged(this, ProviderStatus.IN_SERVICE);
        }
    }

    @Override
    public boolean closeProvider() {
        logger.info("closer service : uid=" + uuid.toString());
        providerStatusListener.providerStatusChanged(this, ProviderStatus.OUT_OF_SERVICE);
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            logger.info("provider closed properly: uid=" + this.uuid.toString());
        } catch (InterruptedException e) {
            logger.warn("provider close service error: " + e.toString());
            return false;
        }
        return true;
    }

    @Override
    public Future invokeProvider(Callable func) {
        Future invocation = executor.submit(func);
        if (this.status == ProviderStatus.IN_SERVICE && (executor.getTaskCount() - executor.getCompletedTaskCount()) >= executor.getMaximumPoolSize()) {
            logger.info("Reached max numbers of threads for " + uuid.toString() + ". switch to full status");
            this.status = ProviderStatus.FULL_OF_SERVICE;
            providerStatusListener.providerStatusChanged(this, status);
        }
        return invocation;
    }

    @Override
    public void taskCompleted() {
        if (this.status == ProviderStatus.FULL_OF_SERVICE) {
            providerStatusListener.providerStatusChanged(this, ProviderStatus.IN_SERVICE);
        }

    }
}
