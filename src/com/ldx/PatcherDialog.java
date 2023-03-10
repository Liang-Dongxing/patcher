package com.ldx;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
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
import com.jgoodies.common.base.Strings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Liang
 */
public class PatcherDialog extends JDialog {

    private JPanel contentPane;

    private JTextField projectName;
    private TextFieldWithBrowseButton savePath;
    private TextFieldWithStoredHistory webPath;
    private JBList<String> fileList;

    private JButton buttonOk;
    private JButton buttonCancel;
    private JLabel pathcerTitle;
    private JCheckBox oldPatcher;
    private JComboBox patcherType;

    private Module[] modules;
    private Project project;
    private List<VirtualFile> patcherFiles = new LinkedList<>();

    private PropertiesComponent propertiesComponent;

    private final NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(PatcherEnum.PATCHER_NOTIFICATION_TITLE);


    /**
     * ????????? Pane ??????
     *
     * @param event
     */
    private void initPane(AnActionEvent event) {

        setTitle("Create Patcher Dialog");
        setContentPane(contentPane);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getRootPane().setDefaultButton(buttonOk);

        // ??????????????????
        projectName.setText(project.getName());

        // ??????????????????
        savePath.setText(propertiesComponent.getValue(PatcherEnum.PATCHER_SAVE_PATH));
        //??????WEB??????
        webPath.setTextAndAddToHistory(propertiesComponent.getValue(PatcherEnum.PATCHER_SAVE_WEB_PATH));
        //???????????????????????????????????????
        oldPatcher.setSelected(Boolean.parseBoolean(propertiesComponent.getValue(PatcherEnum.DELETE_OLD_PATCHER)));

        // ????????????????????????????????????
        String[] fileArray = new String[patcherFiles.size()];
        patcherFiles.stream().map(x -> project.getName() + x.getPath().replaceFirst(Objects.requireNonNull(project.getBasePath()), "")).collect(Collectors.toList()).toArray(fileArray);
        fileList.setListData(fileArray);
        fileList.setEmptyText("No file selected!");
    }

    private void initDialog(AnActionEvent event) {
        super.setSize(600, 400);
        setLocation(event);
        super.setUndecorated(true);
        super.setVisible(true);
        super.requestFocus();
    }


