package com.threeatom.utils.data;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T extends TreeNodeEntity> {
    private T entity;
    private List<TreeNode<T>> children = new ArrayList();

    public TreeNode(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return this.entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public List<TreeNode<T>> getChildren() {
        return this.children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }
}
