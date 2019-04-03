package com.binarysushi.studio.webdav;

import com.binarysushi.studio.configuration.StudioServerConfigurable;
import com.binarysushi.studio.toolWindow.StudioConsoleService;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StudioUpdateFileTask extends Task.Backgroundable {
    private final Logger LOG = Logger.getInstance(StudioUpdateFileTask.class);
    private final CloseableHttpClient myHttpClient;
    private final HttpClientContext myHttpContext;
    private final ArrayList<String> myRemoteDirPaths;
    private final String myRemoteFilePath;
    private final String myLocalFilePath;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    private final ConsoleView myConsoleView;
    private final VirtualFile myEventFile;

    StudioUpdateFileTask(Project project,
                         final String title,
                         final boolean canBeCancelled,
                         final PerformInBackgroundOption backgroundOption,
                         String sourceRootPath,
                         VirtualFile eventFile) {
        super(project, title, canBeCancelled, backgroundOption);
        StudioServerConnection serverConnection = ServiceManager.getService(project, StudioServerConnection.class);
        myConsoleView = ServiceManager.getService(myProject, StudioConsoleService.class).getConsoleView();
        myEventFile = eventFile;
        myLocalFilePath = eventFile.getPath();
        myHttpClient = serverConnection.getClient();
        myHttpContext = serverConnection.getContext();
        myRemoteDirPaths = serverConnection.getRemoteDirPaths(sourceRootPath, myLocalFilePath);
        myRemoteFilePath = serverConnection.getRemoteFilePath(sourceRootPath, myLocalFilePath);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        File localFile = new File(myLocalFilePath);
        FileStatus fileStatus;
        indicator.setFraction(.33);

        int statusCode = testRemoteFileExistence();

        // If there is an error don't do anything on the server
        if (statusCode == 401 || statusCode == -1) {
            return;
        }

        // If the status code is 200 a file exists
        if (statusCode == 200) {
            // Check the existence of the local virtual file to determine whether to update or delete.
            if (myEventFile.exists()) {
                fileStatus = FileStatus.UPDATED;
            } else {
                fileStatus = FileStatus.DELETED;
            }
        } else {
            // If the file doesn't exist on the server a new file should be created
            fileStatus = FileStatus.NEW;
        }

        indicator.setFraction(.5);

        switch (fileStatus) {
            case NEW:
                createRemoteDirectories();
                doHttpRequest(RequestBuilder.create("PUT").setUri(myRemoteFilePath).setEntity(new FileEntity(localFile)).build(), fileStatus, localFile, "Created file");
                break;
            case UPDATED:
                doHttpRequest(RequestBuilder.create("PUT").setUri(myRemoteFilePath).setEntity(new FileEntity(localFile)).build(), fileStatus, localFile, "Updated file");
                break;
            case DELETED:
                doHttpRequest(RequestBuilder.create("DELETE").setUri(myRemoteFilePath).build(), fileStatus, localFile, "Deleted");
                break;
        }

        indicator.setFraction(1);
    }

    private int testRemoteFileExistence() {
        HttpUriRequest getRequest = RequestBuilder.create("HEAD").setUri(myRemoteFilePath).build();

        // action to the server configurations
        NotificationAction notificationAction = new NotificationAction("Configuration") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent,
                                        @NotNull Notification notification) {
                DataContext dataContext = anActionEvent.getDataContext();
                Project project = PlatformDataKeys.PROJECT.getData(dataContext);
                ShowSettingsUtil.getInstance().showSettingsDialog(project, StudioServerConfigurable.class);
            }
        };
        try (CloseableHttpResponse response = myHttpClient.execute(getRequest, myHttpContext)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 401) {
                Notification notification = new Notification(
                        "Salesforce",
                        "Unauthorized Request",
                        "Please check your server configurations.",
                        NotificationType.ERROR);
                notification.addAction(notificationAction);

                Notifications.Bus.notify(notification);
            }
            return statusCode;
        } catch (UnknownHostException e) {
            Notification notification = new Notification(
                    "Salesforce",
                    "Unknown Host",
                    "Please check your server configurations.",
                    NotificationType.ERROR);
            notification.addAction(notificationAction);

            Notifications.Bus.notify(notification);
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void createRemoteDirectories() {
        for (String path : myRemoteDirPaths) {
            HttpUriRequest mkcolRequest = RequestBuilder.create("MKCOL").setUri(path + "/").build();

            try (CloseableHttpResponse response = myHttpClient.execute(mkcolRequest, myHttpContext)) {
                if (response.getStatusLine().getStatusCode() == 201) {
                    Date now = new Date();
                    myConsoleView.print("[" + timeFormat.format(now) + "] " + "[Created folder] " + mkcolRequest.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doHttpRequest(HttpUriRequest request, FileStatus fileStatus, File localFile, String message) {
        try (CloseableHttpResponse ignored = myHttpClient.execute(request, myHttpContext)) {
            Date now = new Date();

            // TODO: update to add local file to console output.
            //  This message could get really long. Might make more sense to create a remote and local sync log.
            myConsoleView.print("[" + timeFormat.format(now) + "] " + "[" + message + " (" + localFile.getName() + ")] " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        } catch (IOException e) {
            // TODO add some messaging here for the user that something went wrong.
            LOG.error(e);
        }
    }

    private enum FileStatus {
        NEW, UPDATED, DELETED
    }
}
