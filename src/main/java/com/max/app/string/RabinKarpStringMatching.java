package com.max.app.string;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RabinKarpStringMatching {

    public static void main(String[] args) {
//        String[] wordsToSearch = {
//                "Perhaps",
//                "sincere devotion and respect",
//                "he raised the maid",
//                "he said at last",
//                "But tell me",
//                "eyes"
//        };

//        String str = readFromFile("book-war-and-peace.txt");

        String[] wordsToSearch = new String[15 - 3 + 1];

        for (int i = 0; i < wordsToSearch.length; ++i) {
            wordsToSearch[i] = randomDNAString(3 + i);
        }

        String str = randomDNAString(1_000_000);

        for (String pattern : wordsToSearch) {

            int index = indexOf(str, pattern);

            if (index < 0) {
                System.out.println("Not found");
            }
            else {
                System.out.printf("Found at index: %d, pattern: '%s', found substring: '%s'%n", index, pattern,
                                  str.substring(index, index + pattern.length()));
            }
        }

        System.out.printf("collisionCnt: %d%n", COLLISIONS_CNT);

        System.out.println("Maine done...");
    }

    private static String readFromFile(String path) {
        try {
            URL url = RabinKarpStringMatching.class.getClassLoader().getResource(path);

            List<String> lines = Files.readAllLines(Path.of(url.toURI()));

            StringBuilder res = new StringBuilder();

            for (String singleLine : lines) {
                res.append(singleLine.trim()).append(" ");
            }

            return res.toString();
        }
        catch (IOException | URISyntaxException ex) {
            throw new IllegalStateException("Can't read lines from file", ex);
        }
    }

    private static int COLLISIONS_CNT = 0;

    private static final char[] DNAS = {'A', 'C', 'T', 'G'};
    private static final int DNAS_LENGTH = DNAS.length;

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    /**
     * Generate random DNA string of specified length.
     */
    static String randomDNAString(int length) {
        assert length >= 0;

        char[] arr = new char[length];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = DNAS[RAND.nextInt(DNAS_LENGTH)];
        }

        return new String(arr);
    }

    // base can be any number, but I decided to choose prime number closer to power of 2.
    private static final int BASE = 31;

    // as a modulo better to choose some big prime number to minimize collisions count
    // sqrt(Integer.MAX_VALUE) = 46340, the next smaller prime number is 46_337
    // we can use Integer.MAX_VALUE as a modulo, but some conmputation should be done using 'long' as a type,
    // otherwise we will have overflow error
    private static final int MODULO = 46_337;

    /**
     * Rabin-Karp string matching algorithm.
     * Time: O(N + M), where N - str.length() and M - pattern.length()
     * Space: O(1)
     *
     * @param str     base string to search 'pattern' value as a substring
     * @param pattern string to match.
     * @return index of substring in 'str' that exactly matches the 'pattern' value,
     * otherwise -1.
     */
    public static int indexOf(String str, String pattern) {
        Objects.requireNonNull(str, "Can't use null 'str' string for Rabin-Karp algorithm.");
        Objects.requireNonNull(pattern, "Can't use null 'pattern' string for Rabin-Karp algorithm.");

        if (pattern.length() > str.length()) {
            return -1;
        }

        if (pattern.length() == str.length()) {
            return str.equals(pattern) ? 0 : -1;
        }

        int strHash = hashMod(str, pattern.length(), BASE, MODULO);
        int patternHash = hashMod(pattern, pattern.length(), BASE, MODULO);

        if (strHash == patternHash && isEquals(str, 0, pattern)) {
            return 0;
        }

        final int modPow = modPower(BASE, pattern.length() - 1, MODULO);

        for (int i = 1; i <= str.length() - pattern.length(); ++i) {
            strHash = rollHash(strHash, str.charAt(i - 1), modPow, str.charAt(i + pattern.length() - 1), BASE, MODULO);

            if (strHash == patternHash) {
                if (isEquals(str, i, pattern)) {
                    return i;
                }
                ++COLLISIONS_CNT;
            }
        }

        return -1;
    }

    static int rollHash(int curHash, char remCh, int modPow, char newCh, int base, int m) {

        // remove most significant character
        int newHash = modAbs(curHash - ((remCh * modPow) % m), m);

        // multiply using base, kinda shift left
        newHash = (newHash * base) % m;

        // add new character to the end
        newHash = (newHash + newCh) % m;

        return newHash;
    }

    static boolean isEquals(String str, int from, String pattern) {

        assert from + pattern.length() <= str.length();

        for (int i = 0; i < pattern.length(); ++i) {
            if (str.charAt(from + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    static int hashMod(String str, int length, int base, int mod) {
        int hash = 0;

        for (int i = 0; i < length; ++i) {
            char ch = str.charAt(i);
            hash = ((hash * base) % mod + ch) % mod;
        }

        return hash;
    }

    /**
     * Calculate  (value ^ pow) % mod
     */
    static int modPower(int value, int pow, int mod) {
        int res = 1;

        for (int i = 0; i < pow; ++i) {
            res = (res * value) % mod;
        }

        return res;
    }

    static int modAbs(int value, int mod) {
        if (value >= 0) {
            return value % mod;
        }

        return mod + (value % mod);
    }


}

