package com.bit.patcher;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Liang
 * @version 1.0
 * @date 2020/12/1 16:00
 * Created by IntelliJ IDEA
 */
public class PatcherAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 获取工具窗口管理器
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(Objects.requireNonNull(e.getProject()));
        // 获取指定的工具窗口
        ToolWindow myToolWindow = toolWindowManager.getToolWindow(PatcherBundle.message("patcher.name"));
        // 显示或隐藏工具窗口
        assert myToolWindow != null;
        if (!myToolWindow.isVisible()) {
            myToolWindow.show(null);
        }
    }
}
