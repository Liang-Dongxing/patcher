package com.ldx;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithStoredHistory;
import com.intellij.ui.components.JBList;
import org.apache.commons.lang.StringUtils;
import sun.awt.SunToolkit;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liang
 */
public class PatcherDialog extends JDialog {

    private JPanel contentPane;

    private JTextField moduleName;
    private TextFieldWithBrowseButton savePath;
    private TextFieldWithStoredHistory webPath;
    private JBList<String> fileList;

    private JButton buttonOK;
    private JButton buttonCancel;

    private Module[] modules;
    private Project project;
    private VirtualFile[] patcherFiles;


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

        // 设置模块名称
        Set<String> collect = Arrays.stream(modules).map(Module::getName).collect(Collectors.toSet());
        moduleName.setText(StringUtils.join(collect, ";"));

        // 设置保存路径
        savePath.setText(Paths.get(System.getProperty("user.home"), "Desktop").toString());

        //设置WEB路径
        webPath.setTextAndAddToHistory(PropertiesComponent.getInstance().getValue("saveWebPath"));

        // 获取需要打补丁的文件列表
        String[] fileArray = new String[patcherFiles.length];
        Arrays.stream(patcherFiles).map(x -> x.getPath().substring(x.getPath().indexOf(project.getName()))).collect(Collectors.toList()).toArray(fileArray);
        fileList.setListData(fileArray);
        fileList.setEmptyText("No File Selected!");
    }

    private void initDialog(AnActionEvent event) {
        super.setSize(600, 400);
        setLocation(event);
        super.setVisible(true);
        super.requestFocus();
    }


    PatcherDialog(AnActionEvent event) {
        // 获取当前Project对象
        project = event.getProject();
        // 获取全部补丁源文件
        patcherFiles = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        // 获取文件的module
        assert patcherFiles != null;
        Set<Module> collect = Arrays.stream(patcherFiles).map(virtualFile -> ModuleUtil.findModuleForFile(virtualFile, project)).collect(Collectors.toSet());
        modules = collect.toArray(Module.EMPTY_ARRAY);

        if (modules.length > 0 || Objects.isNull(project)) {

            initPane(event);

            // 设置文件夹浏览器
            FileChooserDescriptor singleFileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
            savePath.addBrowseFolderListener("Select Patcher Save Path", null, project, singleFileDescriptor);

            buttonOK.addActionListener(e -> {
                onOK();
                dispose();
            });

            buttonCancel.addActionListener(e -> dispose());

            initDialog(event);
        } else {
            // 设置通知
            Notification notification = new Notification("Patcher", "Patcher", "Please select a patch file", NotificationType.ERROR);
            Notifications.Bus.notify(notification);
        }

    }

    /**
     * 不同屏幕获取对话框居中位置
     *
     * @param event
     */
    private void setLocation(AnActionEvent event) {
        // 获取当前程序组件
        Component component = event.getData(DataKeys.CONTEXT_COMPONENT);
        //获取当前组件窗口信息
        Window componentWindow = SunToolkit.getContainingWindow(component);
        super.setLocationRelativeTo(componentWindow);
    }

    private void onOK() {
        // 保存输入的路径
        webPath.setTextAndAddToHistory(webPath.getText());
        // 设置全局保存数据
        PropertiesComponent.getInstance().setValue("saveWebPath", webPath.getText());

        // 编译项目
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        compilerManager.make(project, modules, (aborted, errors, warnings, compileContext) -> {
            Notification notification = new Notification("Patcher", "Patcher", "Please select a patch file", NotificationType.ERROR);
            if (aborted) {
                notification.setContent("Code compilation has been aborted.");
                Notifications.Bus.notify(notification);
                return;
            }
            if (errors != 0) {
                notification.setContent("Errors occurred while compiling code!");
                Notifications.Bus.notify(notification);
                return;
            }
            try {
                execute(compileContext, modules);
                Notifications.Bus.notify(new Notification("Patcher", "Patcher", "Export patch successfully", NotificationType.INFORMATION));
            } catch (IOException e) {
                notification.setContent("Export patch failed");
                Notifications.Bus.notify(notification);
                e.printStackTrace();
            }
        });
    }

    private void execute(CompileContext compileContext, Module[] modules) throws IOException {
        for (int i = 0; i < modules.length; i++) {
            // 编译输出目录
            VirtualFile compilerOutputPath = compileContext.getModuleOutputDirectory(modules[i]);
            // 源码目录
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(modules[i]);
            VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();

            for (int k = 0; k < patcherFiles.length; k++) {
                VirtualFile patcherFile = patcherFiles[k];
                Module moduleForFile = ModuleUtil.findModuleForFile(patcherFile, project);
                if (!modules[i].equals(moduleForFile)) {
                    continue;
                }
                //处理类路径下的文件
                for (int j = 0; j < sourceRoots.length; j++) {
                    VirtualFile sourceRoot = sourceRoots[j];
                    if (patcherFile.getPath().contains(sourceRoot.getPath())) {
                        //编辑后的包路径
                        Path packagePath = Paths.get(sourceRoot.getPath()).relativize(Paths.get(patcherFile.getParent().getPath()));
                        //文件名字和文件格式
                        String classFileNameSuffix = patcherFile.getName();
                        String classFileName = patcherFile.getNameWithoutExtension();
                        String classSuffix = patcherFile.getExtension();

                        //编译后路径
                        Path classFilesPath = Paths.get(compilerOutputPath.getPath(), packagePath.toString());
                        //需要保存的路径
                        Path saveClassPath = Paths.get(savePath.getText(), modules[i].getName(), "WEB-INF", "classes", packagePath.toString());
                        if (Files.notExists(saveClassPath)) {
                            Files.createDirectories(saveClassPath);
                        }
                        if ("java".equals(classSuffix)) {
                            DirectoryStream<Path> classPaths = Files.newDirectoryStream(classFilesPath, classFileName + "*.class");
                            Iterator<Path> iterator = classPaths.iterator();
                            while (iterator.hasNext()) {
                                Path next = iterator.next();
                                Files.copy(next, Paths.get(saveClassPath.toString(), next.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                            }
                        } else {
                            Files.copy(Paths.get(classFilesPath.toString(), classFileNameSuffix), Paths.get(saveClassPath.toString(), classFileNameSuffix), StandardCopyOption.REPLACE_EXISTING);
                        }
                        break;
                    }
                }
                // 处理非类路径下文件
                if (patcherFile.getPath().contains(webPath.getText())) {
                    int webIndex = patcherFile.getPath().lastIndexOf(webPath.getText());
                    String substring = patcherFile.getPath().substring(webIndex).replace(webPath.getText(), "");
                    Path saveStaticPath = Paths.get(savePath.getText(), modules[i].getName(), substring);
                    if (Files.notExists(saveStaticPath)) {
                        Files.createDirectories(saveStaticPath);
                    }
                    Files.copy(Paths.get(patcherFile.getPath()), saveStaticPath, StandardCopyOption.REPLACE_EXISTING);
                }

            }
        }
    }

    private void createUIComponents() {
        webPath = new TextFieldWithStoredHistory("webapp");
        webPath.setMaximumRowCount(10);
        if (webPath.getHistory().size() == 0) {
            webPath.setTextAndAddToHistory("WebRoot");
            webPath.setTextAndAddToHistory("webapp");
        }
    }
}
