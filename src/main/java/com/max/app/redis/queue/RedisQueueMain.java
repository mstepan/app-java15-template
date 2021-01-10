package com.max.app.redis.queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class RedisQueueMain {

    public static void main(String[] args) throws Exception {
        RedisQueue q = new RedisQueue();

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        Runnable task = () -> {
            String value = q.peek();

            while (value != null) {
                System.out.println(value);
                value = q.peek();
            }
        };

        pool.scheduleAtFixedRate(task, 0L, 1L, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);

        for (int i = 0; i < 10; ++i) {
            q.add(String.format("value:%d", i));
        }

        TimeUnit.SECONDS.sleep(10);

        pool.shutdownNow();

        System.out.printf("RedisQueueMain completed. java version: %s%n", System.getProperty("java.version"));
    }
}
