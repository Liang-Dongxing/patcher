package com.bit.patcher;

import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
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
    private ComboBox<PatcherModuleType> moduleTypeComboBox;
    private JPanel moduleNamePanel;
    private ComboBox<String> moduleNameComboBox;
    private JPanel savePathPanel;
    private TextFieldWithBrowseButton savePathTextFieldWithBrowseButton;
    private JPanel saveFilesPanel;
    private JPanel otherPanel;
    private JBCheckBox exportTheSourceCodeJbCheckBox;
    private JBCheckBox deleteOldPatcherFilesJbCheckBox;
    private JButton exportButton;
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
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(PVFUtils.getSaveFilesTree());
        decorator.setAddAction(anActionButton -> {
            // 处理添加操作
            TreeFileChooser treeFileChooser = TreeFileChooserFactory.getInstance(project)
                    .createFileChooser(PatcherBundle.message("patcher.value.3"), null, null, null);
            treeFileChooser.showDialog();
            // 获取选中的文件
            PsiFile selectedFile = treeFileChooser.getSelectedFile();
            if (selectedFile != null) {
                // 获取选中文件的模块
                Module moduleForFile = ModuleUtil.findModuleForFile(selectedFile.getVirtualFile(), project);
                assert moduleForFile != null;
                // 创建 PatcherVirtualFile 对象
                PatcherVirtualFile patcherVirtualFile = PatcherVirtualFile.builder()
                        .virtualFile(selectedFile.getVirtualFile())
                        .path(selectedFile.getVirtualFile().getPath())
                        .name(selectedFile.getVirtualFile().getName())
                        .moduleName(moduleForFile.getName())
                        .module(moduleForFile)
                        .build();
                // 添加到 map 中
                PVFUtils.setVirtualFilesMapValue(patcherVirtualFile);
                PVFUtils.setPatcherFileTree();
            }
        });
        decorator.setEditAction(anActionButton -> {
            // 获取选中的节点
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) PVFUtils.getSaveFilesTree().getLastSelectedPathComponent();
            if (defaultMutableTreeNode.getUserObject() instanceof PatcherVirtualFile patcherVirtualFile) {
                // 打开并激活编辑器
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                fileEditorManager.openFile(patcherVirtualFile.getVirtualFile(), true);
            }
        });
        decorator.setRemoveAction(anActionButton -> {
            // 获取选中的节点
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) PVFUtils.getSaveFilesTree().getLastSelectedPathComponent();
            if (defaultMutableTreeNode.getUserObject() instanceof PatcherVirtualFile patcherVirtualFile) {
                // 从 map 中移除
                PVFUtils.removeVirtualFilesMapValue(patcherVirtualFile);
                PVFUtils.setPatcherFileTree();
            }
        });

        saveFilesPanel.add(decorator.createPanel(), BorderLayout.CENTER);
    }
}

