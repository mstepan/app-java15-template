package com.max.app.hashing;

import java.util.concurrent.ThreadLocalRandom;

/**
 * deviation: 667.1, deviation(%): 0.34
 */
public final class UniversalHashRegular<T> implements UniversalHash<T> {

    private static final int BIG_PRIME = 10_000_339;

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private final int a;
    private final int b;
    private final int mod;

    public UniversalHashRegular(int capacity) {
        this.a = 1 + RAND.nextInt(BIG_PRIME - 1);
        this.b = RAND.nextInt(BIG_PRIME);
        this.mod = capacity;
    }

    public UniversalHashRegular(int a, int b, int mod) {
        this.a = a;
        this.b = b;
        this.mod = mod;
    }

    @Override
    public int hash(T value) {
        int hashValue = ((a * (value == null ? 0 : value.hashCode() + b) % BIG_PRIME)) % mod;
        return hashValue >= 0 ? hashValue : -hashValue;
    }
}
