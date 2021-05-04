package com.max.app;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public final class Main {

    public static void main(String[] args) throws Exception {

        List<List<int[]>> allSolutions = placeQueens();

        System.out.printf("size = %d%n", allSolutions.size());

        for (List<int[]> singleSolution : allSolutions) {
            System.out.println(buildSolutionStr(singleSolution));
        }

        System.out.printf("Main completed...", System.getProperty("java.version"));
    }

    private static final int BOARD_SIZE = 8;

    public static List<List<int[]>> placeQueens() {

        List<List<int[]>> allSolutions = new ArrayList<>();

        placeQueensRec(0, new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>(),
                       allSolutions);

        return allSolutions;

    }

    private static String buildSolutionStr(List<int[]> solution) {
        return solution.stream().map(pair -> "(" + pair[0] + ", " + pair[1] + ")").
                collect(Collectors.joining(";"));
    }

    private static void placeQueensRec(int row, Deque<Integer> usedCols, Deque<Integer> diagonal,
                                       Deque<Integer> revDiagonal, Deque<int[]> curSolution,
                                       List<List<int[]>> allSolutions) {

        if (row == BOARD_SIZE) {
            allSolutions.add(new ArrayList<>(curSolution));
            return;
        }

        for (int col = 0; col < BOARD_SIZE; ++col) {
            if (usedCols.contains(col) || revDiagonal.contains(col) || diagonal.contains(col)) {
                continue;
            }

            curSolution.addLast(new int[]{row, col});
            usedCols.addLast(col);

            diagonal.addLast(col);
            revDiagonal.addLast(col);

            placeQueensRec(row + 1, usedCols, incAll(diagonal), decAll(revDiagonal), curSolution, allSolutions);

            revDiagonal.removeLast();
            diagonal.removeLast();

            usedCols.removeLast();
            curSolution.removeLast();
        }

    }

    private static Deque<Integer> incAll(Deque<Integer> data) {
        return data.stream().map(val -> val + 1).
                collect(Collectors.toCollection(ArrayDeque::new));
    }

    private static Deque<Integer> decAll(Deque<Integer> data) {
        return data.stream().map(val -> val - 1).
                collect(Collectors.toCollection(ArrayDeque::new));
    }

}

