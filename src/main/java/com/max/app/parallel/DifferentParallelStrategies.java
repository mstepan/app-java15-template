package com.max.app.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * JVM parameter to print memory when JVM process exited:
 * -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics
 */
public class DifferentParallelStrategies {

    public static void main(String[] args) throws Exception {

        double[] arr = randomArrayOfDoubles(100_000_000);

        long startTimeNanosParallel = System.nanoTime();
        int countParallel = countBelowZeroParallel(arr);
        long endTimeNanosParallel = System.nanoTime();

        long startTimeNanosSeq = System.nanoTime();
        int countSequential = countBelowZeroSequentially(arr);
        long endTimeNanosSeq = System.nanoTime();

        long startTimeNanosForkJoin = System.nanoTime();
        int countForkJoin = countBelowZeroForkJoinPool(arr);
        long endTimeNanosForkJoin = System.nanoTime();

        System.out.printf("time (parallel): %d ms %n", (endTimeNanosParallel - startTimeNanosParallel) / 1_000_000);
        System.out.printf("below zero count: %d%n%n", countParallel);

        System.out.printf("time (sequential): %d ms %n", (endTimeNanosSeq - startTimeNanosSeq) / 1_000_000);
        System.out.printf("below zero count: %d%n%n", countSequential);

        System.out.printf("time (fork-join): %d ms %n", (endTimeNanosForkJoin - startTimeNanosForkJoin) / 1_000_000);
        System.out.printf("below zero count: %d%n%n", countForkJoin);

        System.out.println("DifferentParallelStrategies done...");
    }

    private static final class Subtask extends RecursiveTask<Integer> {

        private static final int SEQUENTIAL_THRESHOLD = 47;

        private final double[] arr;
        private final int from;
        private final int to;

        public Subtask(double[] arr, int from, int to) {
            this.arr = arr;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Integer compute() {

            int elemsCount = to - from + 1;

            if (elemsCount <= SEQUENTIAL_THRESHOLD) {
                return countBelowZeroForSubarray(arr, from, to);
            }

            int middle = from + ((to - from) >>> 1);

            Subtask left = new Subtask(arr, from, middle);
            Subtask right = new Subtask(arr, middle + 1, to);

            left.fork();
            right.fork();

            return left.join() + right.join();
        }
    }

    private static int countBelowZeroForkJoinPool(double[] arr) throws Exception {

        ForkJoinPool pool = new ForkJoinPool();

        ForkJoinTask<Integer> task = pool.submit(new Subtask(arr, 0, arr.length - 1));

        int res = task.get();

        pool.shutdownNow();

        return res;
    }

    private static int countBelowZeroParallel(double[] arr) throws Exception {

        final int threadsCount = Runtime.getRuntime().availableProcessors();

        ExecutorService pool = Executors.newFixedThreadPool(threadsCount);

        List<Future<Integer>> results = new ArrayList<>(threadsCount);

        final int chunkSize = (arr.length / threadsCount) + 1;

        int lo = 0;
        int hi = chunkSize;

        for (int i = 0; i < threadsCount; ++i) {

            final int from = lo;
            final int to = hi - 1;

            Future<Integer> singleRes = pool.submit(() -> countBelowZeroForSubarray(arr, from, to));
            results.add(singleRes);

            lo = hi;
            hi = Math.min(hi + chunkSize, arr.length);
        }

        int totalCount = 0;
        for (Future<Integer> singleRes : results) {
            totalCount += singleRes.get();
        }


        pool.shutdownNow();

        return totalCount;
    }

    static int countBelowZeroSequentially(double[] arr) {
        return countBelowZeroForSubarray(arr, 0, arr.length - 1);
    }

    static int countBelowZeroForSubarray(double[] arr, int from, int to) {
        int count = 0;
        for (int i = from; i <= to; ++i) {
            if (Double.compare(arr[i], 0.0) < 0) {
                ++count;
            }
        }
        return count;
    }

    private static final Random RAND = new Random();

    private static double[] randomArrayOfDoubles(int length) {
        double[] arr = new double[length];

        for (int i = 0; i < arr.length; ++i) {
            double positiveVal = RAND.nextDouble();
            arr[i] = RAND.nextBoolean() ? positiveVal : -positiveVal;
        }

        return arr;
    }

}
