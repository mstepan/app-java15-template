package com.max.app.uva;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public final class Uva102 {

    enum Color {
        BROWN("B"),
        GREEN("G"),
        CLEAR("C");

        private final String shortName;

        Color(String shortName) {
            this.shortName = shortName;
        }

        String getShortName() {
            return shortName;
        }

        static Color[] sortedAlphabetically() {
            return new Color[]{BROWN, CLEAR, GREEN};
        }
    }

    record Bean(int brown, int green, int clear) {
        public int count(Color color) {
            return switch (color) {
                case BROWN -> brown();
                case GREEN -> green();
                case CLEAR -> clear();
            };
        }
    }

    // UVA-102 - Ecological Bin Packing
    public static void main(String[] args) throws Exception {

        int[] data = {
                // brown, green, clear
                5, 10, 5,
                20, 10, 5,
                10, 20, 10
        };

        var beans = createBeans(data);

        var permutations = allPermutations();

        int minMoves = Integer.MAX_VALUE;
        List<Color> bestPerm = new ArrayList<>();

        for (List<Color> singlePerm : permutations) {
            int movesCount = calculateMoves(singlePerm, beans);

            if (movesCount < minMoves) {
                minMoves = movesCount;
                bestPerm = singlePerm;
            }
        }

        System.out.printf("%s: %d%n", toShortNames(bestPerm), minMoves);

        System.out.printf("Main completed...", System.getProperty("java.version"));
    }

    private static String toShortNames(List<Color> colors) {
        return colors.stream().map(Color::getShortName).collect(Collectors.joining());
    }

    private static Bean[] createBeans(int[] data) {

        final int beansCount = data.length / 3;

        Bean[] beans = new Bean[beansCount];

        for (int i = 0; i < data.length; i += 3) {
            int beanIndex = i / beansCount;
            beans[beanIndex] = new Bean(data[i], data[i + 1], data[i + 2]);
        }

        return beans;
    }

    private static List<List<Color>> allPermutations() {
        List<List<Color>> all = new ArrayList<>();
        generatePermutationsRec(new ArrayDeque<>(), 0, all);
        return all;
    }

    private static final int ALL_COLORS_USED = (1 << Color.values().length) - 1;

    private static void generatePermutationsRec(Deque<Color> cur, int used, List<List<Color>> all) {

        if (used == ALL_COLORS_USED) {
            all.add(new ArrayList<>(cur));
            return;
        }

        // enumerate all enum elements in alphabetical order
        for (Color color : Color.sortedAlphabetically()) {

            int mask = 1 << color.ordinal();

            if ((used & mask) == 0) {
                cur.add(color);
                generatePermutationsRec(cur, used | mask, all);
                cur.removeLast();
            }
        }
    }

    private static int calculateMoves(List<Color> singlePerm, Bean[] beans) {

        int totalMovesCnt = 0;

        for (int i = 0; i < singlePerm.size(); ++i) {
            Color curColor = singlePerm.get(i);

            for (int j = 0; j < beans.length; ++j) {
                if (i != j) {
                    totalMovesCnt += beans[j].count(curColor);
                }
            }

        }

        return totalMovesCnt;
    }


}

