package com.bit.patcher;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.treeStructure.Tree;

import java.io.File;

/**
 * @author Liang
 * @version 1.0
 * @date 2023/2/23 21:55
 * Created by IntelliJ IDEA
 */
public class PatcherUtils {

    private static final String BOOTINF = "(SpringBoot) BOOT-INF";
    private static final String WEBINF = "(Web) WEB-INF";
    private final static Tree SAVE_FILES_TREE = new Tree();
    private static PatcherToolWindow patcherToolWindow;

    static {
        // 设置树的根节点是否显示
        SAVE_FILES_TREE.setRootVisible(false);
    }

    /**
     * 获取PatcherToolWindow
     *
     * @return PatcherToolWindow
     */
    public static PatcherToolWindow getPatcherToolWindow() {
        return PatcherUtils.patcherToolWindow;
    }

    /**
     * 设置PatcherToolWindow
     *
     * @param patcherToolWindow PatcherToolWindow
     */
    public static void setPatcherToolWindow(PatcherToolWindow patcherToolWindow) {
        PatcherUtils.patcherToolWindow = patcherToolWindow;
    }

    /**
     * 把模块添加到模块的下拉框
     *
     * @param project            项目
     * @param moduleNameComboBox 模块的下拉框
     */
    public static void setModuleNameComboBox(Project project, ComboBox<String> moduleNameComboBox) {
        // 根据项目获取模块
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length > 1) {
            moduleNameComboBox.addItem(PatcherBundle.message("patcher.value.1"));
        }
        for (Module module : modules) {
            moduleNameComboBox.addItem(module.getName());
        }
    }

    /**
     * 根据依赖库添加项目类型
     *
     * @param project            项目
     * @param moduleTypeComboBox 项目类型的下拉框
     */
    public static void setLibraryComboBox(Project project, ComboBox<String> moduleTypeComboBox) {
        // 根据项目获取依赖库
        Library[] libraries = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraries();
        for (Library library : libraries) {
            String name = library.getName();
            assert name != null;
            if (name.contains("spring-boot")) {
                moduleTypeComboBox.addItem(BOOTINF);
                break;
            }
        }
        if (moduleTypeComboBox.getItemCount() == 1) {
            moduleTypeComboBox.addItem(WEBINF);
        }
        if (moduleTypeComboBox.getItemCount() == 1) {
            moduleTypeComboBox.addItem(BOOTINF);
        }

    }

    /**
     * 设置保存路径默认值
     *
     * @param textFieldWithBrowseButton 保存路径的文本框
     */
    public static void setDavePathDefault(TextFieldWithBrowseButton textFieldWithBrowseButton) {
        // 获取桌面路径
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        // 设置文本框的默认值
        textFieldWithBrowseButton.setText(desktopPath);
    }

    /**
     * 设置补丁保存路径监听
     *
     * @param project                   项目
     * @param textFieldWithBrowseButton 保存路径的文本框
     */
    public static void setBrowseFolderListener(Project project, TextFieldWithBrowseButton textFieldWithBrowseButton) {
        // chooseFiles       控制是否可以选择文件
        // chooseFolders     控制是否可以选择文件夹
        // chooseJars        控制是否可以选择.jar文件
        // chooseJarsAsFiles 控制.jar文件是作为文件还是文件夹返回
        // chooseJarContents 控制是否可以选择.jar文件内容
        // chooseMultiple    控制是否可以选择多个文件
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        // TextFieldWithBrowseButton 添加文件夹选择监听
        textFieldWithBrowseButton.addBrowseFolderListener(PatcherBundle.message("patcher.value.2"), "", project, fileChooserDescriptor);
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
     * 打开指定节点
     *
     * @param tree          树
     * @param startingIndex 起始索引
     * @param rowCount      行数
     */
    public static void expandAllNodes(Tree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}
