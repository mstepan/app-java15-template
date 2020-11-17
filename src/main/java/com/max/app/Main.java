package com.max.app;

import com.max.app.util.NumberUtils;

public final class Main {

    public static void main(String[] args) throws Exception {

        int[] numbers = {
                12321,
                22,
                54745,
                3,
                0,

                234532,
                -22,
                -2233,
                23
        };

        for (int singleNumber : numbers) {
            System.out.printf("isPalindrome1(%d): %b, isPalindrome2(%d): %b %n",
                              singleNumber, NumberUtils.isPalindrome1(singleNumber),
                              singleNumber, NumberUtils.isPalindrome2(singleNumber));
        }

        System.out.printf("java version: %s%n", System.getProperty("java.version"));
    }


}