    PatcherDialog(AnActionEvent event) {
        // ????????????Project??????
        project = event.getProject();
        // ???????????????????????????
        VirtualFile[] patcherDirectoryAndFiles = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        assert patcherDirectoryAndFiles != null;
        for (VirtualFile patcherDirectoryAndFile : patcherDirectoryAndFiles) {
            if (patcherDirectoryAndFile.isDirectory()) {
                patcherFiles.addAll(Arrays.asList(patcherDirectoryAndFile.getChildren()));
            } else {
                patcherFiles.add(patcherDirectoryAndFile);
            }
        }

        // ???????????????module
        Set<Module> collect = patcherFiles.stream().map(virtualFile -> ModuleUtil.findModuleForFile(virtualFile, project)).collect(Collectors.toSet());
        modules = collect.toArray(Module.EMPTY_ARRAY);

        if (modules.length > 0 || Objects.isNull(project)) {

            initPane(event);

            projectName.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    String[] fileArray = new String[patcherFiles.size()];
                    patcherFiles.stream().map(x -> projectName.getText() + x.getPath().replaceFirst(Objects.requireNonNull(project.getBasePath()), "")).collect(Collectors.toList()).toArray(fileArray);
                    fileList.setListData(fileArray);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    String[] fileArray = new String[patcherFiles.size()];
                    patcherFiles.stream().map(x -> projectName.getText() + x.getPath().replaceFirst(Objects.requireNonNull(project.getBasePath()), "")).collect(Collectors.toList()).toArray(fileArray);
                    fileList.setListData(fileArray);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    String[] fileArray = new String[patcherFiles.size()];
                    patcherFiles.stream().map(x -> projectName.getText() + x.getPath().replaceFirst(Objects.requireNonNull(project.getBasePath()), "")).collect(Collectors.toList()).toArray(fileArray);
                    fileList.setListData(fileArray);
                }
            });

            // ????????????????????????
            FileChooserDescriptor singleFileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
            savePath.addBrowseFolderListener("Select Patcher Save Path", null, project, singleFileDescriptor);

            buttonOk.addActionListener(e -> {
                onOK();
                dispose();
            });

            buttonCancel.addActionListener(e -> dispose());

            pathcerTitle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/Liang-Dongxing/patcher"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            initDialog(event);
        } else {
            // ????????????
            Notifications.Bus.notify(notificationGroup.createNotification("Please select a patch file.", NotificationType.ERROR));
        }

    }

    /**
     * ???????????????????????????????????????
     *
     * @param event
     */
    private void setLocation(AnActionEvent event) {
        // ????????????????????????
        Component component = event.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        //??????????????????????????????
        while (component != null && !(component instanceof Window)) {
            component = component.getParent();
        }
        super.setLocationRelativeTo(component);
    }

    private void onOK() {
        // ?????????????????????
        webPath.setTextAndAddToHistory(webPath.getText());
        // ????????????????????????
        propertiesComponent.setValue(PatcherEnum.PATCHER_SAVE_WEB_PATH, webPath.getText());
        propertiesComponent.setValue(PatcherEnum.PATCHER_SAVE_PATH, savePath.getText());

        // ????????????
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        compilerManager.make(project, modules, (aborted, errors, warnings, compileContext) -> {
            if (aborted) {
                Notifications.Bus.notify(notificationGroup.createNotification("Code compilation has been aborted.", NotificationType.ERROR));
                return;
            }
            if (errors != 0) {
                Notifications.Bus.notify(notificationGroup.createNotification("Errors occurred while compiling code!", NotificationType.ERROR));
                return;
            }

            try {
                execute(compileContext, modules);
                String content = "Export patch successfully(" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ").<br><a href=\"file://" + savePath.getText() + "\" target=\"blank\">open</a>";
                Notifications.Bus.notify(notificationGroup.createNotification(PatcherEnum.PATCHER_NOTIFICATION_TITLE, content, NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER));
            } catch (IOException e) {
                Notifications.Bus.notify(notificationGroup.createNotification("Export patch failed.<br>" + e.getMessage(), NotificationType.ERROR));
                e.printStackTrace();
            }
        });
    }

    private void execute(CompileContext compileContext, Module[] modules) throws IOException {
        for (Module module : modules) {
            // ?????????????????????
            if (oldPatcher.isSelected()) {
                propertiesComponent.setValue(PatcherEnum.DELETE_OLD_PATCHER, oldPatcher.isSelected());
                Path saveModulePath = Paths.get(savePath.getText(), projectName.getText());
                if (Files.isDirectory(saveModulePath)) {
                    Files.walk(saveModulePath).sorted(Comparator.reverseOrder()).forEach(x -> {
                        try {
                            Files.deleteIfExists(x);
                        } catch (IOException e) {
                            try {
                                Files.deleteIfExists(x);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                propertiesComponent.setValue(PatcherEnum.DELETE_OLD_PATCHER, oldPatcher.isSelected());
            }
            // ??????????????????
            VirtualFile compilerOutputPath = compileContext.getModuleOutputDirectory(module);
            // ????????????
            VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(false);

            // ??????????????????????????????????????????
            String tempPatcherType = String.valueOf(patcherType.getSelectedItem()).toLowerCase();
            switch (tempPatcherType) {
                case "class":
                    // ???????????????????????????????????????
                    Path projectNamePath = null;
                    if (project.getName().equals(module.getName())) {
                        projectNamePath = Paths.get(savePath.getText(), this.projectName.getText());
                    } else {
                        projectNamePath = Paths.get(savePath.getText(), this.projectName.getText(), module.getName());
                    }
                    for (VirtualFile patcherFile : patcherFiles) {
                        Module moduleForFile = ModuleUtil.findModuleForFile(patcherFile, project);
                        if (!module.equals(moduleForFile)) {
                            continue;
                        }
                        Optional<VirtualFile> first = Stream.of(sourceRoots).filter(virtualFile -> patcherFile.getPath().contains(virtualFile.getPath())).findFirst();
                        if (first.isPresent()) {
                            // ??????????????????
                            Path packagePath = Paths.get(first.get().getPath()).relativize(Paths.get(patcherFile.getParent().getPath()));
                            //???????????????????????????
                            String classFileNameSuffix = patcherFile.getName();
                            String classFileName = patcherFile.getNameWithoutExtension();
                            String classSuffix = patcherFile.getExtension();

                            //???????????????
                            Path classFilesPath = Paths.get(compilerOutputPath.getPath(), packagePath.toString());
                            //?????????????????????
                            Path saveClassPath = Paths.get(projectNamePath.toString(), "WEB-INF", "classes", packagePath.toString());
                            if (Files.notExists(saveClassPath)) {
                                Files.createDirectories(saveClassPath);
                            }
                            if ("java".equals(classSuffix)) {
                                DirectoryStream<Path> classPaths = Files.newDirectoryStream(classFilesPath, classFileName + ".class");
                                for (Path next : classPaths) {
                                    Files.copy(next, Paths.get(saveClassPath.toString(), next.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                                }
                                DirectoryStream<Path> classPathsProxy = Files.newDirectoryStream(classFilesPath, classFileName + "$*.class");
                                for (Path next : classPathsProxy) {
                                    Files.copy(next, Paths.get(saveClassPath.toString(), next.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                                }
                            } else {
                                Files.copy(Paths.get(classFilesPath.toString(), classFileNameSuffix), Paths.get(saveClassPath.toString(), classFileNameSuffix), StandardCopyOption.REPLACE_EXISTING);
                            }
                        } else if (patcherFile.getPath().contains(webPath.getText())) {
                            // ??????web??????????????????
                            int webIndex = patcherFile.getPath().lastIndexOf(webPath.getText());
                            String substring = patcherFile.getPath().substring(webIndex).replace(webPath.getText(), "");
                            Path saveStaticPath = Paths.get(projectNamePath.toString(), substring);
                            if (Files.notExists(saveStaticPath)) {
                                Files.createDirectories(saveStaticPath);
                            }
                            Files.copy(Paths.get(patcherFile.getPath()), saveStaticPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    break;
                case "java":
                    for (VirtualFile patcherFile : patcherFiles) {
                        Path saveStaticPath = Paths.get(savePath.getText(), this.projectName.getText(), patcherFile.getPath().replaceFirst(Objects.requireNonNull(module.getProject().getBasePath()), ""));
                        if (Files.notExists(saveStaticPath)) {
                            Files.createDirectories(saveStaticPath);
                        }
                        Files.copy(Paths.get(patcherFile.getPath()), saveStaticPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void createUIComponents() {
        propertiesComponent = PropertiesComponent.getInstance();
        webPath = new TextFieldWithStoredHistory(PatcherEnum.WEB_PATH[1]);
        webPath.setMaximumRowCount(10);
        if (webPath.getHistory().size() == 0) {
            webPath.setTextAndAddToHistory(PatcherEnum.WEB_PATH[0]);
            webPath.setTextAndAddToHistory(PatcherEnum.WEB_PATH[1]);
            propertiesComponent.setValue(PatcherEnum.PATCHER_SAVE_WEB_PATH, PatcherEnum.WEB_PATH[1]);
            propertiesComponent.setValue(PatcherEnum.PATCHER_SAVE_PATH, PatcherEnum.DESKTOP_PATH);
        }
        if (Strings.isNotEmpty(propertiesComponent.getValue(PatcherEnum.PATCHER_SAVE_WEB_PATH))) {
            propertiesComponent.setValue(PatcherEnum.PATCHER_SAVE_WEB_PATH, PatcherEnum.WEB_PATH[1]);
        }
        if (Strings.isNotEmpty(propertiesComponent.getValue(PatcherEnum.PATCHER_SAVE_PATH))) {
            propertiesComponent.setValue(PatcherEnum.PATCHER_SAVE_PATH, PatcherEnum.DESKTOP_PATH);
        }
    }
}
