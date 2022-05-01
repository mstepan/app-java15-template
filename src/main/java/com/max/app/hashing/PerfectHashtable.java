package com.max.app.hashing;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PerfectHashtable<T> {

    private final int capacity;

    private final DataNode<T>[] table;

    private final UniversalHashRegular<T> hashFunction;

    public PerfectHashtable(List<T> values) {
        // top level hashtable capacity, should be equal to elements count
        // so capacity = list.size()
        this(values, values.size());
    }

    @SuppressWarnings("unchecked")
    private PerfectHashtable(List<T> values, int capacity) {
        Objects.requireNonNull(values);
        this.capacity = capacity;
        this.table = new DataNode[capacity];
        this.hashFunction = new UniversalHashRegular<>(capacity);
        addValues(values);
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value);

        final int bucketIndex = hashFunction.hash(value);

        DataNode<T> bucket = table[bucketIndex];

        if (bucket == null) {
            return false;
        }

        return bucket.contains(value);
    }

    private void addValues(List<T> values) {

        @SuppressWarnings("unchecked")
        TempNode<T>[] tempNodes = new TempNode[this.capacity];

        for (T singleValue : values) {
            Objects.requireNonNull(singleValue, "Can't store null value.");
            int bucketIndex = hashFunction.hash(singleValue);

            if (tempNodes[bucketIndex] == null) {
                tempNodes[bucketIndex] = new TempNode<>(singleValue);
            }
            else {
                TempNode<T> node = tempNodes[bucketIndex];
                node.add(singleValue);
            }
        }

        for (int i = 0; i < table.length; ++i) {
            TempNode<T> cur = tempNodes[i];
            if (cur != null) {
                if (cur.list.size() == 1) {
                    table[i] = new DataNode<>(cur.firstValue());
                }
                else {
                    table[i] = new DataNode<>(cur.list);
                }
            }
        }
    }

    //---- node for internal representation of single value and hashtable nodes

    private static class DataNode<U> {

        U value;
        PerfectHashtable<U> hashtable;

        DataNode(U value) {
            this.value = value;
        }

        public DataNode(List<U> list) {
            // internal capacity should be 2 * freq[i] * freq[i]
            int internalHashCapacity = list.size() * list.size() * 2;
            this.hashtable = new PerfectHashtable<>(list, internalHashCapacity);
        }

        boolean contains(U valueToSearch) {
            if (value != null) {
                return value.equals(valueToSearch);
            }

            return hashtable.contains(valueToSearch);
        }
    }

    private static final class TempNode<U> {
        final List<U> list = new ArrayList<>();

        TempNode(U singleValue) {
            list.add(singleValue);
        }

        void add(U value) {
            list.add(value);
        }

        U firstValue() {
            return list.get(0);
        }
    }

}
