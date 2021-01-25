package com.balancer.providers;

import java.util.concurrent.*;

class ProviderThreadPoolExecutor extends ThreadPoolExecutor {

    private ThreadExecutionListener threadExecutionListener;

    public ProviderThreadPoolExecutor(int poolSize, ThreadExecutionListener threadExecutionListener) {
        super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.threadExecutionListener = threadExecutionListener;
    }

    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                ((Future<?>) r).get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            threadExecutionListener.taskCompleted();
        }
        if (t != null)
            t.printStackTrace();
    }
}