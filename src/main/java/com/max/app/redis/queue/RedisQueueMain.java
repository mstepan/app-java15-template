package com.max.app.redis.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class RedisQueueMain {

    public static void main(String[] args) throws Exception {
        RedisQueue q = new RedisQueue();

//        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
//        Runnable task = () -> {
//            String value = q.peek();
//
//            while (value != null) {
//                System.out.println(value);
//                value = q.peek();
//            }
//        };
//
//        pool.scheduleAtFixedRate(task, 0L, 1L, TimeUnit.SECONDS);

        final int threadsCount = 10;

        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < threadsCount; ++i) {
            pool.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String value = q.takeReliable();

                        if (value.equals("value:4")) {
                            throw new IllegalStateException("Emulated exception");
                        }

                        System.out.printf("Thread-%d: received value %s%n", Thread.currentThread().getId(), value);
                        q.removeFromBackup(value);
                    }
                    catch (Exception ex) {
                        System.out.printf("Thread-%d: Exception: %s%n", Thread.currentThread().getId(), ex.getMessage());
                    }
                }
            });
        }

        TimeUnit.SECONDS.sleep(5);

        for (int i = 0; i < 10; ++i) {
            q.add(String.format("value:%d", i));
        }

        TimeUnit.SECONDS.sleep(5);

        pool.shutdown();
        pool.awaitTermination(3L, TimeUnit.SECONDS);

        System.out.printf("RedisQueueMain completed. java version: %s%n", System.getProperty("java.version"));
    }
}
