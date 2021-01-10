package com.max.app.redis.queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class RedisQueueMain {

    public static void main(String[] args) throws Exception {
        RedisQueue q = new RedisQueue();

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        Runnable task = () -> {
            String value = q.take();

            while (value != null) {
                System.out.println(value);
                value = q.take();
            }
        };

        pool.scheduleAtFixedRate(task, 0L, 1L, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);

        for (int i = 0; i < 10; ++i) {
            q.add(String.format("value:%d", i));
        }

        TimeUnit.SECONDS.sleep(30);

        pool.shutdownNow();

        System.out.printf("Main completed. java version: %s%n", System.getProperty("java.version"));
    }
}
