package com.max.app.hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class StaticHashSetTest {

    @Test
    public void createAndCallContains() {
        StaticHashSet<String> set = StaticHashSet.fromList(List.of("one", "two", "three"));

        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertTrue(set.contains("three"));

        assertFalse(set.contains("ones"));
        assertFalse(set.contains("One"));
        assertFalse(set.contains("four"));
    }

    @Test
    public void createWithSequentialValues() {
        final int boundary = 1000;
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < boundary; ++i) {
            data.add(i);
        }

        StaticHashSet<Integer> set = StaticHashSet.fromList(data);

        for (Integer singleValue : data) {
            assertTrue(set.contains(singleValue));
        }

        for (int it = 0; it < 10; ++it) {
            assertFalse(set.contains(boundary + it));
        }

        System.out.printf("total capacity: %d, maxDepth: %d%n", set.calculateUsedCapacity(), set.maxDepth());
    }

    @Test
    public void checkWithDuplicateValues() {
        StaticHashSet<String> set = StaticHashSet.fromList(List.of("one", "two", "one", "two"));

        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
    }

    @Test
    public void createWithRandomValues() {
        final int elementsCount = 10_000;
        List<String> data = new ArrayList<>();

        for (int i = 0; i < elementsCount; ++i) {
            data.add(randomAsciiString());
        }

        StaticHashSet<String> set = StaticHashSet.fromList(data);

        for (String addedValue : data) {
            assertTrue(set.contains(addedValue));
        }

        for (int it = 0; it < 100; ++it) {
            assertFalse(set.contains(randomAsciiString()));
        }

        System.out.printf("total capacity: %d, maxDepth: %d%n", set.calculateUsedCapacity(), set.maxDepth());
    }

    private static final Random RAND = new Random();

    private String randomAsciiString() {
        int length = 10 + RAND.nextInt(100);
        StringBuilder buf = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char randCh = (char) ('a' + RAND.nextInt('z' - 'a' + 1));
            buf.append(randCh);
        }
        return buf.toString();
    }
}
