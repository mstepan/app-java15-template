package com.max.app.geeks;

import java.util.ArrayList;
import java.util.List;

final class TreeNode {

    int value;
    List<TreeNode> children;

    public TreeNode(int value) {
        this(value, new ArrayList<>());
    }

    public TreeNode(int value, List<TreeNode> children) {
        this.value = value;
        this.children = children;
    }

    boolean isLeaf() {
        return children.isEmpty();
    }

    TreeNode firstChild() {
        return children.size() > 0 ? children.get(0) : null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
