package com.max.app.hashing;

import java.util.List;
import java.util.Objects;

public class PerfectHashtable {

    private final int capacity;

    private final Node[] table;

    private final UniversalHashNormal hash;

    public PerfectHashtable(List<String> values) {
        Objects.requireNonNull(values);
        this.capacity = values.size();
        this.table = new Node[capacity];
        this.hash = new UniversalHashNormal(capacity);
    }

    interface Node {
    }

    private static final class ValueNode implements Node {
        final String value;

        public ValueNode(String value) {
            this.value = value;
        }
    }

    private static final class ListNode implements Node {
        final List<String> values;

        public ListNode(List<String> values) {
            this.values = values;
        }
    }

    private static final class HashNode implements Node {

    }

}
