package com.bit.patcher;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Liang
 * @version 1.0
 * @date 2023/2/26 17:36
 * Created by IntelliJ IDEA
 */
public class PVFUtils {
    private final static Tree SAVE_FILES_TREE = new Tree();
    private final static Map<String, List<PatcherVirtualFile>> virtualFilesMap = new LinkedHashMap<>();


    static {
        // 设置树的根节点是否显示
        SAVE_FILES_TREE.setRootVisible(false);
    }

    /**
     * 获取保存文件的树
     *
     * @return 保存文件的树
     */
    public static Tree getSaveFilesTree() {
        return SAVE_FILES_TREE;
    }

    /**
     * 获取保存文件的树
     *
     * @return 保存文件的树
     */
    public static Map<String, List<PatcherVirtualFile>> getVirtualFilesMap() {
        return virtualFilesMap;
    }


    /**
     * 设置保存文件的Map集合
     *
     * @param virtualFile 选择的文件
     */
    public static void setVirtualFilesMapValue(PatcherVirtualFile virtualFile) {
        if (virtualFilesMap.get(virtualFile.getModuleName()) != null) {
            List<PatcherVirtualFile> virtualFiles = virtualFilesMap.get(virtualFile.getModuleName());
            if (!virtualFiles.contains(virtualFile)) {
                virtualFiles.add(virtualFile);
            }
        } else {
            List<PatcherVirtualFile> virtualFileList = new LinkedList<>();
            virtualFileList.add(virtualFile);
            virtualFilesMap.put(virtualFile.getModuleName(), virtualFileList);
        }
    }

    /**
     * 设置保存文件的树
     */
    public static void setPatcherFileTree() {
        // 设置模块下拉框的值
        if (virtualFilesMap.size() > 1) {
            PatcherUtils.getPatcherToolWindow().getModuleNameComboBox().setItem(PatcherBundle.message("patcher.value.1"));
        } else {
            for (String s : virtualFilesMap.keySet()) {
                PatcherUtils.getPatcherToolWindow().getModuleNameComboBox().setItem(s);
            }
        }
        // 创建树节点
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        // 将当前选中的文件按照 Module 分组后添加到树节点中
        virtualFilesMap.forEach((key, value) -> {
            DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(key);
            value.forEach(virtualFile -> {
                DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(virtualFile);
                moduleNode.add(fileNode);
            });
            root.add(moduleNode);
        });
        // 创建树模型
        DefaultTreeModel model = new DefaultTreeModel(root);
        // 设置树模型
        PVFUtils.getSaveFilesTree().setModel(model);
        // 展开所有节点
        PatcherUtils.expandAllNodes(PVFUtils.getSaveFilesTree(), 0, PVFUtils.getSaveFilesTree().getRowCount());
    }

    /**
     * 删除保存文件的Map集合中的值
     *
     * @param patcherVirtualFile 虚拟文件
     */
    public static void removeVirtualFilesMapValue(PatcherVirtualFile patcherVirtualFile) {
        if (virtualFilesMap.get(patcherVirtualFile.getModuleName()) != null) {
            List<PatcherVirtualFile> virtualFiles = virtualFilesMap.get(patcherVirtualFile.getModuleName());
            virtualFiles.remove(patcherVirtualFile);
            if (virtualFiles.size() == 0) {
                virtualFilesMap.remove(patcherVirtualFile.getModuleName());
            }
        }
    }

    public static void removeAllChildren() {
        ((DefaultMutableTreeNode) SAVE_FILES_TREE.getModel().getRoot()).removeAllChildren();
        ((DefaultTreeModel) SAVE_FILES_TREE.getModel()).reload();
    }
}
