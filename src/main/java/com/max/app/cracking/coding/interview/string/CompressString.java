package com.max.app.cracking.coding.interview.string;


import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public final class CompressString {

    public static void main(String[] args) throws Exception {

        for (int it = 0; it < 1000; ++it) {

            String str = 10 + randomAlphabetString(RAND.nextInt(10000));

//            long startTime1 = System.currentTimeMillis();
            String compressed1 = compress(str);
//            long endTime1 = System.currentTimeMillis();
//            System.out.printf("time1: %d ms%n", (endTime1-startTime1));

//            long startTime2 = System.currentTimeMillis();
            String compressed2 = parallelCompress(str);
//            long endTime2 = System.currentTimeMillis();//
//            System.out.printf("time2: %d ms%n", (endTime2-startTime2));

            if (!compressed1.equals(compressed2)) {
                System.out.printf("  str: %s%n", str);
                System.out.printf("comp1: %s%n", compressed1);
                System.out.printf("comp2: %s%n", compressed2);
            }
        }

        System.out.printf("CompressString completed. java version: %s%n", System.getProperty("java.version"));
    }

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private static final class EncodeRecursiveTask extends RecursiveTask<String> {

        private static final int SEQUENTIAL_EXECUTION_THRESHOLD = 100;

        private final String str;
        private final int from;
        private final int to;

        public EncodeRecursiveTask(String str, int from, int to) {
            this.str = str;
            this.from = from;
            this.to = to;
        }

        @Override
        protected String compute() {

            int chunkSize = to - from + 1;

            if (chunkSize <= SEQUENTIAL_EXECUTION_THRESHOLD || hasAllSameCharacters(str, from, to)) {

                String substr = str.substring(from, to + 1);
                String substrEncoded = encodeRange(str, from, to);

//                System.out.printf("Sequential: for range = [%d ... %d], %s => %s%n",
//                                  from, to, substr, substrEncoded);

                return substrEncoded;
            }

//            System.out.printf("Parallel: for range = [%d ... %d]%n", from, to);

            int mid = findMiddle(str, from, to);

//            System.out.printf("%s divided into %s and %s %n",
//                              str.substring(from, to + 1),
//                              str.substring(from, mid + 1),
//                              str.substring(mid + 1, to + 1));

            EncodeRecursiveTask left = new EncodeRecursiveTask(str, from, mid);
            ForkJoinTask<String> rightTask = new EncodeRecursiveTask(str, mid + 1, to).fork();

            String leftStr = left.compute();
            String rightStr = rightTask.join();

            return leftStr + rightStr;
        }

        private static boolean hasAllSameCharacters(String str, int from, int to) {
            char ch = str.charAt(from);
            for (int i = from + 1; i <= to; ++i) {
                if (str.charAt(i) != ch) {
                    return false;
                }
            }

            return true;
        }

        private int findMiddle(String str, int from, int to) {

            int mid = from + (to - from) / 2;

            int left = mid - 1;
            int right = mid + 1;

            char ch = str.charAt(mid);

            while (left >= from || right <= to) {

                if (left >= from && str.charAt(left) != ch) {
                    return left;
                }

                if (right <= to && str.charAt(right) != ch) {
                    return right - 1;
                }

                --left;
                ++right;
            }

            return mid;
        }
    }

    private static String randomAlphabetString(int length) {
        assert length > 0 : "negative 'length' detected";
        StringBuilder buf = new StringBuilder(length);

        while (buf.length() < length) {
            char ch = (char) ('a' + RAND.nextInt('z' - 'a' + 1));
            int cnt = 1 + RAND.nextInt(10);

            for (int i = 0; i < cnt && buf.length() < length; ++i) {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Parallel version of RLE-like string compression that use ForkJoin pool.
     */
    public static String parallelCompress(String str) {

        ForkJoinPool pool = new ForkJoinPool();

        try {
            String compressedStr = pool.submit(new EncodeRecursiveTask(str, 0, str.length() - 1)).get();
            return compressedStr.length() < str.length() ? compressedStr : str;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        finally {
            pool.shutdown();
        }
    }

    /**
     * Encode string using RLE-like algorithm.
     * time: O(N)
     * space: O(N)
     */
    public static String compress(String original) {
        checkNotNull(original, "null 'original' string parameter detected");

        if (original.length() < 3) {
            return original;
        }

        String buf = encodeRange(original, 0, original.length() - 1);

        if (buf.length() >= original.length()) {
            return original;
        }

        return buf;
    }

    private static String encodeRange(String str, int from, int to) {
        char prev = str.charAt(from);
        int cnt = 1;

        final int length = to - from + 1;

        StringBuilder buf = new StringBuilder(length);
        char ch;

        for (int i = from + 1; i <= to; ++i) {
            ch = str.charAt(i);

            if (ch == prev) {
                ++cnt;
            }
            else {
                encodeCharacter(buf, prev, cnt);
                prev = ch;
                cnt = 1;
            }
        }

        encodeCharacter(buf, prev, cnt);
        return buf.toString();
    }

    private static void encodeCharacter(StringBuilder buf, char ch, int cnt) {
        buf.append(ch).append(cnt);
    }

    private static <T> void checkNotNull(T objToCheck, String errorMsg) {
        if (objToCheck == null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

}
