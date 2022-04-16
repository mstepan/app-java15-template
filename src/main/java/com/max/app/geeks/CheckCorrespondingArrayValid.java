package com.max.app.geeks;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Arrays;
import java.util.Comparator;

/**
 * https://www.geeksforgeeks.org/check-if-every-pair-in-array-b-follows-the-same-relation-as-their-corresponding-values-in-a/
 * <p>
 * Check if every pair in array B follows the same relation as their corresponding values in array A.
 * <p>
 * Given two arrays A[] and B[] each of size N, the task is to check if the given arrays are valid or not, based on the
 * following conditions:
 * <p>
 * 1. Every element in A at index i, will be mapped with the element in B at the same index only, i.e. (A[i] can be
 * mapped with B[i] only)
 * 2, For any pair in A (A[i], A[j]), if A[i] > A[j], then its corresponding value in B should also be greater, i.e. B[i]
 * > B[j] should be true.
 * 3. For any pair in A (A[i], A[j]), if A[i] = A[j], then its corresponding value in B should also be equal, i.e. B[i] =
 * B[j] should be true.
 */
public final class CheckCorrespondingArrayValid {

    private CheckCorrespondingArrayValid() {
        throw new AssertionError("Can't instantiate utility-only class.");
    }

    public static void main(String[] args) {

        final int[] a = {10, 1, 20};
        final int[] b = {5, 5, 15};

        System.out.println(isValidCorrespondingArrayBruteforce(a, b));
        System.out.println(isValidCorrespondingArrayOptimal(a, b));

        System.out.println("Maine done...");
    }

    /**
     * time: O(N^2)
     * space: O(1)
     */
    public static boolean isValidCorrespondingArrayBruteforce(int[] a, int[] b) {
        checkNotNull(a, "Array 'a' is NULL");
        checkNotNull(b, "Array 'b' is NULL");
        checkArgument(a.length == b.length, "Arrays length aren't equal");

        for (int i = 0; i < a.length - 1; ++i) {
            for (int j = i + 1; j < a.length; ++j) {
                // 1-case: a[i] == a[j]
                if (a[i] == a[j]) {
                    if (b[i] != b[j]) {
                        return false;
                    }
                }
                // 2-case: a[i] > a[j]
                else if (a[i] > a[j]) {
                    if (b[i] <= b[j]) {
                        return false;
                    }
                }
                // 3-case: a[i] < a[j]
                else {
                    if (b[i] >= b[j]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * time: O(N*lgN)
     * space: O(N)
     */
    public static boolean isValidCorrespondingArrayOptimal(int[] a, int[] b) {
        checkNotNull(a, "Array 'a' is NULL");
        checkNotNull(b, "Array 'b' is NULL");
        checkArgument(a.length == b.length, "Arrays length aren't equal");

        Pair[] pairsArr = Pair.toPairs(a, b);

        Arrays.sort(pairsArr, Pair.FIRST_ASC);

        for (int i = 1; i < pairsArr.length; ++i) {
            Pair prev = pairsArr[i - 1];
            Pair cur = pairsArr[i];

            // 1-case: a[i-1] == a[i], check b[i-1] == b[i]
            if (cur.first == prev.first) {
                if (prev.second != cur.second) {
                    return false;
                }
            }
            // 2-case: a[i-1] < a[i], check b[i-1] < b[i]
            else {
                if (prev.second >= cur.second) {
                    return false;
                }
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
