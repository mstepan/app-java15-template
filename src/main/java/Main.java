import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        /*

78798081828384858687888990919293   YES 78

9596979899100101102103104195106 YES 95

         */
        separateNumbers("7879");

        System.out.println("Main done...");
    }

    public static void separateNumbers(String s) {
        Optional<BigInteger> maybeSolution = findFirstNumFromSplit(s);
        if (maybeSolution.isPresent()) {
            System.out.printf("YES %d%n", maybeSolution.get());
        }
        else {
            System.out.println("NO");
        }
    }

    private static class Prev {
        private final List<BigInteger> prev = new ArrayList<>();
        private final List<BigInteger> splitLength = new ArrayList<>();
    }

    /**
     * time: O(N^2)
     * space: O(N)
     */
    private static Optional<BigInteger> findFirstNumFromSplit(String str) {
        int[] arr = toDecimalArr(str);
        BigInteger[] prev = new BigInteger[arr.length];
        int[] splitLength = new int[arr.length];

        prev[0] = BigInteger.valueOf(arr[0]);
        splitLength[0] = 1;

        for (int i = 1; i < arr.length; ++i) {

            BigInteger mul = BigInteger.ONE;
            BigInteger cur = BigInteger.ZERO;

            boolean splitPointFound = false;

            int j = i;
            for (; j > 0; --j) {
                if (arr[j] == 0) {
                    mul = mul.multiply(BigInteger.TEN);
                    continue;
                }
                cur = BigInteger.valueOf(arr[j]).multiply(mul).add(cur);

                // split point found
                if (cur.subtract(prev[j - 1]).equals(BigInteger.ONE)) {
                    prev[i] = cur;
                    splitLength[i] = splitLength[j - 1] + 1;
                    splitPointFound = true;
                    break;
                }

                mul = mul.multiply(BigInteger.TEN);
            }

            // split not found
            if (!splitPointFound) {
                prev[i] = prev[0].multiply(mul).add(cur);
                splitLength[i] = 1;
            }
        }

        if (isSplitPossible(splitLength)) {
            return Optional.of(findFirstNumberFromSplit(prev, splitLength));
        }

        return Optional.empty();
    }

    private static int[] toDecimalArr(String str) {

        final int length = str.length();
        int[] res = new int[length];

        for (int i = 0; i < length; ++i) {
            res[i] = str.charAt(i) - '0';
        }

        return res;
    }

    private static boolean isSplitPossible(int[] splitLength) {
        return splitLength[splitLength.length - 1] > 1;
    }

    private static BigInteger findFirstNumberFromSplit(BigInteger[] prev, int[] splitLength) {

        int index = splitLength.length - 1;

        while (splitLength[index] != 1) {
            BigInteger cur = prev[index];
            index -= String.valueOf(cur).length();
        }

        return prev[index];
    }

}
