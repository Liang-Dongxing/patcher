package com.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.sun.org.apache.xml.internal.security.Init;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Liang
 */
public class PatcherDialog extends JDialog {

    private JPanel contentPane;

    private JTextField moduleName;
    private JTextField savePath;
    private JButton browseButton;
    private JBList<VirtualFile> fileList;

    private JButton buttonOK;
    private JButton buttonCancel;

    private Module module;

    /**
     * 初始化 Pane 内容
     *
     * @param event
     */
    private void initPane(AnActionEvent event) {
        setTitle("Create Patcher Dialog");
        setContentPane(contentPane);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getRootPane().setDefaultButton(buttonOK);
        // 获取当前Module对象
        module = event.getData(DataKeys.MODULE);
        // 设置模块名称
        moduleName.setText(module.getName());
        // 设置保存路径
        savePath.setText(Paths.get(System.getProperty("user.home"), "Desktop").toString());
        // 获取需要打补丁的文件
        VirtualFile[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        fileList.setListData(data);
        fileList.setEmptyText("No File Selected!");
    }


    public PatcherDialog(AnActionEvent event) {
        initPane(event);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
