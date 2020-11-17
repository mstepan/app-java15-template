package com.max.app.cracking.coding.interview.string;

import java.util.Arrays;

import static com.max.app.cracking.coding.interview.string.PropagateZerosInMatrix.Direction.COL;
import static com.max.app.cracking.coding.interview.string.PropagateZerosInMatrix.Direction.ROW;

public final class PropagateZerosInMatrix {


    public static void main(String[] args) throws Exception {

        int[][] m = {
                {1, 0, 1, 0, 1},
                {1, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 1, 1, 1}
        };

        propagateZeros(m);

        printMatrix(m);

        System.out.printf("PropagateZerosInMatrix completed. java version: %s%n", System.getProperty("java.version"));
    }

    private static void printMatrix(int[][] m) {
        assert m != null;

        for (int[] rowArr : m) {
            System.out.println(Arrays.toString(rowArr));
        }
    }

    /**
     * N = rows in 'm'
     * M = cols in 'm'
     * <p>
     * time: O(N*M)
     * space: O(1), in-place operation
     */
    public static void propagateZeros(int[][] m) {
        checkIsRectangular(m);

        if (m.length == 0) {
            return;
        }

        if (m.length == 1) {
            fillRow(m, 0, and(m, 0, ROW));
            return;
        }

        final int rowsCnt = m.length;
        final int colsCnt = m[0].length;

        int firstRow = and(m, 0, ROW);
        int firstCol = and(m, 0, COL);

        // mark phase
        for (int row = 1; row < rowsCnt; ++row) {
            for (int col = 1; col < colsCnt; ++col) {
                if (m[row][col] == 0) {
                    m[0][col] = 0;
                    m[row][0] = 0;
                }
            }
        }

        // zeros propagation phase
        for (int row = 1; row < rowsCnt; ++row) {
            m[row][0] = firstCol;

            for (int col = 1; col < colsCnt; ++col) {

                if (m[0][col] == 0 || m[row][0] == 0) {
                    m[row][col] = 0;
                }
            }
        }

        // fill in zero indexed row
        fillRow(m, 0, firstRow);
    }

    private static void checkIsRectangular(int[][] m) {
        if (m == null) {
            throw new IllegalArgumentException("NULL matrix 'm' detected");
        }

        if (m.length == 0) {
            return;
        }

        final int colsCnt = m[0].length;

        for (int rowIndex = 1; rowIndex < m.length; ++rowIndex) {
            if (m[rowIndex] == null) {
                throw new IllegalArgumentException("NULL row = " + rowIndex + " detected in matrix");
            }

            if (m[rowIndex].length != colsCnt) {
                throw new IllegalArgumentException("Not a rectangular matrix detected, " +
                                                           "row: " + rowIndex + " has dimension: " + m[rowIndex].length +
                                                           ", but expected: " + colsCnt);
            }
        }
    }

    private static int and(int[][] m, int index, Direction direction) {

        int boundary = (direction == ROW) ? m[index].length : m.length;

        int res = 1;
        for (int otherIndex = 0; otherIndex < boundary; ++otherIndex) {
            if (direction == ROW) {
                res = res & m[index][otherIndex];
            }
            else {
                res = res & m[otherIndex][index];
            }
        }

        return res;
    }

    enum Direction {
        ROW, COL;
    }

    private static void fillRow(int[][] m, int row, int value) {
        Arrays.fill(m[row], value);
    }

}
