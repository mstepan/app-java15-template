package com.max.app.hashing;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Super fast universal hash function with very good distribution.
 * deviation: 3.0, deviation(%): 0.00
 */
public final class UniversalHashWithPowerOfTwoCapacityFast<T> implements UniversalHash<T> {

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private static final int INT_BITS = Integer.SIZE;
    private final int a;
    private final int mBits;

    public UniversalHashWithPowerOfTwoCapacityFast(int m) {
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
    public int hash(T value) {
        return (a * value.hashCode()) >>> (INT_BITS - mBits);
    }

    int log2(int value) {
        return (int) (Math.log(value) / Math.log(2.0));
    }
}
