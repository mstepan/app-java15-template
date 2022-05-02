package com.max.app.hashing;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class StaticHashSetTest {

    @Test
    public void createAndCallContains() {
        StaticHashSet<String> hashtable = new StaticHashSet<>(List.of("one", "two", "three"));

        assertTrue(hashtable.contains("one"));
        assertTrue(hashtable.contains("two"));
        assertTrue(hashtable.contains("three"));

        assertFalse(hashtable.contains("ones"));
        assertFalse(hashtable.contains("One"));
        assertFalse(hashtable.contains("four"));
    }

    @Test
    public void createWithSequentialValues() {
        final int boundary = 1000;
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < boundary; ++i) {
            data.add(i);
        }

        StaticHashSet<Integer> hashtable = new StaticHashSet<>(data);

        for (Integer singleValue : data) {
            assertTrue(hashtable.contains(singleValue));
        }

        for(int it = 0; it < 10; ++it){
            assertFalse(hashtable.contains(boundary + it));
        }
    }

}
