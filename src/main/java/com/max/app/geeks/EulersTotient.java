package com.max.app.geeks;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class EulersTotient {

    public static void main(String[] args) {

        int res = countEulersTotient(5);

        System.out.println(res);

        System.out.println("Maine done...");
    }

    /**
     *
     * Coding Problem: Euler’s Totient
     * Euler’s Totient function Φ (n) for an input n is the count of numbers in {1, 2, 3, …, n} that are relatively prime to
     * n, i.e., the numbers whose GCD (Greatest Common Divisor) with n is 1.
     *
     * https://algomart.quora.com/Coding-Problem-Euler-s-Totient
     */
    public static int countEulersTotient(int n) {
        checkArgument(n >= 0, "Can't calculate totient numbers count for n = %s, n should be greater or equals to 0", n);
        if (n == 0 || n == 1) {
            return 0;
        }

        Set<Integer> primeFactors = primeFactorization(n);
        BitSet totient = new BitSet(n);
        totient.set(1, n, true);

        for (int fac : primeFactors) {
            for (int cur = fac; cur < n; cur += fac) {
                totient.clear(cur);
            }
        }

        return totient.cardinality();
    }

    /**
     * Calculate prime factors for value without duplicates.
     */
    private static Set<Integer> primeFactorization(int n) {

        List<Integer> primes = primes(n);
        int val = n;

        Set<Integer> factors = new HashSet<>();
        while (val != 1) {
            for(int singlePrime : primes ){
                if( val % singlePrime == 0 ){
                    val /= singlePrime;
                    factors.add(singlePrime);

                }
            }
        }

        return factors;
    }

    /**
     * Calculate all prime values from 2 up to N.
     */
    private static List<Integer> primes(int n) {

        int boundary = ((int) Math.sqrt(n)) + 1;

        BitSet primes = new BitSet(n + 1);
        primes.set(2, n+1, true);

        for (int i = primes.nextSetBit(2); i <= boundary && i >= 0; i = primes.nextSetBit(i+1)) {
            for (int cur = i * i; cur < n; cur += i) {
                primes.clear(cur);
            }
        }

        return toListForSetValues(primes);
    }

    private static List<Integer> toListForSetValues(BitSet primes) {

        List<Integer> values = new ArrayList<>();

        for (int i = primes.nextSetBit(0); i >= 0; i = primes.nextSetBit(i + 1)) {
            values.add(i);
        }

        return values;
    }
}
