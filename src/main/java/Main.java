import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        final int[] a = {10, 1, 20};
        final int[] b = {10, 5, 15};

        System.out.println(isValidArrayRelationship(a, b));

        System.out.println("Maine done...");
    }

    /**
     * time: O(N*lgN)
     * space: O(N)
     */
    static boolean isValidArrayRelationship(int[] a, int[] b) {
        checkNotNull(a, "'a' is NULL");
        checkNotNull(b, "'b' is NULL");
        checkArgument(a.length == b.length, "Arrays length aren't equal");

        Pair[] arr = Pair.toPairs(a, b);

        Arrays.sort(arr, Pair.FIRST_ASC);

        for (int i = 1; i < arr.length; ++i) {
            Pair prev = arr[i - 1];
            Pair cur = arr[i];

            if (cur.second < prev.second) {
                return false;
            }
        }

        return true;
    }

    record Pair(int first, int second) {

        static Comparator<Pair> FIRST_ASC = Comparator.comparing(Pair::first);

        static Pair[] toPairs(int[] a, int[] b) {
            assert a.length == b.length : "Can't combine to array of pairs";

            Pair[] arr = new Pair[a.length];

            for (int i = 0; i < a.length; ++i) {
                arr[i] = new Pair(a[i], b[i]);
            }

            return arr;
        }
    }


}

