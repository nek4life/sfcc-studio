package com.binarysushi.studio.webdav;

import com.binarysushi.studio.settings.StudioSettingsProvider;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StudioBulkFileListener implements BulkFileListener, Disposable {
    private MessageBusConnection connection;
    private static Logger LOG = Logger.getInstance(StudioBulkFileListener.class);

    public StudioBulkFileListener() {
        connection = ApplicationManager.getApplication().getMessageBus().connect();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public void dispose() {
        connection.disconnect();
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
                    StudioSettingsProvider settingsProvider = StudioSettingsProvider.getInstance(project);

                    // Bail out if auto uploads are not enabled.
                    if (!StudioSettingsProvider.getInstance(project).getAutoUploadEnabled()) {
                        return;
                    }

                    if (settingsProvider.getCartridgeRoots().size() < 1) {
                        return;
                    }

                    for (String cartridgeRoot : settingsProvider.getCartridgeRoots()) {
                        if (eventFile.getPath().contains(cartridgeRoot)) {
                            ProgressManager.getInstance().run(
                                    new StudioUpdateFileTask(
                                            project,
                                            "Syncing files to: " + StudioSettingsProvider.getInstance(project).getHostname(),
                                            true,
                                            PerformInBackgroundOption.ALWAYS_BACKGROUND,
                                            cartridgeRoot,
                                            eventFile.getPath()
                                    )
                            );
                        }
                    }
                }
            }
        }
    }
}
