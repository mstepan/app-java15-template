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
    private void retrace(Node<T> from) {

        Node<T> cur = from;

        while (cur != null) {

            // update height and balance
            cur.recalculateHeightAndBalance();

            Node<T> parent = cur.parent;

            // check AVL properties
            if (cur.balance == 2) {

                // 1.1. left-left case
                if (cur.left.balance == 1) {
                    System.out.println("LEFT-LEFT case");

                    Node<T> newLeft = rotateRight(cur);

                    if (parent == null) {
                        root = newLeft;
                        newLeft.parent = null;
                    }
                    else {
                        parent.left = newLeft;
                        updateParent(newLeft, parent);
                    }
                }
                else {
                    // 1.2. left-right case
                    System.out.println("LEFT-RIGHT case");
                }
            }
            else if (cur.balance == -2) {
                //TODO:
                // 2.1. right-right case
                System.out.println("RIGHT-RIGHT case");

                // 2.2. right-left case
                System.out.println("RIGHT-LEFT case");
            }

            cur = parent;
        }
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> temp = node.left;

        node.left = temp.right;
        updateParent(temp.right, node);

        temp.right = node;
        updateParent(temp.right, temp);

        node.recalculateHeightAndBalance();
        temp.recalculateHeightAndBalance();

        return temp;
    }

    private void updateParent(Node<T> node, Node<T> parent) {
        if (node == null) {
            return;
        }

        node.parent = parent;
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

        void recalculateHeightAndBalance() {
            height = Math.max(leftHeight(), rightHeight()) + 1;
            balance = leftHeight() - rightHeight();
        }

        @Override
        public String toString() {
            return String.format("%s, h = %d, b = %d", value, height, balance);
        }
    }
}
