package com.github.paohaijiao.tree;

import java.io.Serializable;
import java.util.List;

public interface JTreeNode<T, K extends Serializable> extends Comparable<JTreeNode<T, K>> {

    K getId();

    K getParentId();

    List<T> getChildren();

    void setChildren(List<T> children);

    /**
     * 获取节点的排序值
     *
     * @return 排序值，数值越小越靠前
     */
    default Integer getSortOrder() {
        return 0;
    }

    /**
     * 默认比较方法，根据排序值进行比较
     */
    @Override
    default int compareTo(JTreeNode<T, K> other) {
        if (other == null) return -1;
        Integer thisOrder = this.getSortOrder();
        Integer otherOrder = other.getSortOrder();
        if (thisOrder == null && otherOrder == null) return 0;
        if (thisOrder == null) return 1;
        if (otherOrder == null) return -1;
        return thisOrder.compareTo(otherOrder);
    }
}
