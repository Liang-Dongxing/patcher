package com.bit.patcher;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PatcherNotificationUtils {

    /**
     * 成功通知
     *
     * @param openPath 打开的路径
     */
    public static void successNotification(String openPath) {
        Notification notification = new Notification(PatcherBundle.message("patcher.name"),
                PatcherBundle.message("patcher.name"),
                PatcherBundle.message("patcher.notification.content"),
                NotificationType.INFORMATION);
        NotificationAction action = new NotificationAction(PatcherBundle.message("patcher.notification.open")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop.getDesktop().open(new File(openPath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        notification.addAction(action);
        Notifications.Bus.notify(notification);
    }
}
