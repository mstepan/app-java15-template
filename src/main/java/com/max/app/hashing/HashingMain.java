package com.max.app.hashing;

import java.util.Arrays;
import java.util.Random;

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
            UniversalHash hashFunc = new UniversalHashWithPowerOfTwoCapacityFast(m);

            int[] freqPerBucket = new int[m];

            for (int singeVal : arrayToHash) {
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


}

