package com.max.app.tree;

public final class AVLTree<T extends Comparable<T>> {

    private Node<T> root;

    public boolean add(T value) {

        checkNotNull(value, "Can't add NULL value");

        if (root == null) {
            root = new Node<>(value);
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

        retrace(newNode);

        return true;
    }

    /**
     * retrace path from leaf to root, checking AVL property for each node
     */
    private void retrace(Node<T> cur) {

        // update height and balance
        updateHeightAndBalance(cur);

        // check AVL properties

    }

    private void updateHeightAndBalance(Node<T> from) {

        Node<T> cur = from;

        while (cur != null) {
            cur.height = Math.max(cur.leftHeight(), cur.rightHeight()) + 1;
            cur.balance = cur.leftHeight() - cur.rightHeight();
            cur = cur.parent;
        }
    }

    public boolean contains(T value) {
        checkNotNull(value, "Can't search for NULL value");

        Node<T> foundNode = findNodeOrParent(value);

        return foundNode.value.compareTo(value) == 0;
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

    private static final class Node<U> {
        private final U value;
        private Node<U> left;
        private Node<U> right;
        private Node<U> parent;
        private int height = 1;
        private int balance = 0;

        static <U> Node<U> withParent(U value, Node<U> parent) {
            Node<U> node = new Node<>(value);
            node.parent = parent;
            return node;
        }

        Node(U value) {
            this.value = value;
        }

        int leftHeight() {
            return left == null ? 0 : left.height;
        }

        int rightHeight() {
            return right == null ? 0 : right.height;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
