package com.bit.patcher;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Liang
 * @version 1.0
 * @date 2020/12/1 16:00
 * Created by IntelliJ IDEA
 * <p>
 * 工具窗口的Action类
 */
public class PatcherAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 获取工具窗口管理器
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(Objects.requireNonNull(anActionEvent.getProject()));
        // 获取指定的工具窗口
        ToolWindow toolWindow = toolWindowManager.getToolWindow(PatcherBundle.message("patcher.name"));
        // 显示或隐藏工具窗口
        assert toolWindow != null;
        if (!toolWindow.isVisible()) {
            toolWindow.show(null);
        }
        // 获取当前项目的路径
        String basePath = anActionEvent.getProject().getBasePath();
        // 获取当前选中的文件
        VirtualFile[] virtualFiles = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        // 将当前选中的文件按照 Module 分组
        Map<String, List<VirtualFile>> virtualFilesMap = new LinkedHashMap<>();
        assert virtualFiles != null;
        for (VirtualFile virtualFile : virtualFiles) {
            // 获取当前选中的文件所属的 Module
            Module moduleForFile = ModuleUtil.findModuleForFile(virtualFile, anActionEvent.getProject());
            // 将当前选中的文件按照 Module 分组
            assert moduleForFile != null;
            if (virtualFilesMap.get(moduleForFile.getName()) != null) {
                virtualFilesMap.get(moduleForFile.getName()).add(virtualFile);
            } else {
                List<VirtualFile> virtualFileList = new LinkedList<>();
                virtualFileList.add(virtualFile);
                virtualFilesMap.put(moduleForFile.getName(), virtualFileList);
            }
        }

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
                DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(virtualFile.getName() + "  (" + virtualFile.getPath() + ")");
                moduleNode.add(fileNode);
            });
            root.add(moduleNode);
        });
        // 创建树模型
        DefaultTreeModel model = new DefaultTreeModel(root);
        // 设置树模型
        PatcherUtils.getSaveFilesTree().setModel(model);
        // 展开所有节点
        PatcherUtils.expandAllNodes(PatcherUtils.getSaveFilesTree(), 0, PatcherUtils.getSaveFilesTree().getRowCount());

    }

}
