package com.dialog;

import com.intellij.execution.ui.JrePathEditor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vcs.VcsShowConfirmationOptionImpl;
import com.intellij.openapi.vcs.changes.FilePathsHelper;
import com.intellij.openapi.vcs.changes.ui.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xml.internal.security.Init;
import org.bouncycastle.util.io.Streams;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Liang
 */
public class PatcherDialog extends JDialog {

    private JPanel contentPane;

    private JTextField moduleName;
    private JTextField webPath;
    private JTextField savePath;
    private JButton browseButton;
    private JBList<String> fileList;

    private JButton buttonOK;
    private JButton buttonCancel;

    private Module module;
    private Project project;

    /**
     * 初始化 Pane 内容
     *
     * @param event
     */
    private void initPane(AnActionEvent event) {

        // 获取当前Module对象
        module = event.getData(DataKeys.MODULE);

        project = event.getProject();
        setTitle("Create Patcher Dialog");
        setContentPane(contentPane);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getRootPane().setDefaultButton(buttonOK);
        // 设置模块名称
        moduleName.setText(module.getName());
        // 设置保存路径
        savePath.setText(Paths.get(System.getProperty("user.home"), "Desktop").toString());
        // 获取需要打补丁的文件列表
        VirtualFile[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        String[] fileArray = new String[data.length];
        Arrays.stream(data).map(x -> x.getPath().substring(x.getPath().indexOf(module.getName()))).collect(Collectors.toList()).toArray(fileArray);
        fileList.setListData(fileArray);
        fileList.setEmptyText("No File Selected!");
    }


    public PatcherDialog(AnActionEvent event) {
        initPane(event);

        // 保存路径按钮事件
        browseButton.addActionListener(e -> {

        });

        buttonOK.addActionListener(e -> dispose());

        buttonCancel.addActionListener(e -> dispose());

    }


}
