package com.max.app.geeks;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * https://www.geeksforgeeks.org/check-if-every-pair-in-array-b-follows-the-same-relation-as-their-corresponding-values-in-a/
 * <p>
 * Check if every pair in array B follows the same relation as their corresponding values in array A.
 * <p>
 * Given two arrays A[] and B[] each of size N, the task is to check if the given arrays are valid or not, based on the
 * following conditions:
 * <p>
 * 1. Every element in A at index i, will be mapped with the element in B at the same index only, i.e. (A[i] can be
 * mapped with B[i] only)
 * 2, For any pair in A (A[i], A[j]), if A[i] > A[j], then its corresponding value in B should also be greater,
 * i.e. B[i] > B[j] should be true.
 * 3. For any pair in A (A[i], A[j]), if A[i] == A[j], then its corresponding value in B should also be equal,
 * i.e. B[i] == B[j] should be true.
 */
public final class CheckCorrespondingArrayValid {

    private CheckCorrespondingArrayValid() {
        throw new AssertionError("Can't instantiate utility-only class.");
    }

    public static void main(String[] args) throws Exception {

        final int[] a = generateArray(10_000);
        Arrays.sort(a);

//        final int[] b = addToEachElement(Arrays.copyOf(a, a.length), 10);
        final int[] b = generateArray(10_000);

        System.out.printf("brute-force fork-join %b%n", isValidCorrespondingArrayBruteforceForkJoin(a, b));
        System.out.printf("brute-force parallel %b%n", isValidCorrespondingArrayBruteforceParallel(a, b));
        System.out.printf("brute-force: %b%n", isValidCorrespondingArrayBruteforce(a, b));
        System.out.printf("optimal: %b%n", isValidCorrespondingArrayOptimal(a, b));

        System.out.println("Maine done...");
    }

    private static int[] addToEachElement(int[] arr, int valueToAdd) {
        for (int i = 0; i < arr.length; ++i) {
            arr[i] += valueToAdd;
        }

        return arr;
    }

    private static final Random RAND = ThreadLocalRandom.current();

