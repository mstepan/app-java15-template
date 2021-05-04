import java.util.Random;

public class Main {


    public static void main(String[] args) {

        Random rand = new Random();

        for (int i = 0; i < 100_000; ++i) {
            int x = rand.nextInt();
            int y = rand.nextInt();

            long expected = x * y;
            long actual = mul(x, y);

            if (expected != actual) {
                throw new IllegalStateException("Incorrect value for multiplication " +
                                                        x + " * " + y + " = " + (actual));
            }
        }

        System.out.printf("java-%s%n", System.getProperty("java.version"));
    }

    /**
     * Peasant multiplication.
     * <p>
     * time: O(lgN*M)
     * space: O(N*M)
     */
    private static long mul(int x, int y) {
        int sign = calcSign(x, y);

        long first = Math.abs((long) x);
        long second = Math.abs((long) y);

        int res = 0;

        while (first > 0) {
            if (isOdd(first)) {
                res += second;
            }

            first >>= 1;
            second <<= 1;
        }

        return (sign == 1) ? res : -res;
    }

    private static int calcSign(int x, int y) {
        return ((x >>> 31) ^ (y >>> 31)) == 0 ? 1 : -1;
    }

    private static boolean isOdd(long x) {
        return !isEven(x);
    }

    private static boolean isEven(long x) {
        return (x & 1) == 0;
    }


}
