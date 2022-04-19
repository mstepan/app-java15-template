package com.max.app.geeks;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Given an array A of positive integers. Your task is to find the leaders in the array. An element of array is leader if it
 * is greater than or equal to all the elements to its right side. The rightmost element is always a leader.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * Input:
 * n = 6
 * A[] = {16,17,4,3,5,2}
 * Output: 17 5 2
 */
public class LeadersInArray {

    public static void main(String[] args) {

        int[] arr = {10}; //{16, 17, 4, 3, 5, 2};
        List<Integer> res = leaders(arr);

        System.out.println(res);

        System.out.println("Maine done...");
    }

    /**
     * time: O(N)
     * space: O(N), if we have fully sorted array in reverse order, like {133, 27, 15, 12, 10, ...}
     */
    public static List<Integer> leaders(int[] arr) {
        checkNotNull(arr, "null 'arr' reference detected");
        if (arr.length == 0) {
            return Collections.emptyList();
        }

        final List<Integer> res = new ArrayList<>(arr.length);

        int maxFromRight = Integer.MIN_VALUE;

        for (int i = arr.length - 1; i >= 0; --i) {
            if (arr[i] >= maxFromRight) {
                res.add(arr[i]);
                maxFromRight = arr[i];
            }
        }

        Collections.reverse(res);
        return Collections.unmodifiableList(res);
    }
}
