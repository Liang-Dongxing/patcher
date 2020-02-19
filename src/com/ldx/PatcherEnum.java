package com.ldx;

import java.nio.file.Paths;

/**
 * @author Liang
 */

class PatcherEnum {

    /**
     * 消息窗口标题
     */
    static final String PATCHER_NOTIFICATION_TITLE = "Patcher";
    /**
     * 补丁保存全局的Key
     */
    static final String PATCHER_SAVE_PATH = "PatcherSavePath";
    /**
     * webPath保存全局的Key
     */
    static final String PATCHER_SAVE_WEB_PATH = "PatcherSaveWebPath";

    /**
     * webPath
     */
    static final String[] WEB_PATH = {"WebRoot", "webapp","WebContent"};

    /**
     * 桌面路径
     */
    static final String DESKTOP_PATH = Paths.get(System.getProperty("user.home"), "Desktop").toString();

    static final String DELETE_OLD_PATCHER = "DeleteOldPatcher";

}
