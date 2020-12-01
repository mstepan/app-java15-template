package com.max.app.tree;

import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * AVLTree implementation with height tracking.
 * For each node balance factor lies in range [-1...1].
 * <p>
 * balance factor = left child height - right child height
 */
public class AVLTree<T extends Comparable<T>> extends AbstractSet<T> {

    private Node<T> root;

    private int size;
    private int modCount;

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

        retrace(newNode);
        incSize();

        return true;
    }

    @Override
    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }
        T value = (T) obj;

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

    private void incSize() {
        ++modCount;
        ++size;
    }

    /**
     * retrace path from leaf to root, checking AVL property for each node
     */
    private void retrace(Node<T> from) {

        Node<T> cur = from;

        while (cur != null) {

            // update height and balance
            cur.recalculateHeight();

            Node<T> parent = cur.parent;

            // check AVL properties
            if (cur.balance() == 2) {
                // 1.1. left-left case
                if (cur.left.balance() == 1) {
                    rotateRight(cur, parent);
                }
                else if (cur.left.balance() == -1) {
                    // 1.2. left-right case
                    rotateLeft(cur.left, cur);
                    rotateRight(cur, parent);
                }
                else {
                    throw new IllegalStateException(String.format("Undefined rotation case, not LEFT-LEFT or LEFT-RIGHT: " +
                                                                          "cur %s, cur.left: %s, cur.right: %s",
                                                                  cur, cur.left, cur.right));
                }
            }
            else if (cur.balance() == -2) {
                if (cur.right.balance() == -1) {
                    // 2.1. right-right case
                    rotateLeft(cur, parent);
                }
                else if (cur.right.balance() == 1) {
                    // 2.2. right-left case
                    rotateRight(cur.right, cur);
                    rotateLeft(cur, parent);
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

    private void rotateLeft(Node<T> cur, Node<T> parent) {
        Node<T> movedUpNode = cur.right;

        cur.right = movedUpNode.left;
        updateParent(movedUpNode.left, cur);

        movedUpNode.left = cur;
        updateParent(cur, movedUpNode);

        cur.recalculateHeight();
        movedUpNode.recalculateHeight();

        updateParentLink(parent, movedUpNode, cur);
    }

    private void rotateRight(Node<T> cur, Node<T> parent) {
        Node<T> mainNode = cur.left;

        cur.left = mainNode.right;
        updateParent(mainNode.right, cur);

        mainNode.right = cur;
        updateParent(mainNode.right, mainNode);

        cur.recalculateHeight();
        mainNode.recalculateHeight();

        updateParentLink(parent, mainNode, cur);
    }

    private void updateParentLink(Node<T> parent, Node<T> movedUpNode, Node<T> cur) {
        if (parent == null) {
            root = movedUpNode;
            movedUpNode.parent = null;
        }
        else {
            if (parent.left == cur) {
                parent.left = movedUpNode;
            }
            else {
                parent.right = movedUpNode;
            }
            updateParent(movedUpNode, parent);
            parent.recalculateHeight();
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

    // Visitor part

    /**
     * visit in post-order fashion: left-right-parent
     */
    public void visitPostOrder(TreeVisitor<T> visitor) {
        visitPostOrderRec(root, visitor);
    }

    private void visitPostOrderRec(Node<T> cur, TreeVisitor<T> visitor) {
        if (cur == null) {
            return;
        }

        if (cur.left != null) {
            visitPostOrderRec(cur.left, visitor);
        }

        if (cur.right != null) {
            visitPostOrderRec(cur.right, visitor);
        }

        visitor.visitNode(cur);
    }

    // Node part
    static final class Node<U> {

        private final U value;
        private Node<U> left;
        private Node<U> right;
        private Node<U> parent;

        private int height;

        static <U> Node<U> withParent(U value, Node<U> parent) {
            Node<U> node = new Node<>(value);
            node.parent = parent;
            return node;
        }

        void recalculateHeight() {
            height = 1 + Math.max(getHeightOrZero(left), getHeightOrZero(right));
        }

        static <U> int getHeightOrZero(Node<U> cur) {
            return cur == null ? 0 : cur.height;
        }


        int balance() {
            int leftHeight = (left == null) ? 0 : left.height;
            int rightHeight = (right == null) ? 0 : right.height;
            return leftHeight - rightHeight;
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

        public int getHeight() {
            return height;
        }

        public int leftHeight() {
            return left == null ? 0 : left.getHeight();
        }

        public int rightHeight() {
            return right == null ? 0 : right.getHeight();
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
            return String.format("%s, h = %d", value, height);
        }
    }

    // Iterator part
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
