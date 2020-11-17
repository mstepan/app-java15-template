package com.max.app.cracking.coding.interview.string;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class CheckPermutation {

    public static void main(String[] args) throws Exception {

        for (int it = 0; it < 10_000; ++it) {
            String base = randomAlphabetString(1 + RAND.nextInt(1000));
            String other = permutation(base);

            boolean res1 = isPermutation(base, other);
            boolean res2 = isPermutationOptimized(base, other);

            if (!res1) {
                System.out.printf("base: %s%n", base);
                System.out.printf("other: %s%n", other);
                System.out.printf("isPermutation: %b %n", res1);
                throw new IllegalStateException("'isPermutation' working incorrectly");
            }

            if (!res2) {
                System.out.printf("base: %s%n", base);
                System.out.printf("other: %s%n", other);
                System.out.printf("isPermutationOptimized: %b %n", res2);
                throw new IllegalStateException("'isPermutationOptimized' working incorrectly");
            }
        }

        System.out.printf("CheckPermutation completed. java version: %s%n", System.getProperty("java.version"));
    }

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private static final int ALPHABET_CHARS_COUNT = 'z' - 'a' + 1;

    private static String randomAlphabetString(int length) {
        assert length >= 0 : "Can't generate string with negative length: " + length;

        char[] arr = new char[length];

        for (int i = 0; i < arr.length; ++i) {
            char ch = (char) ('a' + RAND.nextInt(ALPHABET_CHARS_COUNT));
            arr[i] = ch;
        }

        return new String(arr);
    }

    private static String permutation(String base) {
        char[] arr = base.toCharArray();

        for (int i = 0, length = arr.length; i < length - 1; ++i) {
            int index = RAND.nextInt(length - i);
            swap(arr, i, index);
        }

        return new String(arr);
    }

    private static void swap(char[] arr, int from, int to) {
        assert arr != null;
        assert from >= 0 && from < arr.length;
        assert to >= 0 && to < arr.length;

        if (from == to) {
            return;
        }

        char temp = arr[from];
        arr[from] = arr[to];
        arr[to] = temp;
    }

    /**
     * Check if 'other' string is permutation of 'base' string.
     * Convert string to char array, sort, and check if two char arrays are equal.
     * time: O(N*lgN) or could be O(N) if counting sort (default in java for size > 1750) used for char[] array sorting
     * space" O(N)
     */
    public static boolean isPermutation(String base, String other) {
        Objects.requireNonNull(base, "'base' string is NULL");
        Objects.requireNonNull(other, "'other' string is NULL");

        if (base == other) {
            return true;
        }

        if (base.length() != other.length()) {
            return false;
        }

        char[] baseArr = base.toCharArray();
        char[] otherArr = other.toCharArray();

        Arrays.sort(baseArr);
        Arrays.sort(otherArr);

        return Arrays.equals(baseArr, otherArr);
    }

    /**
     * Variant 2 for check if 'other' string is permutation of 'base' string.
     * Use HashMap to store frequency of elements in 'base' string, check frequencies of
     * characters in 'other' against 'base' HashMap.
     * time: O(n)
     * space: O(N)
     */
    public static boolean isPermutationOptimized(String base, String other) {
        Objects.requireNonNull(base, "'base' string is NULL");
        Objects.requireNonNull(other, "'other' string is NULL");

        if (base == other) {
            return true;
        }

        if (base.length() != other.length()) {
            return false;
        }

        Map<Character, Integer> baseFreq = toFreqTable(base);
        Map<Character, Integer> otherFreq = toFreqTable(other);

        return baseFreq.equals(otherFreq);

        /*
        Map<Character, Integer> charsFreq = new HashMap<>();

        for (int i = 0, length = base.length(); i < length; ++i) {
            char ch = base.charAt(i);
            charsFreq.compute(ch, (key, value) -> value == null ? 1 : value + 1);

        }

        for (int i = 0, length = other.length(); i < length; ++i) {
            char ch = other.charAt(i);

            if (charsFreq.getOrDefault(ch, 0) == 0) {
                return false;
            }

            charsFreq.compute(ch, (key, value) -> value - 1);
        }

        return true;

         */
    }

    private static Map<Character, Integer> toFreqTable(String str) {
        Map<Character, Integer> freq = new HashMap<>();

        char ch;
        for (int i = 0, length = str.length(); i < length; ++i) {
            ch = str.charAt(i);
            freq.compute(ch, (key, value) -> value == null ? 1 : value + 1);
        }
        return freq;
    }

}
