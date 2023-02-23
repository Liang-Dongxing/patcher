package com.bit.patcher;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AddEditRemovePanel;
import com.intellij.ui.components.JBCheckBox;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

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

    private final List<String> saveFilesList = new LinkedList<>();
    private JPanel patcherWindowContent;
    private JPanel moduleTypePanel;
    private ComboBox<String> moduleTypeComboBox;
    private JPanel moduleNamePanel;
    private ComboBox<String> moduleNameComboBox;
    private JPanel savePathPanel;
    private TextFieldWithBrowseButton savePathTextFieldWithBrowseButton;
    private JPanel saveFilesPanel;
    private AddEditRemovePanel<String> filesAddEditRemovePanel;
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
    }

    /**
     * 创建自定义的用户界面组件
     */
    public void createUIComponents() {
        filesAddEditRemovePanel = new AddEditRemovePanel<String>(new AddEditRemovePanel.TableModel<String>() {
            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public @Nullable @NlsContexts.ColumnName String getColumnName(int columnIndex) {
                return null;
            }

            @Override
            public Object getField(String o, int columnIndex) {
                return null;
            }
        }, saveFilesList) {
            @Override
            protected @Nullable String addItem() {
                return null;
            }

            @Override
            protected boolean removeItem(String o) {
                return false;
            }

            @Override
            protected @Nullable String editItem(String o) {
                return null;
            }

        };
    }

    public JComponent getContent() {
        return patcherWindowContent;
    }
}

