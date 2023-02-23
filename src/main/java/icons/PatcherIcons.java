package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author Liang
 * @version 1.0
 * @date 2020/12/1 16:00
 * Created by IntelliJ IDEA
 */
public class PatcherIcons {
    public static final Icon PATCHER = getIcon("/icons/patcher.svg");

    /**
     * 获取图标
     *
     * @param path 静态文件地址
     * @return 图标
     */
    private static Icon getIcon(String path) {
        return IconLoader.getIcon(path, PatcherIcons.class);
    }
}
