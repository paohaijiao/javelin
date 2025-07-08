package com.github.paohaijiao.tree;

import java.io.Serializable;
import java.util.List;

public class JSimpleTreeNode<K extends Serializable> implements JTreeNode<JSimpleTreeNode<K>, K> {

    private final K id;

    private final K parentId;

    private List<JSimpleTreeNode<K>> children;

    public JSimpleTreeNode(K id, K parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    @Override
    public K getId() {
        return id;
    }

    @Override
    public K getParentId() {
        return parentId;
    }

    @Override
    public List<JSimpleTreeNode<K>> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<JSimpleTreeNode<K>> children) {
        this.children = children;
    }

}
