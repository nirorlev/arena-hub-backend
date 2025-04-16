
package com.threeatom.utils;

import com.threeatom.utils.data.TreeNode;
import com.threeatom.utils.data.TreeNodeEntity;
import java.util.Iterator;
import java.util.List;

public class TreeUtil {
    public TreeUtil() {}

    public static <T extends TreeNodeEntity> TreeNode<T> createTree(
            List<T> list, TreeNodeEntity root) {
        TreeNode<T> trees = new TreeNode(root);
        Iterator var3 = list.iterator();

        while (var3.hasNext()) {
            TreeNodeEntity it = (TreeNodeEntity) var3.next();
            if (root.getId().equals(it.getFid())) {
                trees.getChildren().add(createTree(list, it));
            }
        }

        return trees;
    }
}
