package math.factorial;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class FactorialMain {

    public static void main(String[] args) throws Exception {

        final int value = 200_000;

        BigInteger res1 = measureTime(FactorialMain::factorialParallel, "factorialParallel").apply(value);

        BigInteger res2 = measureTime(FactorialMain::factorialForkJoin, "factorialForkJoin").apply(value);

        BigInteger actualRes = measureTime(FactorialMain::factorial, "factorial sequential").apply(value);

        if (!res1.equals(actualRes)) {
            throw new IllegalStateException("res1 != actualRes for parallel");
        }

        if (!res2.equals(actualRes)) {
            throw new IllegalStateException("res2 != actualRes for fork-join");
        }


        System.out.printf("java version: %s. Main done...", System.getProperty("java.version"));
    }

    static <T, R> Function<T, R> measureTime(Function<T, R> function, String title) {
        return (value) -> {
            long start = System.nanoTime();

            R res = function.apply(value);

            long end = System.nanoTime();

            System.out.printf("[%s] time: %d ms%n", title, ((end - start) / 1_000_000));

            return res;
        };
    }


    static BigInteger factorialForkJoin(int n) {

        ForkJoinPool pool = new ForkJoinPool();

        final int cpus = Runtime.getRuntime().availableProcessors();
        final int base = 2;

        ForkJoinTask<BigInteger> result = pool.submit(new FactorialRecursiveTask(n, cpus, base, cpus));

        try {
            return result.get();
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException("Factorial fork-join calculation failed");
        }
    }

    private static final class FactorialRecursiveTask extends RecursiveTask<BigInteger> {

        private final int n;
        private final int step;
        private final int base;
        private final int cpus;

        public FactorialRecursiveTask(int n, int step, int base, int cpus) {
            this.n = n;
            this.step = step;
            this.base = base;
            this.cpus = cpus;
        }

        @Override
        protected BigInteger compute() {

            if (cpus == 1) {
                BigInteger res = BigInteger.ONE;
                for (int value = base; value <= n; value += step) {
                    res = res.multiply(BigInteger.valueOf(value));
                }

                return res;
            }

            final int leftCpus = cpus / 2;
            final int rightCpus = cpus - leftCpus;

            FactorialRecursiveTask leftSide = new FactorialRecursiveTask(n, step, base, leftCpus);

            FactorialRecursiveTask rightSide = new FactorialRecursiveTask(n, step, base + leftCpus, rightCpus);

            ForkJoinTask<BigInteger> rightSideTask = rightSide.fork();

            BigInteger leftSideValue = leftSide.compute();

            try {
                return leftSideValue.multiply(rightSideTask.get());
            }
            catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            }

        }
    }

    static BigInteger factorialParallel(int n) {

        final int cpus = Runtime.getRuntime().availableProcessors();
        final int base = 2;
        ExecutorService pool = Executors.newFixedThreadPool(cpus);

        List<Callable<BigInteger>> tasksForExecution = new ArrayList<>();
        for (int i = 0; i < cpus; ++i) {
            final int taskNo = i;

            tasksForExecution.add(() -> {
                BigInteger curRes = BigInteger.ONE;
                for (int number = base + taskNo; number <= n && !Thread.currentThread().isInterrupted(); number += cpus) {
                    curRes = curRes.multiply(BigInteger.valueOf(number));
                }
                return curRes;
            });
        }

        BigInteger totalRes = BigInteger.ONE;
        try {
            List<Future<BigInteger>> futureResults = pool.invokeAll(tasksForExecution);
            for (Future<BigInteger> singleRes : futureResults) {
                totalRes = totalRes.multiply(singleRes.get());
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException("Calculatiom failed", ex);
        }

        pool.shutdownNow();
        return totalRes;
    }


    private static final int FAC_CONSTANT_VALUE_THRESHOLD = 1;
    private static final int FAC_LONG_VALUE_THRESHOLD = 20;

    static BigInteger factorial(int n) {
        checkArgument(n >= 0, "Can't calculate factorial for negative value: " + n);

        // 0! = 1
        // 1! = 1
        if (n <= FAC_CONSTANT_VALUE_THRESHOLD) {
            return BigInteger.ONE;
        }

        // long can hold factorial value up to 20!
        if (n <= FAC_LONG_VALUE_THRESHOLD) {
            return BigInteger.valueOf(factorialLong(n));
        }

        BigInteger res = BigInteger.valueOf(factorialLong(FAC_LONG_VALUE_THRESHOLD));

        for (int i = FAC_LONG_VALUE_THRESHOLD + 1; i <= n; ++i) {
            res = res.multiply(BigInteger.valueOf(i));
        }

        return res;
    }

    private static long factorialLong(int val) {
        assert val > 1;

        long res = 1L;

        for (int i = 2; i <= val; ++i) {
            res *= i;
        }

        return res;
    }

    private static void checkArgument(boolean condition, String errorMsg) {
        if (!condition) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

}
