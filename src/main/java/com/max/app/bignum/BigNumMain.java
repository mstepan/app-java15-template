package com.max.app.bignum;

import java.util.concurrent.ThreadLocalRandom;

public final class BigNumMain {


    public static void main(String[] args) throws Exception {

        for (int it = 0; it < 1_000_000; ++it) {

            int randValue = ThreadLocalRandom.current().nextInt(1_000_000_000);

            BigNum value = new BigNum(String.valueOf(randValue));

            int bigNumAsDecimal = value.toDecimalValue();

            if (randValue != bigNumAsDecimal) {
                System.out.printf("Mismatch, randValue: %d, BigNum: %s%n", randValue, bigNumAsDecimal);
            }
        }

        System.out.printf("BigNumMain completed..., java version:%s%n", System.getProperty("java.version"));
    }


}

