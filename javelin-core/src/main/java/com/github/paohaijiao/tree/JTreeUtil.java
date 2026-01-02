package com.github.paohaijiao.tree;

import com.github.paohaijiao.function.JBiConsumer;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JTreeUtil {

    /**
     * 构建树形结构（使用默认比较器，无排序）
     *
     * @param <T>            节点类型
     * @param <K>            节点ID类型
     * @param list           节点列表
     * @param idGetter       获取节点ID的函数
     * @param parentGetter   获取父节点ID的函数
     * @param childrenGetter 获取子节点列表的函数
     * @param childrenSetter 设置子节点列表的函数
     * @param rootParentId   根节点的父ID
     * @return 树的根节点列表
     */
    public static <T, K extends Serializable> List<T> build(List<T> list, Function<T, K> idGetter, Function<T, K> parentGetter, Function<T, List<T>> childrenGetter, JBiConsumer<T, List<T>> childrenSetter, K rootParentId) {
        return build(list, idGetter, parentGetter, childrenGetter, childrenSetter, rootParentId, null);
    }

    /**
     * 构建树形结构（使用指定比较器）
     *
     * @param <T>            节点类型
     * @param <K>            节点ID类型
     * @param list           节点列表
     * @param idGetter       获取节点ID的函数
     * @param parentGetter   获取父节点ID的函数
     * @param childrenGetter 获取子节点列表的函数
     * @param childrenSetter 设置子节点列表的函数
     * @param rootParentId   根节点的父ID
     * @param comparator     节点比较器（为null时不排序）
     * @return 树的根节点列表
     */
    public static <T, K extends Serializable> List<T> build(List<T> list, Function<T, K> idGetter, Function<T, K> parentGetter, Function<T, List<T>> childrenGetter, JBiConsumer<T, List<T>> childrenSetter, K rootParentId, Comparator<T> comparator) {
        Map<K, T> nodeMap = list.stream().collect(Collectors.toMap(idGetter, node -> node));
        List<T> roots = new ArrayList<>();
        for (T node : list) {
            K parentId = parentGetter.apply(node);
            if (rootParentId.equals(parentId)) {
                roots.add(node);
            } else {
                T parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    List<T> children = childrenGetter.apply(parentNode);
                    if (children == null) {
                        children = new ArrayList<>();
                        childrenSetter.accept(parentNode, children);
                    }
                    children.add(node);
                }
            }
        }
        if (comparator != null) {
            sortTree(roots, childrenGetter, childrenSetter, comparator);
        }

        return roots;
    }

    /**
     * 构建树形结构（针对JTreeNode接口，使用默认比较器）
     *
     * @param <T>          节点类型（需实现JTreeNode接口）
     * @param <K>          节点ID类型
     * @param list         节点列表
     * @param rootParentId 根节点的父ID
     * @return 树的根节点列表
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable> List<T> build(List<T> list, K rootParentId) {
        return build(list, rootParentId, null);
    }

    /**
     * 构建树形结构（针对JTreeNode接口，使用指定比较器）
     *
     * @param <T>          节点类型（需实现JTreeNode接口）
     * @param <K>          节点ID类型
     * @param list         节点列表
     * @param rootParentId 根节点的父ID
     * @param comparator   节点比较器（为null时使用自然顺序）
     * @return 树的根节点列表
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable> List<T> build(List<T> list, K rootParentId, Comparator<T> comparator) {
        return build(list,
                JTreeNode::getId,
                JTreeNode::getParentId,
                JTreeNode::getChildren,
                JTreeNode::setChildren,
                rootParentId,
                comparator != null ? comparator : Comparator.naturalOrder());
    }

    /**
     * 对树进行递归排序
     *
     * @param <T>            节点类型
     * @param nodes          节点列表
     * @param childrenGetter 获取子节点列表的函数
     * @param childrenSetter 设置子节点列表的函数
     * @param comparator     节点比较器
     */
    public static <T> void sortTree(List<T> nodes, Function<T, List<T>> childrenGetter, JBiConsumer<T, List<T>> childrenSetter, Comparator<T> comparator) {
        if (nodes == null || nodes.isEmpty() || comparator == null) {
            return;
        }
        nodes.sort(comparator);// 排序当前层级的节点
        for (T node : nodes) {// 递归排序子节点
            List<T> children = childrenGetter.apply(node);
            if (children != null && !children.isEmpty()) {
                sortTree(children, childrenGetter, childrenSetter, comparator);
                childrenSetter.accept(node, children);// 重新设置已排序的子节点
            }
        }
    }

    /**
     * 对树进行递归排序（使用自然顺序比较器）
     *
     * @param <T>            节点类型（需实现Comparable接口）
     * @param nodes          节点列表
     * @param childrenGetter 获取子节点列表的函数
     * @param childrenSetter 设置子节点列表的函数
     */
    public static <T extends Comparable<? super T>> void sortTree(List<T> nodes, Function<T, List<T>> childrenGetter, JBiConsumer<T, List<T>> childrenSetter) {
        sortTree(nodes, childrenGetter, childrenSetter, Comparator.naturalOrder());
    }

    /**
     * 对树进行递归排序（针对JTreeNode接口，使用自然顺序）
     *
     * @param <T>   节点类型（需实现JTreeNode接口）
     * @param <K>   节点ID类型
     * @param nodes 节点列表
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable> void sortTree(List<T> nodes) {
        sortTree(nodes, Comparator.naturalOrder());
    }

    /**
     * 对树进行递归排序（针对JTreeNode接口，使用指定比较器）
     *
     * @param <T>        节点类型（需实现JTreeNode接口）
     * @param <K>        节点ID类型
     * @param nodes      节点列表
     * @param comparator 节点比较器
     */
    public static <T extends JTreeNode<T, K>, K extends Serializable> void sortTree(List<T> nodes, Comparator<T> comparator) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        if (comparator != null) {// 排序当前层级的节点
            nodes.sort(comparator);
        }
        for (T node : nodes) {
            List<T> children = node.getChildren();// 递归排序子节点
            if (children != null && !children.isEmpty()) {
                sortTree(children, comparator);
                node.setChildren(children);// 重新设置子节点顺序
            }
        }
    }

    /**
     * 扁平化树结构
     *
     * @param <T>            节点类型
     * @param roots          根节点列表
     * @param childrenGetter 获取子节点列表的函数
     * @return 扁平化后的节点列表（深度优先顺序）
     */
    public static <T> List<T> flatten(List<T> roots, Function<T, List<T>> childrenGetter) {
        List<T> result = new ArrayList<>();
        flatten(roots, childrenGetter, result);
        return result;
    }

    /**
     * 递归扁平化树结构
     */
    private static <T> void flatten(List<T> nodes, Function<T, List<T>> childrenGetter, List<T> result) {
        for (T node : nodes) {
            result.add(node);
            List<T> children = childrenGetter.apply(node);
            if (children != null && !children.isEmpty()) {
                flatten(children, childrenGetter, result);
            }
        }
    }

    /**
     * 在树中查找指定ID的节点
     *
     * @param <T>            节点类型
     * @param <K>            节点ID类型
     * @param roots          根节点列表
     * @param id             要查找的节点ID
     * @param idGetter       获取节点ID的函数
     * @param childrenGetter 获取子节点列表的函数
     * @return 找到的节点，未找到返回null
     */
    public static <T, K> T findNode(List<T> roots, K id, Function<T, K> idGetter, Function<T, List<T>> childrenGetter) {
        for (T root : roots) {
            if (id.equals(idGetter.apply(root))) {
                return root;
            }
            List<T> children = childrenGetter.apply(root);
            if (children != null && !children.isEmpty()) {
                T found = findNode(children, id, idGetter, childrenGetter);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * 获取节点的所有后代节点（包括子节点、孙子节点等）
     *
     * @param <T>            节点类型
     * @param <K>            节点ID类型
     * @param node           起始节点
     * @param childrenGetter 获取子节点列表的函数
     * @return 所有后代节点列表
     */
    public static <T, K> List<T> getAllChildren(T node, Function<T, List<T>> childrenGetter) {
        List<T> result = new ArrayList<>();
        if (node == null) {
            return result;
        }
        List<T> children = childrenGetter.apply(node);
        if (children != null && !children.isEmpty()) {
            result.addAll(children);
            for (T child : children) {
                result.addAll(getAllChildren(child, childrenGetter));
            }
        }
        return result;
    }

    /**
     * 获取节点的直接子节点
     *
     * @param <T>            节点类型
     * @param node           节点
     * @param childrenGetter 获取子节点列表的函数
     * @return 直接子节点列表
     */
    public static <T> List<T> getDirectChildren(T node, Function<T, List<T>> childrenGetter) {
        if (node == null) {
            return Collections.emptyList();
        }
        List<T> children = childrenGetter.apply(node);
        return children != null ? new ArrayList<>(children) : Collections.emptyList();
    }

    /**
     * 获取节点的所有祖先节点（从当前节点向上到根节点）
     *
     * @param <T>          节点类型
     * @param <K>          节点ID类型
     * @param node         起始节点
     * @param nodeId       节点ID（当node为null时使用）
     * @param idGetter     获取节点ID的函数
     * @param parentGetter 获取父节点ID的函数
     * @param nodeMap      节点ID到节点的映射
     * @param includeCurrent 是否包含当前节点
     * @return 祖先节点列表（从当前节点向上）
     */
    public static <T, K> List<T> getParents(T node, K nodeId, Function<T, K> idGetter, Function<T, K> parentGetter, Map<K, T> nodeMap, boolean includeCurrent) {
        List<T> parents = new ArrayList<>();
        if (node == null && nodeId == null) {
            return parents;
        }
        T currentNode = node;
        if (currentNode == null) {
            currentNode = nodeMap.get(nodeId);
        }
        if (includeCurrent && currentNode != null) {
            parents.add(currentNode);
        }
        while (currentNode != null) {
            K parentId = parentGetter.apply(currentNode);
            if (parentId == null) {
                break;
            }
            T parentNode = nodeMap.get(parentId);
            if (parentNode == null) {
                break;
            }
            parents.add(parentNode);
            currentNode = parentNode;
        }

        return parents;
    }

    /**
     * 获取节点的祖先链（从根节点向下到当前节点）
     *
     * @param <T>          节点类型
     * @param <K>          节点ID类型
     * @param node         起始节点
     * @param nodeId       节点ID（当node为null时使用）
     * @param idGetter     获取节点ID的函数
     * @param parentGetter 获取父节点ID的函数
     * @param nodeMap      节点ID到节点的映射
     * @param includeCurrent 是否包含当前节点
     * @return 祖先链列表（从根节点向下）
     */
    public static <T, K> List<T> getParentChain(T node, K nodeId, Function<T, K> idGetter, Function<T, K> parentGetter, Map<K, T> nodeMap, boolean includeCurrent) {
        List<T> parents = getParents(node, nodeId, idGetter, parentGetter, nodeMap, includeCurrent);
        Collections.reverse(parents);
        return parents;
    }

    /**
     * 获取节点的兄弟节点
     *
     * @param <T>            节点类型
     * @param <K>            节点ID类型
     * @param node           当前节点
     * @param nodeId         节点ID（当node为null时使用）
     * @param idGetter       获取节点ID的函数
     * @param parentGetter   获取父节点ID的函数
     * @param childrenGetter 获取子节点列表的函数
     * @param nodeMap        节点ID到节点的映射
     * @param includeSelf    是否包含当前节点自身
     * @return 兄弟节点列表
     */
    public static <T, K> List<T> getSiblings(T node, K nodeId, Function<T, K> idGetter, Function<T, K> parentGetter, Function<T, List<T>> childrenGetter, Map<K, T> nodeMap, boolean includeSelf) {
        List<T> siblings = new ArrayList<>();
        if (node == null && nodeId == null) {
            return siblings;
        }
        T currentNode = node;
        if (currentNode == null) {
            currentNode = nodeMap.get(nodeId);
        }
        if (currentNode == null) {
            return siblings;
        }
        K parentId = parentGetter.apply(currentNode);
        if (parentId == null) {
            return includeSelf ? Collections.singletonList(currentNode) : Collections.emptyList();
        }

        T parentNode = nodeMap.get(parentId);
        if (parentNode == null) {
            return includeSelf ? Collections.singletonList(currentNode) : Collections.emptyList();
        }

        List<T> children = childrenGetter.apply(parentNode);
        if (children != null) {
            for (T child : children) {
                if (includeSelf || !idGetter.apply(child).equals(idGetter.apply(currentNode))) {
                    siblings.add(child);
                }
            }
        }
        return siblings;
    }
}
