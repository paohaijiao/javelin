package com.github.paohaijiao.tree;

import com.github.paohaijiao.function.JBiConsumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JTreeUtil {

    public static <T, K extends Serializable> List<T> build(List<T> list, Function<T, K> idGetter, Function<T, K> parentGetter, Function<T, List<T>> childrenGetter, JBiConsumer<T, List<T>> childrenSetter, K rootParentId) {
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
        return roots;
    }


    public static <T extends JTreeNode<T, K>, K extends Serializable> List<T> build(List<T> list, K rootParentId) {
        return build(list,
                JTreeNode::getId,
                JTreeNode::getParentId,
                JTreeNode::getChildren,
                JTreeNode::setChildren,
                rootParentId);
    }

    public static <T> List<T> flatten(List<T> roots, Function<T, List<T>> childrenGetter) {
        List<T> result = new ArrayList<>();
        flatten(roots, childrenGetter, result);
        return result;
    }

    private static <T> void flatten(List<T> nodes, Function<T, List<T>> childrenGetter, List<T> result) {
        for (T node : nodes) {
            result.add(node);
            List<T> children = childrenGetter.apply(node);
            if (children != null && !children.isEmpty()) {
                flatten(children, childrenGetter, result);
            }
        }
    }
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
    public static <T> List<T> getDirectChildren(T node, Function<T, List<T>> childrenGetter) {
        if (node == null) {
            return Collections.emptyList();
        }
        List<T> children = childrenGetter.apply(node);
        return children != null ? new ArrayList<>(children) : Collections.emptyList();
    }
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
    public static <T, K> List<T> getParentChain(T node, K nodeId, Function<T, K> idGetter, Function<T, K> parentGetter, Map<K, T> nodeMap, boolean includeCurrent) {
        List<T> parents = getParents(node, nodeId, idGetter, parentGetter, nodeMap, includeCurrent);
        Collections.reverse(parents);
        return parents;
    }
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
