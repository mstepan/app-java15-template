package com.max.app.tree;


import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AVLTreeTest {

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    @RepeatedTest(100)
    public void addRandomValues() {

        int[] arr = randomArray(1_000);

        Set<Integer> expectedSet = new TreeSet<>();
        AVLTree<Integer> actualSet = new AVLTree<>();

        for (int value : arr) {
            boolean wasAdded1 = expectedSet.add(value);
            boolean wasAdded2 = actualSet.add(value);
            assertEquals(wasAdded1, wasAdded2, "value = " + value);
        }

        for (int value : arr) {
            assertTrue(actualSet.contains(value), ("value not found: " + value));
        }

        for (int it = 0; it < 1_000; ++it) {
            int randValue = RAND.nextInt();
            assertEquals(expectedSet.contains(randValue), actualSet.contains(randValue));
        }
    }

    private static int[] randomArray(int length) {
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt();
        }
        return arr;
    }

    @Test
    public void addWithDifferentRotationsAndContains() {
        AVLTree<Integer> tree = new AVLTree<>();

        assertTrue(tree.add(5));
        assertTrue(tree.contains(5));

        assertTrue(tree.add(12));
        assertTrue(tree.contains(12));
        assertTrue(tree.add(8));
        assertTrue(tree.add(3));
        assertTrue(tree.add(20));

        assertTrue(tree.add(4));
        assertTrue(tree.add(2));
        assertTrue(tree.add(1));
        assertTrue(tree.add(50));
        assertTrue(tree.add(37));

        assertTrue(tree.contains(5));
        assertTrue(tree.contains(3));
        assertTrue(tree.contains(12));
        assertTrue(tree.contains(2));
        assertTrue(tree.contains(4));
        assertTrue(tree.contains(8));
        assertTrue(tree.contains(20));
        assertTrue(tree.contains(1));
        assertTrue(tree.contains(50));
        assertTrue(tree.contains(37));

        assertFalse(tree.contains(-1));
        assertFalse(tree.contains(6));
        assertFalse(tree.contains(10));
        assertFalse(tree.contains(51));
        assertFalse(tree.contains(133));
    }

    @Test
    public void addLeftLeftRebalance() {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.add(10);
        tree.add(7);

        tree.add(15);
        tree.add(3);
        tree.add(9);

        tree.add(2);

        assertEquals(7, tree.root.getValue());
        assertSame(null, tree.root.getParent());
        assertEquals(3, tree.root.getHeight());
        assertEquals(0, tree.root.getBalance());

        AVLTree.Node<Integer> left = tree.root.getLeft();
        assertEquals(3, left.getValue());

        AVLTree.Node<Integer> right = tree.root.getRight();
        assertEquals(10, right.getValue());
    }
}
