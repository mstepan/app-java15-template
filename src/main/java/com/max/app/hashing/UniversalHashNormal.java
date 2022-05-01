package com.max.app.hashing;

import java.util.concurrent.ThreadLocalRandom;

/**
 * deviation: 667.1, deviation(%): 0.34
 */
public final class UniversalHashNormal implements UniversalHash {

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
