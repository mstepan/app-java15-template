package com.max.app.tree;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AVLTreeTest {

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    @Test
    public void deleteRootValue() {
        Set<Integer> tree = new AVLTree<>();
        tree.add(50);

        tree.add(20);
        tree.add(120);

        tree.add(5);
        tree.add(90);
        tree.add(150);

        tree.add(80);

        assertTrue(tree.remove(50));
        assertFalse(tree.contains(50));
    }

    @Test
    public void deleteNonLeafNode() {
        Set<Integer> tree = new AVLTree<>();
        tree.add(50);

        tree.add(20);
        tree.add(120);

        tree.add(5);
        tree.add(90);
        tree.add(150);

        tree.add(80);

        assertTrue(tree.remove(120));
        assertFalse(tree.contains(120));
    }


    @Test
    public void deleteLeafNodes() {
        Set<Integer> tree = new AVLTree<>();
        tree.add(50);
        tree.add(120);
        tree.add(20);

        tree.add(90);
        tree.add(150);
        tree.add(5);

        assertTrue(tree.remove(90));
        assertFalse(tree.contains(90));

        assertTrue(tree.remove(150));
        assertFalse(tree.contains(150));

        assertTrue(tree.remove(5));
        assertFalse(tree.contains(5));

        assertTrue(tree.remove(20));
        assertFalse(tree.contains(20));

        assertTrue(tree.remove(120));
        assertFalse(tree.contains(120));

        assertTrue(tree.remove(50));
        assertFalse(tree.contains(50));

        assertEquals(0, tree.size());
    }


    @RepeatedTest(10)
    public void deleteRandomValues() {

        int[] arr = randomArray(100);

        Set<Integer> expectedSet = new TreeSet<>();
        AVLTree<Integer> actualSet = new AVLTree<>();

        for (int value : arr) {
            boolean wasAdded1 = expectedSet.add(value);
            boolean wasAdded2 = actualSet.add(value);
            assertEquals(wasAdded1, wasAdded2, "value = " + value);
        }

        for (int i = 0; i < 30; ++i) {
            int valueToDelete = arr[i];
            boolean wasDeleted1 = expectedSet.remove(valueToDelete);
            boolean wasDeleted2 = actualSet.remove(valueToDelete);
            assertEquals(wasDeleted1, wasDeleted2, "value = " + valueToDelete);
        }
    }


    @RepeatedTest(100)
    public void addRandomValues() {

        int[] arr = randomArray(10 + RAND.nextInt(1000));

        Set<Integer> expectedSet = new TreeSet<>();
        AVLTree<Integer> actualSet = new AVLTree<>();

        for (int value : arr) {
            boolean wasAdded1 = expectedSet.add(value);
            boolean wasAdded2 = actualSet.add(value);
            assertEquals(wasAdded1, wasAdded2, "value = " + value);
        }

        assertAVLTreePropertiesCorrect(actualSet);

        for (int value : arr) {
            assertTrue(actualSet.contains(value), ("value not found: " + value));
        }

        for (int it = 0; it < 1_000; ++it) {
            int randValue = RAND.nextInt();
            assertEquals(expectedSet.contains(randValue), actualSet.contains(randValue));
        }
    }

    // Check all AVL tree properties: height & max/min balance.
    private static void assertAVLTreePropertiesCorrect(AVLTree<Integer> actualTree) {

        PropertiesVisitor<Integer> visitor = new PropertiesVisitor<>();

        actualTree.visitPostOrder(visitor);

        int maxPossibleAvlHeight = (int) Math.ceil(1.44 * log2(actualTree.size()));

        assertTrue(visitor.height() <= maxPossibleAvlHeight, "Abnormal AVL tree height detected, maxPossible: " +
                maxPossibleAvlHeight + ", but found: " + visitor.height());
        assertTrue(visitor.maxBalance() < 2, "max balance value is too high: " + visitor.maxBalance());
        assertTrue(visitor.minBalance() > -2, "min balance value is too small: " + visitor.minBalance());
    }

    private static double log2(int value) {
        return Math.log10(value) / Math.log10(2.0);
    }

    private static int[] randomArray(int length) {
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt();
        }
        return arr;
    }

    @Test
    public void addNullThrowsException() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertThrows(IllegalArgumentException.class, () -> tree.add(null));
        assertEquals(0, tree.size());
    }

    @Test
    public void containsForNullReturnFalse() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(5);
        tree.add(8);
        tree.add(17);

        assertFalse(tree.contains(null));
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
    public void checkSize() {
        Set<Integer> set = new AVLTree<>();

        set.add(5);
        assertEquals(1, set.size());

        set.add(8);
        set.add(13);
        set.add(55);
        assertEquals(4, set.size());

        // add duplicate values should not change set size
        set.add(13);
        set.add(55);
        assertEquals(4, set.size());
    }

    // check AVL tree in-order iterator
    @Test
    public void exhaustedIteratorThrowsException() {
        Set<Integer> set = new AVLTree<>();
        set.add(8);
        set.add(5);
        set.add(15);

        Iterator<Integer> it = set.iterator();

        assertTrue(it.hasNext());
        assertEquals(5, it.next());
        assertTrue(it.hasNext());
        assertEquals(8, it.next());
        assertTrue(it.hasNext());
        assertEquals(15, it.next());

        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    public void iteratorThrowsExceptionIfModifiedDuringTraversal() {
        Set<Integer> set = new AVLTree<>();
        set.add(8);
        set.add(5);
        set.add(15);

        Iterator<Integer> it = set.iterator();

        assertTrue(it.hasNext());
        assertEquals(5, it.next());

        set.add(33);
        assertTrue(it.hasNext());
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    public void iterateOverSet() {
        Set<Integer> set = new AVLTree<>();
        set.add(8);
        set.add(5);
        set.add(20);
        set.add(6);
        set.add(15);
        set.add(33);

        checkIterator(new int[]{5, 6, 8, 15, 20, 33}, set.iterator());
    }

    @RepeatedTest(100)
    public void iterateOverRandomData() {

        int[] arr = randomArray(10 + RAND.nextInt(1000));

        AVLTree<Integer> set = new AVLTree<>();
        for (int value : arr) {
            set.add(value);
        }

        int[] arrCopy = Arrays.copyOf(arr, arr.length);
        Arrays.sort(arrCopy);

        checkIterator(arrCopy, set.iterator());
    }

    private static void checkIterator(int[] expected, Iterator<Integer> it) {
        for (int expectedValue : expected) {
            assertTrue(it.hasNext(), "Iterator ended earlier than expected");
            assertEquals(expectedValue, it.next(), "Incorrect value detected during iteration");
        }

        assertFalse(it.hasNext());
    }

}
