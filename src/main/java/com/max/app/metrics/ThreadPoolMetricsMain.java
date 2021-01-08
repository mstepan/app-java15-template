package com.max.app.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPoolMetricsMain {

    private static final Histogram asyncTaskDuration = Histogram.build().
            buckets(0.1, 0.25, 0.5, 1.0, 1.5, 2.0, 5.0, 10.0, 15.0, 30.0).
            name("async_task_duration_seconds").
            help("Async task duration in seconds.").
            register();

    private static final Gauge utilizationRatio = Gauge.build().
            name("thread_pool_utilization_ratio").
            help("Thread pool utilization ratio.").
            register();

    private static final Counter rejectedTasks = Counter.build().
            name("async_rejected_tasks_count").
            help("Count of rejected async tasks.").
            register();

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    public static void main(String[] args) throws Exception {

        final BlockingQueueWithMetrics workingQueue = new BlockingQueueWithMetrics(new ArrayBlockingQueue<>(5));

        final int threadsCount = 20;
        final CountDownLatch allCompleted = new CountDownLatch(threadsCount);

        final RejectedExecutionHandler rejectionHandler = (Runnable r, ThreadPoolExecutor executor) -> {
            rejectedTasks.inc();
            throw new RejectedExecutionException("Task rejected, capacity is full.");
        };

        final int corePoolSize = 5;
        final int maxPoolSize = 10;
        ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 10, TimeUnit.SECONDS,
                                                         workingQueue, rejectionHandler);

        for (int i = 0; i < threadsCount; ++i) {
            try {

                // Queue size
                //workingQueue.printMetrics();

                pool.execute(() -> {

                    Histogram.Timer durationTimer = asyncTaskDuration.startTimer();

                    utilizationRatio.set((double) pool.getActiveCount() / pool.getMaximumPoolSize());

                    try {

                        // Thread pool utilization ratio
                        //System.out.printf("utilization: %.2f%n", utilizationRatio.get());

                        long delay = 1000L + RAND.nextInt(1000);
                        TimeUnit.MILLISECONDS.sleep(delay);
                    }
                    catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    finally {
                        allCompleted.countDown();
                        durationTimer.observeDuration();
                    }
                });
            }
            catch (RejectedExecutionException ex) {
                allCompleted.countDown();
            }
        }

        allCompleted.await();
        pool.shutdownNow();

        // Rejected tasks counter
        //System.out.printf("Rejected tasks count: %.0f%n", rejectedTasks.get());

        // Duration histogram, buckets [5ms, 10ms, 25ms, 50ms, .....,  10sec]
        for (Collector.MetricFamilySamples sampleCol : asyncTaskDuration.collect()) {

            for(Collector.MetricFamilySamples.Sample sample : sampleCol.samples ){
                System.out.println(sample.toString());
            }
        }

        System.out.printf("ThreadPoolMetricsMain completed. java version: %s%n", System.getProperty("java.version"));
    }


}
