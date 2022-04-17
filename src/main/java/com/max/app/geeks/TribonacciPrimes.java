package com.max.app.geeks;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * https://www.geeksforgeeks.org/find-primes-in-range-1-n-which-also-belongs-to-a-tribonacci-series/
 * <p>
 * Find primes in range [1, N] which also belongs to a Tribonacci series
 * <p>
 * Given a number N, the task is to find the primes within range [1, N] which is also a part of a Tribonacci series starting
 * with {0, 0, 1}.
 * <p>
 * Note: A Tribonacci series is a series in which the next term is the sum of the previous three terms.
 */
public class TribonacciPrimes {

    public static void main(String[] args) {

        System.out.println(findPrimesThatIsAlsoTribonacciWithCaching(200));

        System.out.println("TribonacciPrimes done...");
    }

    public static List<Integer> findPrimesThatIsAlsoTribonacciWithCaching(int boundary) {
        checkArgument(boundary >= 0, "Negative boundary detected");

        if (boundary < CACHE_SIZE) {
            return new ArrayList<>(CACHE.get(boundary));
        }

        return findPrimesThatIsAlsoTribonacci(boundary);
    }

    private static final int CACHE_SIZE = 256;
    private static final List<List<Integer>> CACHE = new ArrayList<>(CACHE_SIZE);

    static {
        for (int i = 0; i < CACHE_SIZE; ++i) {
            CACHE.add(findPrimesThatIsAlsoTribonacci(i));
        }
    }

    /**
     * Time & space complexity proportional to the sieve algorithm.
     */
    private static List<Integer> findPrimesThatIsAlsoTribonacci(int boundary) {

        final List<Integer> res = new ArrayList<>();

        // calculate all primes using sieve of Eratosthenes
        BitSet primes = sievePrimes(boundary);

        int a = 0;
        int b = 0;
        int c = 1;

        // calculate Tribonacci numbers one by one and check if a number is also prime
        while (c <= boundary) {
            if (primes.get(c)) {
                res.add(c);
            }

            int next = a + b + c;
            a = b;
            b = c;
            c = next;
        }

        return res;
    }

    /**
     * time: (N* lglgN)
     * space: O(N/8)
     */
    static BitSet sievePrimes(int n) {

        final int lastPossibleCompositeValue = ((int) Math.sqrt(n)) + 1;

        BitSet primes = new BitSet(n + 1);
        primes.set(0, n + 1);

        // 0 and 1 are not primes, so clear bits
        primes.clear(0, 2);

        for (int singlePrime = 2; singlePrime >= 0 && singlePrime <= lastPossibleCompositeValue;
             singlePrime = primes.nextSetBit(singlePrime + 1)) {
            for (int composite = singlePrime * singlePrime; composite <= n; composite += singlePrime) {
                primes.clear(composite);
            }
        }

        return primes;
    }


}

