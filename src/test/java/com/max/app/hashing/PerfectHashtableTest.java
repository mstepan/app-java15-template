package com.max.app.hashing;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PerfectHashtableTest {

    @Test
    public void createAndCallContains() {
        PerfectHashtable<String> table = new PerfectHashtable<>(List.of("one", "two", "three"));

        assertTrue(table.contains("one"));
        assertTrue(table.contains("two"));
        assertTrue(table.contains("three"));

        assertFalse(table.contains("ones"));
        assertFalse(table.contains("One"));
        assertFalse(table.contains("four"));
    }

    @Test
    public void createWithSequentialValues() {

        final int boundary = 1000;
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 1000; ++i) {
            data.add(i);
        }

        PerfectHashtable<Integer> table = new PerfectHashtable<>(data);

        for (Integer singleValue : data) {
            assertTrue(table.contains(singleValue));
        }

        for(int it = 0; it < 10; ++it){
            assertFalse(table.contains(boundary + it));
        }

    }

}
