package com.max.app.hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Perfect hashing is a data structure of linear expected size, whose expected worst-case search time is constant.
 * At the top level, we use a hash table of size m = n and a near-universal hash function, but instead of
 * linked lists, we use secondary hash tables to resolve collisions. Specifically, the jth secondary hash table
 * has size 2(Nj)^2, where Nj is the number of items whose primary hash value is j. Our earlier analysis implies that
 * with probability at least 1/2, the secondary hash table has no collisions at all, so the worst-case
 * search time in any secondary hash table is O(1). (If we discover a collision in some secondary
 * hash table, we can simply rebuild that table with a new near-universal hash function.)
 * Although this data structure apparently needs significantly more memory for each secondary
 * structure, the overall increase in space is insignificant, at least in expectation.
 */
public class StaticHashSet<T> {

    private final int capacity;

    private final DataNode<T>[] table;

    private final UniversalHashRegular<T> hashFunction;

    public StaticHashSet(List<T> values) {
        // top level hashtable capacity, should be equal to elements count, so capacity = list.size()
        this(values, values.size());
    }

    @SuppressWarnings("unchecked")
    private StaticHashSet(List<T> values, int capacity) {
        Objects.requireNonNull(values);
        this.capacity = capacity;
        this.table = new DataNode[capacity];
        this.hashFunction = new UniversalHashRegular<>(capacity);
        addValues(values);
    }

    /**
     * This method can be used to calculate space occupied by perfect hashtable.
     */
    int calculateUsedCapacity() {
        int totalCapacity = table.length;

        for (DataNode<?> node : table) {
            if (node != null && node.hashtable != null) {
                totalCapacity += node.hashtable.calculateUsedCapacity();
            }
        }

        return totalCapacity;
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
                    table[i] = new DataNode<>(cur.value());
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
        StaticHashSet<U> hashtable;

        DataNode(U value) {
            this.value = value;
        }

        public DataNode(List<U> list) {
            // internal capacity should be 2 * freq[i] * freq[i]
            int internalHashCapacity = list.size() * list.size() * 2;
            this.hashtable = new StaticHashSet<>(list, internalHashCapacity);
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
            assert list != null : "null list reference detected";
            list.add(value);
        }

        U value() {
            assert list.size() == 1 : "incorrect list size detected";
            return list.get(0);
        }
    }

}
