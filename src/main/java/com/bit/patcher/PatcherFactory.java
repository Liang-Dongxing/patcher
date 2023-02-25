package com.bit.patcher;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Liang
 * @version 1.0
 * @date 2020/12/1 16:00
 * Created by IntelliJ IDEA
 * <p>
 * 工具窗口工厂类
 */
public class PatcherFactory implements ToolWindowFactory {
    @Override
    public boolean isApplicable(@NotNull Project project) {
        return ToolWindowFactory.super.isApplicable(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        PatcherToolWindow patcherToolWindow = new PatcherToolWindow(project, toolWindow);
        PatcherUtils.setPatcherToolWindow(patcherToolWindow);
        PatcherUtils.setModuleNameComboBox(project, patcherToolWindow.getModuleNameComboBox());
        PatcherUtils.setLibraryComboBox(project, patcherToolWindow.getModuleTypeComboBox());
        PatcherUtils.setDavePathDefault(patcherToolWindow.getSavePathTextFieldWithBrowseButton());
        PatcherUtils.setBrowseFolderListener(project, patcherToolWindow.getSavePathTextFieldWithBrowseButton());
        // 创建工具窗口内容
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(patcherToolWindow.getContent(), PatcherBundle.message("patcher.display.name"), false);
        // 将内容添加到工具窗口
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        ToolWindowFactory.super.init(toolWindow);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return ToolWindowFactory.super.shouldBeAvailable(project);
    }

    @Override
    public @Nullable ToolWindowAnchor getAnchor() {
        return ToolWindowFactory.super.getAnchor();
    }

    @Override
    public @Nullable Icon getIcon() {
        return ToolWindowFactory.super.getIcon();
    }
}
