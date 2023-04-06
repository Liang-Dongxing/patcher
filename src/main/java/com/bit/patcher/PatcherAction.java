package com.bit.patcher;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

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
        ToolWindow toolWindow = toolWindowManager.getToolWindow(PatcherBundle.message("toolwindow.stripe.Patcher"));
        // 显示或隐藏工具窗口
        assert toolWindow != null;
        if (!toolWindow.isVisible()) {
            toolWindow.show(null);
        }
        // 获取当前选中的文件
        VirtualFile[] virtualFiles = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        // 将当前选中的文件按照 Module 分组
        PVFUtils.getVirtualFilesMap().clear();
        assert virtualFiles != null;
        for (VirtualFile virtualFile : virtualFiles) {
            PatcherUtils.getExportFile(anActionEvent.getProject(), virtualFile);
        }
        PVFUtils.setPatcherFileTree();
    }
}