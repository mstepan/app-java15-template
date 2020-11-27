package com.max.app.tree;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AVLTreeTest {

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    @Test
    public void test1(){
        int[] arr = {78, 30, 83, 52, 9, 92, 27, 5, 34, 2, 62, 36, 49, 58, 38};

        AVLTree<Integer> avlSet = new AVLTree<>();

        for(int value : arr){
            avlSet.add(value);
        }

        assertTrue(avlSet.contains(52));
    }

    @Test
    @Disabled
    public void addRandomValues() {
        AVLTree<Integer> tree = new AVLTree<>();

        int[] arr = randomArrayOfPositiveValues(20);

        Set<Integer> treeSet = new TreeSet<>();
        AVLTree<Integer> avlSet = new AVLTree<>();

        for (int value : arr) {
            boolean wasAdded1 = treeSet.add(value);
            boolean wasAdded2 = avlSet.add(value);
            assertEquals(wasAdded1, wasAdded2, "value = " + value);
        }

        for (int value : arr) {
            assertTrue(avlSet.contains(value), ("value not found: " + value));
        }

        for (int it = 0; it < 1_000; ++it) {
            int randValue = -200 + RAND.nextInt(100);
            assertFalse(avlSet.contains(randValue));
        }
    }

    private static int[] randomArrayOfPositiveValues(int length) {
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt(100);
        }

        System.out.println(Arrays.toString(arr));
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
