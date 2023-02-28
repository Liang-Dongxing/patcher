package com.bit.patcher;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Liang
 * @version 1.0
 * @date 2023/2/23 21:55
 * Created by IntelliJ IDEA
 */
public class PatcherUtils {

    private static final String BOOTINF = "(SpringBoot) BOOT-INF";
    private static final String WEBINF = "(Web) WEB-INF";
    private static PatcherToolWindow patcherToolWindow;


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
    public static void setLibraryComboBox(Project project, ComboBox<PatcherModuleType> moduleTypeComboBox) {
        moduleTypeComboBox.addItem(PatcherModuleType.builder().name("Spring Framework").type("BOOT-INF").build());
        moduleTypeComboBox.addItem(PatcherModuleType.builder().name("Java EE").type("WEB-INF").build());
        moduleTypeComboBox.addItem(PatcherModuleType.builder().name("Java SE").type("").build());
        // 根据项目获取依赖库
        Library[] libraries = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraries();
        for (Library library : libraries) {
            if (Objects.requireNonNull(library.getName()).contains("spring-boot")) {
                moduleTypeComboBox.setSelectedIndex(0);
                return;
            }
        }
        for (Library library : libraries) {
            if (Objects.requireNonNull(library.getName()).contains("spring")) {
                moduleTypeComboBox.setSelectedIndex(1);
                return;
            }
        }
        moduleTypeComboBox.setSelectedIndex(2);
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

    public static void getExportFile(Project project, VirtualFile virtualFile) {
        if (virtualFile.isDirectory() && virtualFile.getChildren().length > 0) {
            for (VirtualFile child : virtualFile.getChildren()) {
                getExportFile(project, child);
            }
        } else {
            // 获取当前选中的文件所属的 Module
            Module moduleForFile = ModuleUtil.findModuleForFile(virtualFile, project);
            // 将当前选中的文件按照 Module 分组
            assert moduleForFile != null;
            PatcherVirtualFile patcherVirtualFile = PatcherVirtualFile.builder().virtualFile(virtualFile).path(virtualFile.getPath()).name(virtualFile.getName()).moduleName(moduleForFile.getName()).module(moduleForFile).build();
            PVFUtils.setVirtualFilesMapValue(patcherVirtualFile);
        }
    }

    /**
     * 导出补丁文件
     *
     * @param project                           项目
     * @param savePathTextFieldWithBrowseButton 保存路径的文本框
     * @param moduleTypeComboBox                项目类型的下拉框
     * @param deleteOldPatcherFilesJbCheckBox   删除旧的补丁文件的复选框
     * @param exportButton                      导出按钮
     */
    public static void exportFile(Project project, TextFieldWithBrowseButton savePathTextFieldWithBrowseButton, ComboBox<String> moduleNameComboBox, ComboBox<PatcherModuleType> moduleTypeComboBox, JBCheckBox exportTheSourceCodeJbCheckBox, JBCheckBox deleteOldPatcherFilesJbCheckBox, JButton exportButton) {
        exportButton.addActionListener(e -> {
            Map<String, List<PatcherVirtualFile>> virtualFilesMap = PVFUtils.getVirtualFilesMap();
            if (virtualFilesMap.size() > 0) {
                exportDeleteFile(project, deleteOldPatcherFilesJbCheckBox, savePathTextFieldWithBrowseButton);
                exportSourcesFile(project, savePathTextFieldWithBrowseButton, exportTheSourceCodeJbCheckBox);
                // 编译项目
                CompilerManager.getInstance(project).make((aborted, errors, warnings, compileContext) -> {
                    if (errors == 0) {
                        exportClassFile(project, savePathTextFieldWithBrowseButton, moduleNameComboBox, moduleTypeComboBox);
                    }
                });
            }
        });
    }

    /**
     * 删除旧的补丁文件
     *
     * @param project                           项目
     * @param deleteOldPatcherFilesJbCheckBox   删除旧的补丁文件的复选框
     * @param savePathTextFieldWithBrowseButton 保存路径的文本框
     */
    private static void exportDeleteFile(Project project, JBCheckBox deleteOldPatcherFilesJbCheckBox, TextFieldWithBrowseButton savePathTextFieldWithBrowseButton) {
        if (deleteOldPatcherFilesJbCheckBox.isSelected()) {
            // 删除旧的补丁文件
            Path path = Paths.get(savePathTextFieldWithBrowseButton.getText());
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted((p1, p2) -> -p1.compareTo(p2)).forEach(p -> {
                    if (p.toString().contains(project.getName())) {
                        try {
                            Files.delete(p);
                        } catch (IOException ex1) {
                            throw new RuntimeException(ex1);
                        }
                    }
                });
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    /**
     * 导出源码文件
     *
     * @param project                           项目
     * @param savePathTextFieldWithBrowseButton 保存路径的文本框
     * @param exportTheSourceCodeJbCheckBox     导出源码的复选框
     */
    private static void exportSourcesFile(Project project, TextFieldWithBrowseButton savePathTextFieldWithBrowseButton, JBCheckBox exportTheSourceCodeJbCheckBox) {
        Map<String, List<PatcherVirtualFile>> virtualFilesMap = PVFUtils.getVirtualFilesMap();
        if (exportTheSourceCodeJbCheckBox.isSelected()) {
            virtualFilesMap.forEach((key, value) -> {
                value.forEach(virtualFile -> {
                    // 如果模块名字和项目名字相同，则不需要保存模块名字
                    String saveModulePath = project.getName().equals(virtualFile.getModule().getName()) ? "" : virtualFile.getModule().getName();
                    // 获取保存路径/项目名/模块名/项目类型
                    Path pm = Paths.get(savePathTextFieldWithBrowseButton.getText(), project.getName() + "-sources", saveModulePath);
                    // 获取源码路径
                    VirtualFile[] sourceRoots = ModuleRootManager.getInstance(virtualFile.getModule()).getSourceRoots();
                    for (VirtualFile sourceRoot : sourceRoots) {
                        if (virtualFile.getVirtualFile().getPath().contains(sourceRoot.getPath())) {
                            Path savePath = Paths.get(virtualFile.getVirtualFile().getPath().replace(sourceRoot.getPath(), pm.toString()));
                            try {
                                Files.createDirectories(savePath.getParent());
                                Files.copy(virtualFile.getVirtualFile().getInputStream(), savePath);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
            });
        }
    }

    /**
     * 导出 class 文件
     *
     * @param project                           项目
     * @param savePathTextFieldWithBrowseButton 保存路径的文本框
     * @param moduleNameComboBox                模块名字的下拉框
     * @param moduleTypeComboBox                模块类型的下拉框
     */
    private static void exportClassFile(Project project, TextFieldWithBrowseButton savePathTextFieldWithBrowseButton, ComboBox<String> moduleNameComboBox, ComboBox<PatcherModuleType> moduleTypeComboBox) {
        Map<String, List<PatcherVirtualFile>> virtualFilesMap = PVFUtils.getVirtualFilesMap();
        virtualFilesMap.forEach((key, value) -> {
            value.forEach(virtualFile -> {
                // 如果模块名字和项目名字相同，则不需要保存模块名字
                StringBuilder modelName = new StringBuilder();
                switch (moduleTypeComboBox.getItem().getType()) {
                    case "BOOT-INF", "WEB-INF" -> {
                        if (moduleNameComboBox.getItem().equals(PatcherBundle.message("patcher.value.1"))) {
                            modelName.append(virtualFile.getModule().getName());
                        } else if (virtualFile.getModule().getModuleNioFile().getParent().equals(Paths.get(project.getBasePath()))) {
                        } else if (project.getName().equals(moduleNameComboBox.getItem())) {
                        } else {
                            modelName.append(moduleNameComboBox.getItem());
                        }
                    }
                    default ->
                }
                // 获取保存路径/项目名/模块名/项目类型
                Path pm = Paths.get(savePathTextFieldWithBrowseButton.getText(), project.getName(), modelName.toString(), moduleTypeComboBox.getItem().getType());
                // 获取编译后的路径
                VirtualFile compilerOutputPath = CompilerModuleExtension.getInstance(virtualFile.getModule()).getCompilerOutputPath();
                // 获取源码路径
                VirtualFile[] sourceRoots = ModuleRootManager.getInstance(virtualFile.getModule()).getSourceRoots();
                for (VirtualFile sourceRoot : sourceRoots) {
                    // 判断文件是否在源码路径下
                    if (virtualFile.getVirtualFile().getPath().contains(sourceRoot.getPath())) {
                        // 获取源码路径下的文件路径
                        String searchPath = virtualFile.getVirtualFile().getParent().getPath().replace(sourceRoot.getPath(), compilerOutputPath.getPath());
                        // 获取源码路径方便查找内部类构建文件的路径
                        String path = virtualFile.getVirtualFile().getPath().replace(sourceRoot.getPath(), compilerOutputPath.getPath()).replace(".java", ".*");
                        // 如果项目类型是空，则保存路径为编译后的路径
                        String classpath = moduleTypeComboBox.getItem().getType().equals("") ? compilerOutputPath.getPath() : compilerOutputPath.getParent().getPath();
                        String savePath = searchPath.replace(classpath, pm.toString());
                        try {
                            Files.createDirectories(Path.of(savePath));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        // 查找文件正则表达式
                        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + path);
                        try (Stream<Path> stream = Files.walk(Paths.get(searchPath))) {
                            // 查找文件并复制到保存路径
                            stream.filter(Files::isRegularFile).filter(matcher::matches).forEach(currentFile -> {
                                try {
                                    Path file = Paths.get(savePath, currentFile.getFileName().toString());
                                    Files.copy(currentFile, file, StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                // 获取Web资源路径
                if (StringUtil.toLowerCase(virtualFile.getPath()).contains("webapp")) {
                    Path savePath = Path.of(pm.getParent().toString(), virtualFile.getPath().replaceAll(".*webapp", ""));
                    try {
                        Files.createDirectories(savePath.getParent());
                        Files.copy(virtualFile.getVirtualFile().getInputStream(), savePath);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        });
    }
}
