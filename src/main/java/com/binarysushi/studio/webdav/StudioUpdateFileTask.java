package com.binarysushi.studio.webdav;

import com.binarysushi.studio.toolWindow.StudioConsoleService;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
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

        if (testRemoteFileExistence() == 200) {
            if (myEventFile.exists()) {
                fileStatus = FileStatus.UPDATED;
            } else {
                fileStatus = FileStatus.DELETED;
            }
        } else {
            fileStatus = FileStatus.NEW;
        }

        indicator.setFraction(.5);

        switch (fileStatus) {
            case NEW:
                createRemoteDirectories();
                doHttpRequest(RequestBuilder.create("PUT").setUri(myRemoteFilePath).setEntity(new FileEntity(localFile)).build(), fileStatus);
                break;
            case UPDATED:
                doHttpRequest(RequestBuilder.create("PUT").setUri(myRemoteFilePath).setEntity(new FileEntity(localFile)).build(), fileStatus);
                break;
            case DELETED:
                doHttpRequest(RequestBuilder.create("DELETE").setUri(myRemoteFilePath).build(), fileStatus);
                break;
        }

        indicator.setFraction(1);
    }

    private int testRemoteFileExistence() {
        HttpUriRequest getRequest = RequestBuilder.create("HEAD").setUri(myRemoteFilePath).build();
        try (CloseableHttpResponse response = myHttpClient.execute(getRequest, myHttpContext)) {
            return response.getStatusLine().getStatusCode();
        } catch (UnknownHostException e) {
            Notifications.Bus.notify(new Notification("Salesforce", "Unknown Host",
                    "Please check your server configuration in the project settings panel.", NotificationType.INFORMATION));
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
                    myConsoleView.print("[" + timeFormat.format(now) + "] " + "Created " + mkcolRequest.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doHttpRequest(HttpUriRequest request, FileStatus fileStatus) {
        try (CloseableHttpResponse ignored = myHttpClient.execute(request, myHttpContext)) {
            Date now = new Date();
            String message = "";
            switch (fileStatus) {
                case NEW:
                    message = "Created";
                    break;
                case DELETED:
                    message = "Deleted";
                    break;
                case UPDATED:
                    message = "Updated";
                    break;
            }

            myConsoleView.print("[" + timeFormat.format(now) + "] " + message + " " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        } catch (IOException e) {
            // TODO add some messaging here for the user that something went wrong.
            LOG.error(e);
        }
    }

    private enum FileStatus {
        NEW, UPDATED, DELETED
    }
}
