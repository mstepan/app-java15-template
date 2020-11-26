package com.max.app.tree;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AVLTreeTest {

    @Test
    public void addAndContains() {
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
    }
}
