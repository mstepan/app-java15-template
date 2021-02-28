package com.max.app;

import java.util.function.Supplier;

public final class ArrayUtils {

    public static PartitionInfo treeWayPartition(int[] arr, int index) {
        checkNotNull(arr, () -> "null 'arr' reference detected");
        checkIndexBoundary(index, 0, arr.length - 1, () ->
                String.format("Index is out of boundary, index = %d, but should be in range [0, %d]", index, arr.length));

        return new PartitionInfo(-1, -1);
    }

    private ArrayUtils() {
        throw new IllegalStateException("Can't instantiate utility-only class");
    }

    private static <T> T checkNotNull(T obj, Supplier<String> errorMsg) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMsg.get());
        }
        return obj;
    }

    private static void checkIndexBoundary(int index, int from, int to, Supplier<String> errorMsg) {
        if (index < from || index > to) {
            throw new IndexOutOfBoundsException(errorMsg.get());
        }
    }

    public static record PartitionInfo(int lessBoundary, int eqBoundary){}
}
