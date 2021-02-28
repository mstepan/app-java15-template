package com.max.app;


public final class Main {

    public static void main(String[] args) throws Exception {

        int[] arr = {1, 3, 8, 2, 3, 9, 4, 6, 8, 5, 6};

        // pivot arr[6] = 4;
        int index = 6;

        var partitionInfo = ArrayUtils.treeWayPartition(arr, index);

        System.out.println(partitionInfo.lessBoundary());
        System.out.println(partitionInfo.eqBoundary());

        System.out.printf("Main completed...%njava version: %s%n", System.getProperty("java.version"));
    }

}

