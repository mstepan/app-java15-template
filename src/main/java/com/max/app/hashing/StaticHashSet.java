package com.max.app.hashing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Perfect hashing is a data structure of linear expected size, whose expected worst-case search time is constant.
 * At the top level, we use a hash table of size m = n and a near-universal hash function, but instead of linked lists,
 * we use secondary hash tables to resolve collisions. Specifically, the jth secondary hash table has size 2(Nj)^2,
 * where Nj is the number of items whose primary hash value is j. Our earlier analysis implies that with probability
 * at least 1/2, the secondary hash table has no collisions at all, so the worst-case search time in any secondary
 * hash table is O(1). (If we discover a collision in some secondary hash table, we can simply rebuild that table
 * with a new near-universal hash function.)
 * Although this data structure apparently needs significantly more memory for each secondary structure,
 * the overall increase in space is insignificant, at least in expectation.
 */
public class StaticHashSet<T> {

    private static int maxDepth = 0;

    private final int capacity;

    private final DataNode<T>[] table;

    private final UniversalHashRegular<T> hashFunction;

    public static <U> StaticHashSet<U> fromList(List<U> values) {
        Set<U> uniqueValues = new HashSet<>(values);
        return new StaticHashSet<>(new ArrayList<>(uniqueValues), uniqueValues.size(), 0);
    }

    @SuppressWarnings("unchecked")
    private StaticHashSet(List<T> values, int capacity, int depth) {
        Objects.requireNonNull(values);
        this.capacity = capacity;
        this.table = new DataNode[capacity];
        this.hashFunction = new UniversalHashRegular<>(capacity);
        addValues(values, depth);
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

    int maxDepth() {
        return maxDepth;
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value);

        final int bucketIndex = hashFunction.hash(value);

        DataNode<T> bucket = table[bucketIndex];

        return bucket != null && bucket.contains(value);
    }

    private void addValues(List<T> values, int depth) {

        maxDepth = Math.max(maxDepth, depth);

        @SuppressWarnings("unchecked")
        TempNode<T>[] tempNodes = new TempNode[this.capacity];

        int createdBucketsCount = 0;
        TempNode<T> lastBucketValue = null;
        int lastIndex = -1;

        for (T singleValue : values) {
            Objects.requireNonNull(singleValue, "Can't store null value.");
            int bucketIndex = hashFunction.hash(singleValue);

            if (tempNodes[bucketIndex] == null) {
                tempNodes[bucketIndex] = new TempNode<>(singleValue);

                ++createdBucketsCount;
                lastBucketValue = tempNodes[bucketIndex];
                lastIndex = bucketIndex;
            }
            else {
                tempNodes[bucketIndex].add(singleValue);
            }
        }

        // handle special case, when all values hashed to the same bucket
        if (createdBucketsCount == 1) {
            assert lastBucketValue != null;
            assert lastIndex >= 0 && lastIndex < table.length;
            table[lastIndex] = lastBucketValue.toOrdinarySet();
            return;
        }

        assert tempNodes.length == table.length;

        for (int i = 0; i < tempNodes.length; ++i) {
            TempNode<T> cur = tempNodes[i];
            table[i] = (cur == null) ? null : cur.toDataNode(depth + 1);
        }
    }

    //---- node for internal representation of single value and hashtable nodes

    private static class DataNode<U> {

        U value;
        StaticHashSet<U> hashtable;
        Set<U> set;

        DataNode(U value) {
            this.value = value;
        }

        DataNode(Set<U> set) {
            this.set = set;
        }

        public DataNode(List<U> list, int depth) {
            // internal capacity should be 2 * freq[i] * freq[i]
            int internalHashCapacity = list.size() * list.size() * 2;
            this.hashtable = new StaticHashSet<>(list, internalHashCapacity, depth);
        }

        boolean contains(U valueToSearch) {
            if (value != null) {
                return value.equals(valueToSearch);
            }
            else if (hashtable != null) {
                return hashtable.contains(valueToSearch);
            }
            return set.contains(valueToSearch);
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

        DataNode<U> toOrdinarySet() {
            return new DataNode<>(new HashSet<>(list));
        }

        DataNode<U> toDataNode(int depth) {
            if (list.size() == 1) {
                return new DataNode<>(list.get(0));
            }
            return new DataNode<>(list, depth);
        }

    }

}
