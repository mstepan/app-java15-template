package com.max.app.stats;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StandardDeviations {

    private static final URL FILE_WITH_TEXT_URL = StandardDeviations.class.getClassLoader().getResource("book-war-and-peace.txt");

    public static void main(String[] args) throws Exception {

        DataStatistic stats = new DataStatistic();

//        List<String> lines = randomAsciiLines(400, 80);
        List<String> lines = Files
            .readAllLines(Path.of(FILE_WITH_TEXT_URL.getFile()));

        for (String singleLine : lines) {
            StringBuilder cleanedLine = new StringBuilder(singleLine.length());
            for (int i = 0; i < singleLine.length(); ++i) {
                char ch = singleLine.charAt(i);
                if (Character.isAlphabetic(ch)) {
                    cleanedLine.append(Character.toLowerCase(ch));
                }
            }

            String lineToProcess = cleanedLine.toString().trim();

            if (lineToProcess.length() > 0) {
                stats.add(lineToProcess);
            }
        }

        System.out.printf("avg: %.1f%n", stats.average());
        System.out.printf("total elements cnt: %.1f%n", stats.sum());
        System.out.printf("distinct elements cnt: %d%n", stats.distinctElementsCount());
        System.out.printf("standard deviation: %.1f%n", stats.standardDeviation());

        System.out.println("Maine done...");
    }

    private static List<String> randomAsciiLines(int linesCount, int lineLength) {
        List<String> lines = new ArrayList<>(linesCount);

        for (int i = 0; i < linesCount; ++i) {
            lines.add(generateRandomAsciiString(lineLength));
        }

        return lines;
    }

    private static final Random RAND = new Random();

    private static final int LOWERCASE_A = 'a';
    private static final int LOWERCASE_ALPHABET_CHARS_COUNT = 'z' - 'a' + 1;

    private static String generateRandomAsciiString(int length) {

        StringBuilder res = new StringBuilder(length);

        for(int i =0; i < length; ++i){
            char randomChar = (char)(LOWERCASE_A + RAND.nextInt(LOWERCASE_ALPHABET_CHARS_COUNT));
            res.append(randomChar);
        }

        return res.toString();
    }
}

