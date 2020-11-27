package com.max.app.tree;

public final class AVLTree<T extends Comparable<T>> {

    Node<T> root;

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
//                    System.out.println("LEFT-LEFT case");
                    rotateRight(cur, parent, true);
                }
                else if (cur.left.balance == -1) {
                    // 1.2. left-right case
//                    System.out.println("LEFT-RIGHT case");
                    rotateLeft(cur.left, cur, true);
                    rotateRight(cur, parent, true);
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
//                    System.out.println("RIGHT-RIGHT case");
                    rotateLeft(cur, parent, false);
                }
                else if (cur.right.balance == 1) {
                    // 2.2. right-left case
//                    System.out.println("RIGHT-LEFT case");
                    rotateRight(cur.right, cur, false);
                    rotateLeft(cur, parent, false);
                }
                else {
                    throw new IllegalStateException(String.format("Undefined rotation case, not RIGHT-RIGHT or RIGHT-LEFT: " +
                                                                          "cur %s, cur.left: %s, cur.right: %s",
                                                                  cur, cur.left, cur.right));
                }
            }

            cur = parent;
        }
    }

    private void rotateLeft(Node<T> cur, Node<T> parent, boolean baseRotationLeft) {

        Node<T> mainNode = cur.right;

        cur.right = mainNode.left;
        updateParent(mainNode.left, cur);
        cur.recalculateHeightAndBalance();

        mainNode.left = cur;
        updateParent(cur, mainNode);
        mainNode.recalculateHeightAndBalance();

        if (parent == null) {
            root = mainNode;
            mainNode.parent = null;
        }
        else {
            if (baseRotationLeft) {
                parent.left = mainNode;
            }
            else {
                parent.right = mainNode;
            }
            updateParent(mainNode, parent);
            parent.recalculateHeightAndBalance();
        }

    }

    private void rotateRight(Node<T> cur, Node<T> parent, boolean baseRotationLeft) {
        Node<T> mainNode = cur.left;

        cur.left = mainNode.right;
        updateParent(mainNode.right, cur);

        mainNode.right = cur;
        updateParent(mainNode.right, mainNode);

        cur.recalculateHeightAndBalance();
        mainNode.recalculateHeightAndBalance();

        if (parent == null) {
            root = mainNode;
            mainNode.parent = null;
        }
        else {
            if (baseRotationLeft) {
                parent.left = mainNode;
            }
            else {
                parent.right = mainNode;
            }
            updateParent(mainNode, parent);
            parent.recalculateHeightAndBalance();
        }
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

    static final class Node<U> {
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

        U getValue() {
            return value;
        }

        int getHeight() {
            return height;
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
