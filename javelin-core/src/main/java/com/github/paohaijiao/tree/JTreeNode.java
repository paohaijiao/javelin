package com.github.paohaijiao.tree;

import java.io.Serializable;
import java.util.List;

public interface JTreeNode<T, K extends Serializable> {

    K getId();

    K getParentId();

    List<T> getChildren();

    void setChildren(List<T> children);
}
