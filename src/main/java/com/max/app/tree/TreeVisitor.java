package com.max.app.tree;

public interface TreeVisitor <U> {

    void visitNode(AVLTree.Node<U> node);

}
