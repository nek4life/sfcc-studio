package com.binarysushi.studio.browseActions;

import com.binarysushi.studio.configuration.StudioConfigurationProvider;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class StudioBrowseSandboxBMAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(project);
        String hostname = configurationProvider.getHostname();

        if (hostname != null && !hostname.isEmpty()) {
            BrowserUtil.browse("https://" + hostname + "/on/demandware.store/Sites-Site/default/ViewApplication-DisplayWelcomePage");
        } else {
            Notifications.Bus.notify(new Notification(
                    "Salesforce",
                    "Unknown Host",
                    "Please check your server configuration in the project settings panel. (File | Settings | Tools | Commerce Cloud Server)",
                    NotificationType.ERROR));
        }
    }
}