    private static int[] generateArray(int length) {
        int[] arr = new int[length];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt(100);
        }
        return arr;
    }

    /**
     * time: O(N^2)
     * space: O(1)
     */
    public static boolean isValidCorrespondingArrayBruteforceForkJoin(int[] a, int[] b) {
        checkPreconditions(a, b);

        ForkJoinPool pool = new ForkJoinPool();

        System.out.printf("fork-join pool parallelism %d%n", pool.getParallelism());

        return pool.invoke(new SingleForkJoinTask(a, b, 0, a.length - 1));
    }

    private static final class SingleForkJoinTask extends RecursiveTask<Boolean> {

        final int[] a;
        final int[] b;

        // 'from' and 'to' are used as closed intervals
        final int from;
        final int to;

        public SingleForkJoinTask(int[] a, int[] b, int from, int to) {
            this.a = a;
            this.b = b;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Boolean compute() {

            final int elemsCount = to - from + 1;

            if (elemsCount < 100) {
                return executeMainFlow();
            }

            int mid = from + (to - from) / 2;

            // left should be created and forked first
            SingleForkJoinTask left = new SingleForkJoinTask(a, b, from, mid);
            left.fork();

            SingleForkJoinTask right = new SingleForkJoinTask(a, b, mid + 1, to);

            // do not change below lines order, right.compute() should be execute first
            final boolean rightRes = right.compute();
            final boolean leftRes = left.join();

            return leftRes && rightRes;
        }

        private boolean executeMainFlow() {
            // time O(N^2)
            for (int i = from; i < to; ++i) {
                for (int j = i + 1; j < a.length; ++j) {
                    // 1-case: a[i] == a[j]
                    if (a[i] == a[j]) {
                        if (b[i] != b[j]) {
                            return false;
                        }
                    }
                    // 2-case: a[i] > a[j]
                    else if (a[i] > a[j]) {
                        // IMPORTANT: use '<=' below
                        if (b[i] <= b[j]) {
                            return false;
                        }
                    }
                    // 3-case: a[i] < a[j]
                    else {
                        // IMPORTANT: use '>=' below
                        if (b[i] >= b[j]) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }


    private static void checkPreconditions(int[] a, int[] b) {
        checkNotNull(a, "Array 'a' is NULL");
        checkNotNull(b, "Array 'b' is NULL");
        checkArgument(a.length == b.length, "Arrays length aren't equal");
    }

    /**
     * time: O(N^2)
     * space: O(1)
     */
    public static boolean isValidCorrespondingArrayBruteforceParallel(int[] a, int[] b) throws InterruptedException,
        ExecutionException {
        checkPreconditions(a, b);

        final int threadsCount = Math.min(Runtime.getRuntime().availableProcessors(), a.length);
        ExecutorService pool = Executors.newFixedThreadPool(threadsCount);

        try {

            List<Callable<Boolean>> tasks = new ArrayList<>(threadsCount);
            for (int taskIndex = 0; taskIndex < threadsCount; ++taskIndex) {
                tasks.add(new SingleTask(a, b, taskIndex, threadsCount));
            }

            List<Future<Boolean>> allResults = pool.invokeAll(tasks);

            boolean combinedResult = true;
            for (Future<Boolean> singleRes : allResults) {
                combinedResult = combinedResult && singleRes.get();
            }
            return combinedResult;
        }
        finally {
            pool.shutdownNow();
            pool.awaitTermination(1L, TimeUnit.SECONDS);
        }
    }

    private static final class SingleTask implements Callable<Boolean> {

        final int[] a;
        final int[] b;
        final int thIndex;
        final int threadsCount;

        public SingleTask(int[] a, int[] b, int thIndex, int threadsCount) {
            this.a = a;
            this.b = b;
            this.thIndex = thIndex;
            this.threadsCount = threadsCount;
        }

        @Override
        public Boolean call() {
//            System.out.printf("Thread started: %d%n", Thread.currentThread().getId());

            try {
                // time O(N^2)
                for (int i = thIndex; i < a.length - 1; i += threadsCount) {
                    for (int j = i + 1; j < a.length; ++j) {
                        // 1-case: a[i] == a[j]
                        if (a[i] == a[j]) {
                            if (b[i] != b[j]) {
                                return false;
                            }
                        }
                        // 2-case: a[i] > a[j]
                        else if (a[i] > a[j]) {
                            // IMPORTANT: use '<=' below
                            if (b[i] <= b[j]) {
                                return false;
                            }
                        }
                        // 3-case: a[i] < a[j]
                        else {
                            // IMPORTANT: use '>=' below
                            if (b[i] >= b[j]) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            finally {
//                System.out.printf("Thread completed: %d%n", Thread.currentThread().getId());
            }
        }
    }

    /**
     * time: O(N^2)
     * space: O(1)
     */
    public static boolean isValidCorrespondingArrayBruteforce(int[] a, int[] b) {
        checkPreconditions(a, b);

        // time O(N^2)
        for (int i = 0; i < a.length - 1; ++i) {
            for (int j = i + 1; j < a.length; ++j) {
                // 1-case: a[i] == a[j]
                if (a[i] == a[j]) {
                    if (b[i] != b[j]) {
                        return false;
                    }
                }
                // 2-case: a[i] > a[j]
                else if (a[i] > a[j]) {
                    // IMPORTANT: use '<=' below
                    if (b[i] <= b[j]) {
                        return false;
                    }
                }
                // 3-case: a[i] < a[j]
                else {
                    // IMPORTANT: use '>=' below
                    if (b[i] >= b[j]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static final int PARALLEL_SORT_THRESHOLD = 1_000_000;

    /**
     * time: O(N*lgN)
     * space: O(N)
     */
    public static boolean isValidCorrespondingArrayOptimal(int[] a, int[] b) {
        checkPreconditions(a, b);

        // space O(N)
        Pair[] pairsArr = Pair.toPairs(a, b);

        // time N*lgN
        if (pairsArr.length >= PARALLEL_SORT_THRESHOLD) {
            Arrays.parallelSort(pairsArr, Pair.FIRST_ASC);
        }
        else {
            Arrays.sort(pairsArr, Pair.FIRST_ASC);
        }

        // time O(N)
        for (int i = 1; i < pairsArr.length; ++i) {
            Pair prev = pairsArr[i - 1];
            Pair cur = pairsArr[i];

            // 1-case: a[i-1] == a[i], check b[i-1] == b[i]
            if (cur.first == prev.first) {
                if (prev.second != cur.second) {
                    return false;
                }
            }
            // 2-case: a[i-1] < a[i], check b[i-1] < b[i]
            else {
                // IMPORTANT: use '>=' below
                if (prev.second >= cur.second) {
                    return false;
                }
            }
            // we don't have 3-rd case here as in brute-force solution, b/c array is sorted by Pair.first ASC
        }

        return true;
    }

    record Pair(int first, int second) {

        static Comparator<Pair> FIRST_ASC = Comparator.comparing(Pair::first);

        static Pair[] toPairs(int[] a, int[] b) {
            assert a.length == b.length : "Can't combine to array of pairs";

            Pair[] arr = new Pair[a.length];

            for (int i = 0; i < a.length; ++i) {
                arr[i] = new Pair(a[i], b[i]);
            }

            return arr;
        }
    }

}
