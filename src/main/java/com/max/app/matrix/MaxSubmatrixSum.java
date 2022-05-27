package com.max.app.matrix;

import java.util.Objects;
import java.util.Random;

public class MaxSubmatrixSum {


    public static void main(String[] args) {

        int[][] m = generateRandomMatrix();

//        int[][] m = {
//            {0, -2, -7, 0},
//            {9, 2, -6, 2},
//            {-4, 1, -4, 1},
//            {-1, 8, 0, -2}
//        };

        System.out.printf("max submatrix sum (bruteforce): %d %n", maxSubmatrixSum(m));
        System.out.printf("max submatrix sum (dynamic): %d %n", maxSubmatrixSumDynamic(m));

        System.out.println("Maine done...");
    }

    private static final Random RAND = new Random();

    private static int[][] generateRandomMatrix() {

        final int rows = 2 + RAND.nextInt(100);
        final int cols = 2 + RAND.nextInt(100);

        int[][] m = new int[rows][cols];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                m[row][col] = -100 + RAND.nextInt(201);
            }
        }

        return m;
    }

    /**
     * time: O(N^4)
     * space: O(N^2)
     */
    static long maxSubmatrixSumDynamic(int[][] m) {

        final int rows = m.length;
        final int cols = m[0].length;

        final long[][] cumMatrix = calculateCummulativeSum(m);

        long maxSoFar = 0L;

        for (int startRow = 0; startRow < rows; ++startRow) {
            for (int startCol = 0; startCol < cols; ++startCol) {

                for (int endRow = startRow; endRow < rows; ++endRow) {
                    for (int endCol = startCol; endCol < cols; ++endCol) {

                        long curMax = cumMatrix[endRow][endCol];

                        if (startRow > 0) {
                            curMax -= cumMatrix[startRow - 1][endCol];
                        }

                        if (startCol > 0) {
                            curMax -= cumMatrix[endRow][startCol - 1];
                        }

                        if (startRow > 0 && startCol > 0) {
                            curMax += cumMatrix[startRow - 1][startCol - 1];
                        }
                        maxSoFar = Math.max(maxSoFar, curMax);
                    }
                }
            }
        }

        return maxSoFar;
    }

    /**
     * time: O(N^2)
     * space: O(N^2)
     */
    private static long[][] calculateCummulativeSum(int[][] m) {

        final int rows = m.length;
        final int cols = m[0].length;

        final long[][] res = new long[rows][cols];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                long sum = m[row][col];
                if (row > 0) {
                    sum += res[row - 1][col];
                }
                if (col > 0) {
                    sum += res[row][col - 1];
                }
                if (row > 0 && col > 0) {
                    sum -= res[row - 1][col - 1];
                }

                res[row][col] = sum;
            }
        }

        return res;
    }

    /**
     * time: O(N^4 * N^2 = N^6)
     * space: O(1)
     */
    static long maxSubmatrixSum(int[][] m) {
        Objects.requireNonNull(m);

        if (m.length == 0) {
            return 0L;
        }

        checkNotNullRows(m);

        final int rows = m.length;
        final int cols = m[0].length;

        long maxSoFar = 0L;

        for (int startRow = 0; startRow < rows; ++startRow) {
            for (int startCol = 0; startCol < cols; ++startCol) {

                for (int endRow = startRow; endRow < rows; ++endRow) {
                    for (int endCol = startCol; endCol < cols; ++endCol) {
                        maxSoFar = Math.max(maxSoFar, subSum(m, startRow, startCol, endRow, endCol));
                    }
                }
            }
        }

        return maxSoFar;
    }

    private static void checkNotNullRows(int[][] m) {
        for (int[] subArr : m) {
            Objects.requireNonNull(subArr);
        }
    }

    private static long subSum(int[][] m, int startRow, int startCol, int endRow, int endCol) {

        assert startRow <= endRow && startCol <= endCol;

        long sum = 0L;
        for (int row = startRow; row <= endRow; ++row) {
            for (int col = startCol; col < endCol; ++col) {
                sum += m[row][col];
            }
        }

        return sum;
    }

}

