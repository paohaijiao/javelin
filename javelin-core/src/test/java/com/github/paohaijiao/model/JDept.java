package com.github.paohaijiao.model;

import com.github.paohaijiao.tree.JTreeNode;
import lombok.Data;

import java.util.List;

@Data
public class JDept implements JTreeNode<JDept, Long> {
    private Long id;
    private Long parentId;
    private String name;
    private List<JDept> children;
    public JDept(Long id, Long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public List<JDept> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<JDept> children) {
        this.children = children;
    }
}
