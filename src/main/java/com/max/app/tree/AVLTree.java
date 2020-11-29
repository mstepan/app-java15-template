package com.max.app.tree;

import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AVLTree<T extends Comparable<T>> extends AbstractSet<T> {

    Node<T> root;

    private int size;
    private int modCount;

    /**
     * Max possible AVL tree height = 1.44 * log2(N)
     */
    public HeightAndBalance calculateHeight() {
        return calculateStatsRec(root);
    }

    static class HeightAndBalance {

        private static final HeightAndBalance EMPTY = new HeightAndBalance(0, 0, 0);

        int maxBalance;
        int minBalance;
        int height;

        HeightAndBalance(int maxBalance, int minBalance, int height) {
            this.maxBalance = maxBalance;
            this.minBalance = minBalance;
            this.height = height;
        }
    }

    private HeightAndBalance calculateStatsRec(Node<T> cur) {
        if (cur == null) {
            return HeightAndBalance.EMPTY;
        }

        HeightAndBalance leftValue = calculateStatsRec(cur.left);
        HeightAndBalance rightValue = calculateStatsRec(cur.right);

        int curHeight = 1 + Math.max(leftValue.height, rightValue.height);
        int curBalance = leftValue.height - rightValue.height;

        int maxBalanceSoFar = max(curBalance, leftValue.maxBalance, rightValue.maxBalance);
        int minBalanceSoFar = min(curBalance, leftValue.minBalance, rightValue.minBalance);

        return new HeightAndBalance(maxBalanceSoFar, minBalanceSoFar, curHeight);
    }

    private static int max(int first, int second, int third) {
        return Math.max(Math.max(first, second), third);
    }

    private static int min(int first, int second, int third) {
        return Math.min(Math.min(first, second), third);
    }

    @Override
    public boolean add(T value) {

        checkNotNull(value, "Can't add NULL value");

        if (root == null) {
            root = new Node<>(value);
            incSize();
            return true;
        }

        Node<T> node = findNodeOrParent(value);

        // value already exists in tree
        if (node.value.compareTo(value) == 0) {
            return false;
        }

        // Add new node
        Node<T> newNode = Node.withParent(value, node);

        if (value.compareTo(node.value) < 0) {
            node.left = newNode;
        }
        else {
            node.right = newNode;
        }
        retraceAfterAdd(newNode);
        incSize();

        return true;
    }

    @Override
    public boolean contains(Object obj) {

        T value = (T) obj;

        checkNotNull(value, "Can't search for NULL value");

        Node<T> foundNode = findNodeOrParent(value);

        return foundNode.value.compareTo(value) == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator();
    }

    @Override
    public int size() {
        return size;
    }

    private Node<T> findMax(Node<T> startNode) {
        Node<T> cur = startNode;

        while (cur.right != null) {
            cur = cur.right;
        }

        return cur;
    }

    private Node<T> findMin(Node<T> startNode) {
        Node<T> cur = startNode;

        while (cur.left != null) {
            cur = cur.left;
        }

        return cur;
    }

    private void incSize() {
        ++modCount;
        ++size;
    }

    private void decSize() {
        ++modCount;
        --size;
    }

    /**
     * retrace path from leaf to root, checking AVL property for each node
     */
    private void retraceAfterAdd(Node<T> leaf) {

        Node<T> prev = leaf;
        Node<T> cur = leaf.parent;

        while (cur != null) {

            if (cur.left == prev) {
                cur.balance += 1;
            }
            else {
                cur.balance -= 1;
            }

            if (cur.balance == 0) {
                // height hasn't changed, so we can break the cycle here
                break;
            }

            Node<T> parent = cur.parent;

            // check AVL properties
            if (cur.balance == 2) {
                // 1.1. left-left case
                if (cur.left.balance == 1) {
                    rotateRight(cur, parent);
                    break;
                }
                else if (cur.left.balance == -1) {
                    // 1.2. left-right case
                    rotateLeft(cur.left, cur);
                    rotateRight(cur, parent);
                    break;
                }
                else {
                    throw new IllegalStateException(String.format("Undefined rotation case, not LEFT-LEFT or LEFT-RIGHT: " +
                                                                          "cur %s, cur.left: %s, cur.right: %s",
                                                                  cur, cur.left, cur.right));
                }
            }
            else if (cur.balance == -2) {
                if (cur.right.balance == -1) {
                    // 2.1. right-right case
                    rotateLeft(cur, parent);
                    break;
                }
                else if (cur.right.balance == 1) {
                    // 2.2. right-left case
                    rotateRight(cur.right, cur);
                    rotateLeft(cur, parent);
                    break;
                }
                else {
                    throw new IllegalStateException(String.format("Undefined rotation case, not RIGHT-RIGHT or RIGHT-LEFT: " +
                                                                          "cur %s, cur.left: %s, cur.right: %s",
                                                                  cur, cur.left, cur.right));
                }
            }

            prev = cur;
            cur = parent;
        }
    }

    private void rotateLeft(Node<T> cur, Node<T> parent) {

        Node<T> mainNode = cur.right;

        cur.right = mainNode.left;
        updateParent(mainNode.left, cur);
        cur.balance = 0;

        mainNode.left = cur;
        updateParent(cur, mainNode);
        mainNode.balance = 0;

        if (parent == null) {
            root = mainNode;
            mainNode.parent = null;
        }
        else {
            if (parent.left == cur) {
                parent.left = mainNode;
            }
            else {
                parent.right = mainNode;
            }

            updateParent(mainNode, parent);
        }
    }

    private void rotateRight(Node<T> cur, Node<T> parent) {
        Node<T> mainNode = cur.left;

        cur.left = mainNode.right;
        updateParent(mainNode.right, cur);
        cur.balance = 0;

        mainNode.right = cur;
        updateParent(mainNode.right, mainNode);
        mainNode.balance = 0;

        if (parent == null) {
            root = mainNode;
            mainNode.parent = null;
        }
        else {
            if (parent.left == cur) {
                parent.left = mainNode;
            }
            else {
                parent.right = mainNode;
            }
            updateParent(mainNode, parent);
        }
    }

    private void updateParent(Node<T> node, Node<T> parent) {
        if (node == null) {
            return;
        }

        node.parent = parent;
    }

    private Node<T> findNodeOrParent(T value) {

        Node<T> parent = root;
        Node<T> cur = root;
        int cmpRes;

        while (cur != null) {

            cmpRes = value.compareTo(cur.value);

            if (cmpRes == 0) {
                return cur;
            }

            parent = cur;
            cur = (cmpRes < 0) ? cur.left : cur.right;
        }

        return parent;
    }

    private static <U> void checkNotNull(U value, String errorMsg) {
        if (value == null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    static final class Node<U> {
        private U value;
        private Node<U> left;
        private Node<U> right;
        private Node<U> parent;

        // value can be in range [-2; 2].
        private byte balance = 0;

        static <U> Node<U> withParent(U value, Node<U> parent) {
            Node<U> node = new Node<>(value);
            node.parent = parent;
            return node;
        }

        Node(U value) {
            this.value = value;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        U getValue() {
            return value;
        }

        int getBalance() {
            return balance;
        }

        Node<U> getParent() {
            return parent;
        }

        Node<U> getLeft() {
            return left;
        }

        Node<U> getRight() {
            return right;
        }

        @Override
        public String toString() {
            return String.format("%s, b = %d", value, balance);
        }
    }

    private final class InOrderIterator implements Iterator<T> {

        private final int modCountSnapshot;

        private Node<T> cur;
        private final Deque<Node<T>> stack;

        public InOrderIterator() {
            this.modCountSnapshot = modCount;
            this.cur = root;
            this.stack = new ArrayDeque<>();
        }

        @Override
        public boolean hasNext() {
            return !(stack.isEmpty() && cur == null);
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Iterator is EMPTY");
            }

            Node<T> retNode = moveCurrent();

            return retNode.value;
        }

        private Node<T> moveCurrent() {

            if (modCountSnapshot != AVLTree.this.modCount) {
                throw new ConcurrentModificationException("AVLTree was modified during traversal");
            }

            if (cur == null) {
                cur = stack.pop();
                Node<T> res = cur;
                cur = cur.right;
                return res;
            }

            if (cur.left == null) {
                Node<T> res = cur;
                cur = cur.right;
                return res;
            }

            while (cur.left != null) {
                stack.push(cur);
                cur = cur.left;
            }

            Node<T> res = cur;
            cur = cur.right;
            return res;
        }
    }
}
