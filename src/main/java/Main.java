import java.math.BigInteger;
import java.util.Arrays;

/**
 * JVM parameter to print memory when JVM process exited:
 * -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics
 */
public class Main {

    /**
     * Hackerrank https://www.hackerrank.com/challenges/maximum-palindromes
     */
    public static void main(String[] args) throws Exception {

        initialize("daadabbadcabacbcccbdcccdbcccbbaadcbabbdaaaabbbdabdbbdcadaaacaadadacddabbbbbdcccbaabbbacacddbbbcbbdbd");

        System.out.println(answerQuery(14, 17));

        System.out.println("Maine done...");
    }

    private static char[] arr;
    private static int[][] prefixFreq;

    public static void initialize(String s) {
        arr = s.toCharArray();
        prefixFreq = new int[arr.length][];

        int[] latest = new int['z' - 'a' + 1];

        for (int i = 0; i < arr.length; ++i) {
            ++latest[arr[i] - 'a'];
            prefixFreq[i] = Arrays.copyOf(latest, latest.length);
        }
    }

    private static final int MOD = 1_000_000_007;
    private static final BigInteger MOD_BIGINT = BigInteger.valueOf(MOD);

    /*
     * Complete the 'answerQuery' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. INTEGER l
     *  2. INTEGER r
     */
    public static int answerQuery(int l, int r) {

        int[] freq = freqTable(l - 1, r - 1);

        int oddCount = calculateOddCount(freq);

        int halfSize = 0;
        BigInteger denom = BigInteger.ONE;

        for (int curFreq : freq) {
            int halfFreq = curFreq / 2;

            halfSize += halfFreq;

            if (halfFreq > 1) {
                denom = denom.multiply(factorialMod(halfFreq));
            }

        }

        denom = denom.modInverse(MOD_BIGINT);

        long partialRes = factorialMod(halfSize).multiply(denom).mod(MOD_BIGINT).longValue();

        if (oddCount != 0) {
            partialRes *= oddCount;
        }

        return (int) (partialRes % MOD);
    }

    private static int[] freqTable(int from, int to) {

        if (from == 0) {
            return prefixFreq[to];
        }

        return subtractFrequencies(prefixFreq[to], prefixFreq[from - 1]);
    }

    private static int[] subtractFrequencies(int[] base, int[] toRemove) {

        int[] res = Arrays.copyOf(base, base.length);

        for (int i = 0; i < res.length; ++i) {
            res[i] -= toRemove[i];
        }

        return res;
    }

    private static BigInteger factorialMod(int halfFreq) {
        BigInteger res = BigInteger.ONE;

        for (int val = 2; val <= halfFreq; ++val) {
            res = res.multiply(BigInteger.valueOf(val)).mod(MOD_BIGINT);
        }

        return res;
    }

    private static int calculateOddCount(int[] freqTable) {

        int cnt = 0;

        for (int curFreq : freqTable) {
            if ((curFreq & 1) != 0) {
                ++cnt;
            }
        }

        return cnt;
    }


}
