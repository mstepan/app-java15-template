package com.max.app.redis;

import com.max.app.redis.point.PointService;
import com.max.app.redis.point.XYPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public final class RedisMain {

    public static void main(String[] args) throws Exception {

        final String id = "CAFEBABE";

        final PointService mainPointService = new PointService();

        XYPoint point = new XYPoint(id, 1, 1);
        mainPointService.save(point);

        final int threadsCount = 100;

        ExecutorService pool = Executors.newCachedThreadPool();

        List<Callable<Void>> tasks = new ArrayList<>(threadsCount);

        for (int i = 0; i < threadsCount; ++i) {
            tasks.add(() -> {

                final ThreadLocalRandom rand = ThreadLocalRandom.current();
                final PointService pointService = new PointService();

                for (int it = 0; it < 100; ++it) {
                    int offset = rand.nextInt();

                    XYPoint p = pointService.getById(id);

                    if (!p.isValid()) {
                        System.out.printf("Invalid point detected: %s%n", p);
                    }
                    pointService.movePoint(id, p.x() + offset, p.y() + offset);
                }

                return null;
            });
        }

        List<Future<Void>> futures = pool.invokeAll(tasks);

        for (Future<Void> singleFuture : futures) {
            try {
                singleFuture.get();
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

        pool.shutdownNow();

        System.out.println("Main HERE");
        System.out.println(mainPointService.getById(id));

        System.out.printf("Main completed. java version: %s%n", System.getProperty("java.version"));
    }
}
