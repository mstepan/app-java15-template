package com.max.app.hashing;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class HashingMain {

    private static final Random RAND = new Random();

    /*
    Benchmark:
                                 Mode  Cnt  Score   Error  Units
             UniversalHash.hash  avgt   25  0.416 ± 0.006  ms/op
    UniversalHashSuperFast.hash  avgt   25  0.273 ± 0.002  ms/op

     */
    public static void main(String[] args) {

        final int n = 100_000_000;
        final int[] arrayToHash = generateSequentialValues(n);
        final int m = 1024;

        for (int it = 0; it < 10; ++it) {
            UniversalHash hashFunc = new UniversalHashFast(m);

            int[] freqPerBucket = new int[m];

            for (int singeVal : arrayToHash ) {
                int hashBucketIndex = hashFunc.hash(singeVal);

                ++freqPerBucket[hashBucketIndex];

                if (hashBucketIndex >= m) {
                    throw new IllegalStateException("Big hash value detected");
                }
            }

            int minFreq = Arrays.stream(freqPerBucket).min().getAsInt();
            int maxFreq = Arrays.stream(freqPerBucket).max().getAsInt();

//            System.out.printf("min: %d, max: %d %n", minFreq, maxFreq);

            double deviation = standardDeviation(freqPerBucket);

            double deviationPercentageFromAvg = (deviation * 100) / average(freqPerBucket);

            System.out.printf("deviation: %.1f, deviation(%%): %.2f %n", deviation, deviationPercentageFromAvg);
        }

        System.out.println("Maine done...");
    }

    private static int[] generateRandomArray(int length) {
        assert length >= 0;
        int[] randArr = new int[length];

        for (int i = 0; i < randArr.length; ++i) {
            randArr[i] = RAND.nextInt();
        }

        return randArr;
    }

    private static int[] generateSequentialValues(int length) {
        assert length >= 0;
        int[] randArr = new int[length];

        for (int i = 0; i < randArr.length; ++i) {
            randArr[i] = i;
        }

        return randArr;
    }

    private static double average(int[] arr) {

        double sum = 0.0;
        for (int val : arr) {
            sum += val;
        }

        return sum / arr.length;
    }

    private static double standardDeviation(int[] arr) {

        final double avg = average(arr);

        double deviationVal = 0.0;

        for (int singleVal : arr) {
            double diff = singleVal - avg;
            deviationVal += (diff * diff);
        }

        return Math.sqrt(deviationVal / arr.length);
    }

    interface UniversalHash {
        int hash(int value);
    }

    /**
     * deviation: 667.1, deviation(%): 0.34
     */
    static final class UniversalHashNormal implements UniversalHash {

        private static int BIG_PRIME = 10_000_339;

        private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

        private final int a;
        private final int b;
        private final int mod;

        public UniversalHashNormal(int m) {
            this.a = 1 + RAND.nextInt(BIG_PRIME - 1);
            this.b = RAND.nextInt(BIG_PRIME);
            this.mod = m - 1;
        }

        @Override
        public int hash(int value) {
            return ((a * value + b) % BIG_PRIME) & (mod);
        }

    }

    /**
     * Super fast universal hash function with very good distribution.
     * deviation: 3.0, deviation(%): 0.00
     */
    static final class UniversalHashFast implements UniversalHash {

        private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

        private static final int INT_BITS = Integer.SIZE;
        private final int a;
        private final int mBits;

        public UniversalHashFast(int m) {
            if (!isPowerOfTwo(m)) {
                throw new IllegalArgumentException("'m' should be power of 2, but found '" + m + "'");
            }
            this.a = randomOddNumber();
            this.mBits = log2(m);
        }

        private static boolean isPowerOfTwo(int value) {
            return (value & (value - 1)) == 0;
        }

        private int randomOddNumber() {
            return RAND.nextInt() | 1;
        }

        @Override
        public int hash(int value) {
            return (a * value) >>> (INT_BITS - mBits);
        }

        int log2(int value) {
            return (int) (Math.log(value) / Math.log(2.0));
        }
    }

}

