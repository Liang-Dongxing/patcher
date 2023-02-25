package com.bit.patcher;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author Liang
 * @version 1.0
 * @date 2020/12/1 16:00
 * Created by IntelliJ IDEA
 * <p>
 * 工具窗口类
 */
@Getter
public class PatcherToolWindow {

    private JPanel patcherWindowContent;
    private JPanel moduleTypePanel;
    private ComboBox<String> moduleTypeComboBox;
    private JPanel moduleNamePanel;
    private ComboBox<String> moduleNameComboBox;
    private JPanel savePathPanel;
    private TextFieldWithBrowseButton savePathTextFieldWithBrowseButton;
    private JPanel saveFilesPanel;
    private JPanel otherPanel;
    private JBCheckBox exportTheSourceCodeJbCheckBox;
    private JBCheckBox deleteOldPatcherFilesJbCheckBox;
    private JButton okButton;
    private JButton selectButton;
    private final Project project;
    private final ToolWindow toolWindow;

    public PatcherToolWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        initializationSaveFilesPanel();
    }

    /**
     * 创建自定义的用户界面组件
     */
    public void createUIComponents() {

    }

    public JComponent getContent() {
        return patcherWindowContent;
    }

    /**
     * 初始化保存文件的面板
     */
    private void initializationSaveFilesPanel() {
        // 创建 ToolbarDecorator，并设置添加、编辑和删除操作
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(PatcherUtils.getSaveFilesTree());
        decorator.setAddAction(anActionButton -> {
            // 处理添加操作
        });
        decorator.setEditAction(anActionButton -> {
            // 处理编辑操作
        });
        decorator.setRemoveAction(anActionButton -> {
            // 处理删除操作
        });

        saveFilesPanel.add(decorator.createPanel(), BorderLayout.CENTER);
    }
}

