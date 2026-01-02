package com.github.paohaijiao;

import com.github.paohaijiao.model.JDept;
import com.github.paohaijiao.tree.JSimpleTreeNode;
import com.github.paohaijiao.tree.JTreeUtil;
import com.github.paohaijiao.tree.TreeComparators;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Ignore
public class JTestTree {
    @Test
    public void length() throws IOException {
        List<JDept> deptList = new ArrayList<>();
        deptList.add(new JDept(1L, 0L, "总公司"));
        deptList.add(new JDept(2L, 1L, "技术部"));
        deptList.add(new JDept(3L, 1L, "市场部"));
        deptList.add(new JDept(4L, 2L, "后端组"));
        deptList.add(new JDept(5L, 2L, "前端组"));
        List<JDept> tree = JTreeUtil.build(deptList, 0L);
        List<JDept> flattenList = JTreeUtil.flatten(tree, JDept::getChildren);
        JDept node = JTreeUtil.findNode(tree, 2L, JDept::getId, JDept::getChildren);
        System.out.println(node);
    }

    @Test
    public void length1() throws IOException {
        List<JDept> deptList = new ArrayList<>();
        deptList.add(new JDept(1L, 0L, "总公司"));
        deptList.add(new JDept(2L, 1L, "技术部"));
        deptList.add(new JDept(3L, 1L, "市场部"));
        deptList.add(new JDept(4L, 2L, "后端组"));
        deptList.add(new JDept(5L, 2L, "前端组"));
        deptList.add(new JDept(6L, 4L, "程序员小李"));
        deptList.add(new JDept(7L, 4L, "程序员小网"));
        deptList.add(new JDept(8L, 5L, "售后1"));
        deptList.add(new JDept(9L, 5L, "售后2"));
        deptList.add(new JDept(10L, 6L, "程序员小李的孩子1"));
        deptList.add(new JDept(11L, 6L, "程序员小李的孩子2"));
        List<JDept> tree = JTreeUtil.build(deptList, 0L);
        Map<Long, JDept> nodeMap = deptList.stream().collect(Collectors.toMap(JDept::getId, dept -> dept));
        // 获取所有子节点
        JDept techDept = JTreeUtil.findNode(tree, 4L, JDept::getId, JDept::getChildren);
        List<JDept> allChildren = JTreeUtil.getAllChildren(techDept, JDept::getChildren);
        System.out.println("后端组所有子部门: " + allChildren.stream().map(JDept::getName).collect(Collectors.toList()));
        // 获取直接子节点
        List<JDept> directChildren = JTreeUtil.getDirectChildren(techDept, JDept::getChildren);
        System.out.println("后端组直接子部门: " + directChildren.stream().map(JDept::getName).collect(Collectors.toList()));
        // 获取所有父节点（从近到远）
        JDept backendGroup = JTreeUtil.findNode(tree, 4L, JDept::getId, JDept::getChildren);
        List<JDept> parents = JTreeUtil.getParents(backendGroup, null, JDept::getId, JDept::getParentId, nodeMap, false);
        System.out.println("后端组的上级部门: " + parents.stream().map(JDept::getName).collect(Collectors.toList()));
        // 获取兄弟节点
        List<JDept> siblings = JTreeUtil.getSiblings(backendGroup, null, JDept::getId, JDept::getParentId, JDept::getChildren, nodeMap, false);
        System.out.println("后端组的兄弟部门: " + siblings.stream().map(JDept::getName).collect(Collectors.toList()));
    }

    @Test
    public void sort() throws IOException {
        List<JSimpleTreeNode<Integer>> nodes = new ArrayList<>();
        Integer ROOT_ID = 0;
        // 根节点
        nodes.add(new JSimpleTreeNode<>(1, ROOT_ID, 3));  // 排序值3
        nodes.add(new JSimpleTreeNode<>(2, ROOT_ID, 1));  // 排序值1
        nodes.add(new JSimpleTreeNode<>(3, ROOT_ID, 2));  // 排序值2
        // 子节点
        nodes.add(new JSimpleTreeNode<>(11, 1, 5));  // 父节点1，排序值5
        nodes.add(new JSimpleTreeNode<>(12, 1, 4));  // 父节点1，排序值4
        nodes.add(new JSimpleTreeNode<>(13, 1, 6));  // 父节点1，排序值6

        nodes.add(new JSimpleTreeNode<>(21, 2, 8));  // 父节点2，排序值8
        nodes.add(new JSimpleTreeNode<>(22, 2, 7));  // 父节点2，排序值7

        nodes.add(new JSimpleTreeNode<>(31, 3, 10)); // 父节点3，排序值10
        nodes.add(new JSimpleTreeNode<>(32, 3, 9));  // 父节点3，排序值9

        // 孙子节点
        nodes.add(new JSimpleTreeNode<>(111, 11, 12)); // 父节点11，排序值12
        nodes.add(new JSimpleTreeNode<>(112, 11, 11)); // 父节点11，排序值11

        nodes.add(new JSimpleTreeNode<>(121, 12, 14)); // 父节点12，排序值14
        nodes.add(new JSimpleTreeNode<>(122, 12, 13)); // 父节点12，排序值13
        //List<JSimpleTreeNode<Integer>> tree = JTreeUtil.build(nodes, ROOT_ID);
        //List<JSimpleTreeNode<Integer>> treeq = JTreeUtil.build(nodes, ROOT_ID, TreeComparators.bySortOrder());
        List<JSimpleTreeNode<Integer>> tree2 = JTreeUtil.build(nodes, ROOT_ID, TreeComparators.bySortOrderDesc());
        System.out.println(tree2);
    }
}
