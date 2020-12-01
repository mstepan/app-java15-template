package com.max.app.tree;

public final class PropertiesVisitor<U> implements TreeVisitor<U> {

    private int height;
    private int minBalance = Integer.MAX_VALUE;
    private int maxBalance = Integer.MIN_VALUE;

    @Override
    public void visitNode(AVLTree.Node<U> node) {

        int curHeight = 1 + Math.max(node.leftHeight(), node.rightHeight());
        int curBalance = node.leftHeight() - node.rightHeight();

        height = Math.max(height, curHeight);
        minBalance = Math.min(minBalance, curBalance);
        maxBalance = Math.max(maxBalance, curBalance);
    }

    public int height() {
        return height;
    }

    public int minBalance() {
        return minBalance;
    }

    public int maxBalance() {
        return maxBalance;
    }
}
