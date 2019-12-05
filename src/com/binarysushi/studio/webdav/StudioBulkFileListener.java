package com.binarysushi.studio.webdav;

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StudioBulkFileListener implements BulkFileListener {
    private MessageBusConnection connection;
    private static Logger LOG = Logger.getInstance(StudioBulkFileListener.class);

    public StudioBulkFileListener() {
        connection = ApplicationManager.getApplication().getMessageBus().connect();
    }

    @NotNull
    public String getComponentName() {
        return "StudioBulkFileListener";
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> events) {
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();

        for (VFileEvent event : events) {
            VirtualFile eventFile = event.getFile();

            if (eventFile != null && !eventFile.isDirectory()) {
                for (Project project : projects) {
                    StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(project);

                    // Bail out if auto uploads are not enabled.
                    if (!StudioConfigurationProvider.getInstance(project).getAutoUploadEnabled()) {
                        return;
                    }

                    if (configurationProvider.getCartridgeRoots().size() < 1) {
                        return;
                    }

                    for (String cartridgeRoot : configurationProvider.getCartridgeRoots()) {
                        if (eventFile.getPath().contains(cartridgeRoot)) {
                            ProgressManager.getInstance().run(
                                    new StudioUpdateFileTask(
                                            project,
                                            "Syncing files to: " + StudioConfigurationProvider.getInstance(project).getHostname(),
                                            true,
                                            PerformInBackgroundOption.ALWAYS_BACKGROUND,
                                            cartridgeRoot,
                                            eventFile
                                    )
                            );
                        }
                    }
                }
            }
        }
    }
}
