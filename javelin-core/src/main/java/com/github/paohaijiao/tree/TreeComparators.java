/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.tree;
import java.io.Serializable;
import java.util.Comparator;

/**
 * 树节点比较器工具类
 */
public class TreeComparators {

    /**
     * 根据排序值排序（升序）
     */
    public static <T extends JTreeNode<T, ? extends Serializable>> Comparator<T> bySortOrder() {
        return Comparator.comparing(JTreeNode::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    /**
     * 根据排序值排序（降序）
     */
    public static <T extends JTreeNode<T, ? extends Serializable>> Comparator<T> bySortOrderDesc() {
        return Comparator.comparing(JTreeNode::getSortOrder, Comparator.nullsFirst(Comparator.reverseOrder()));
    }

    /**
     * 根据ID排序（ID需要实现Comparable接口）
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable & Comparable<K>> Comparator<T> byId() {
        return Comparator.comparing(JTreeNode::getId, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    /**
     * 根据ID排序（降序）
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable & Comparable<K>> Comparator<T> byIdDesc() {
        return Comparator.comparing(JTreeNode::getId, Comparator.nullsFirst(Comparator.reverseOrder()));
    }

    /**
     * 链式比较器：先按排序值，再按ID
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable & Comparable<K>> Comparator<T> bySortOrderThenId() {
        return Comparator.<T, Integer>comparing(JTreeNode::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(byId());
    }

    /**
     * 链式比较器：先按排序值，再按ID（降序）
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable & Comparable<K>> Comparator<T> bySortOrderThenIdDesc() {
        return Comparator.<T, Integer>comparing(JTreeNode::getSortOrder, Comparator.nullsFirst(Comparator.reverseOrder())).thenComparing(byIdDesc());
    }
}
