package com.max.app;

import com.max.app.concurrency.ReusableLatch;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Main {

    private static final class StatHolder {
        int count;
    }

    public static void main(String[] args) throws Exception {

        final int waitThreadsCount = 1;
        final int threadsCount = 10;

        StatHolder res = new StatHolder();
        Lock lock = new ReentrantLock();

        final ReusableLatch allCompleted = new ReusableLatch(threadsCount);

        for (int it = 0; it < 10; ++it) {
            Thread[] threads = new Thread[threadsCount];
            for (int i = 0; i < threadsCount; ++i) {
                threads[i] = new Thread(() -> {
                    try {
                        for (int val = 0; val < 1_000; ++val) {
                            lock.lock();
                            try {
                                res.count += 1;
                            }
                            finally {
                                lock.unlock();
                            }
                        }
                    }
                    finally {
                        allCompleted.countDown();
                    }
                });
            }

            for (Thread singleThread : threads) {
                singleThread.start();
            }

            for (int waitCnt = 0; waitCnt < waitThreadsCount; ++waitCnt) {
                Thread th = new Thread(() -> {
                    allCompleted.await();
                    System.out.printf("[%s] completed, res = %d%n",
                                      Thread.currentThread().getName(),
                                      res.count);
                });
                th.setName("waiter-" + waitCnt);
                th.start();
            }

            allCompleted.await();

            System.out.printf("[%s] completed, it: %d, value: %d%n",
                              Thread.currentThread().getName(), it, res.count);
        }

        System.out.printf("java version: %s%n", System.getProperty("java.version"));
    }

    /**
     * 5.10. Generate uniform random numbers in range.
     */
    private static int randomInRange(int from, int to) {
        checkArgument(from <= to, String.format("from > to: %d > %d", from, to));

        // only 1 value possible
        if (from == to) {
            return from;
        }

        // 2 values possible
        if (to - from == 1) {
            return (randomBit() == 0) ? from : to;
        }

        final int upperBoundary = (to - from);
        final int bitsCount = (int) log2(upperBoundary) + 1;

        int randomValue = Integer.MAX_VALUE;

        while (randomValue > upperBoundary) {
            randomValue = generateRandomBits(bitsCount);
        }

        return from + randomValue;
    }

    private static double log2(double value) {
        return Math.log10(value) / Math.log10(2.0);
    }

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private static int randomBit() {
        return RAND.nextBoolean() ? 1 : 0;
    }

    private static int generateRandomBits(int bitsCount) {
        assert bitsCount <= Integer.SIZE;

        int res = 0;

        for (int i = 0; i < bitsCount; ++i) {
            res = (res << 1) | randomBit();
        }

        return res;
    }

    private static void checkArgument(boolean exp, String errorMsg) {
        if (!exp) {
            throw new IllegalArgumentException(errorMsg);
        }
    }


}
