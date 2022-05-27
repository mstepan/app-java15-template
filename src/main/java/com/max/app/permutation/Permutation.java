package com.max.app.permutation;

import java.util.Arrays;
import java.util.Objects;

public class Permutation {

    public static void main(String[] args) {

        char[] arr = {'a', 'b', 'c', 'd', 'e'};
        int cnt = 1;

        System.out.println(Arrays.toString(arr));

        while (nextPermutation(arr)) {
            System.out.println(Arrays.toString(arr));
            ++cnt;
        }

        System.out.printf("cnt: %d%n", cnt);

        System.out.println("Permutation done...");
    }

    /**
     * Returns next permutation in lexicographical order if available.
     * <p>
     * time: O(n)
     * space: O(1), in-place
     *
     * @return true if there is next permutation available in lexicographical order,
     * otherwise false.
     */
    static boolean nextPermutation(char[] arr) {

        Objects.requireNonNull(arr, "null 'arr' argument detected");

        if (arr.length < 2) {
            return false;
        }

        int smallerIdx = findFirstDecFromRight(arr);
        if (smallerIdx < 0) {
            return false;
        }

        int nextIdx = biggerFromRight(arr, arr[smallerIdx]);

        assert nextIdx >= 0 : "negative value for 'nextIdx' detected";

        swap(arr, smallerIdx, nextIdx);
        reverse(arr, smallerIdx + 1, arr.length - 1);

        return true;
    }

    private static int findFirstDecFromRight(char[] arr) {
        assert arr != null;

        int i = arr.length - 2;

        while (i >= 0) {
            if (arr[i] < arr[i + 1]) {
                return i;
            }
            --i;
        }

        return -1;
    }

    private static int biggerFromRight(char[] arr, int value) {
        assert arr != null;

        for (int i = arr.length - 1; i >= 0; --i) {
            if (arr[i] > value) {
                return i;
            }
        }

        return 0;
    }

    private static void reverse(char[] arr, int leftBase, int rightBase) {
        assert arr != null;
        assert leftBase >= 0 && leftBase < arr.length;
        assert rightBase >= 0 && rightBase < arr.length;
        assert leftBase <= rightBase;

        int left = leftBase;
        int right = rightBase;

        while (left < right) {
            swap(arr, left, right);
            ++left;
            --right;
        }
    }

    private static void swap(char[] arr, int from, int to) {
        assert arr != null;
        assert from >= 0 && from < arr.length;
        assert to >= 0 && to < arr.length;

        char temp = arr[from];
        arr[from] = arr[to];
        arr[to] = temp;
    }

}

