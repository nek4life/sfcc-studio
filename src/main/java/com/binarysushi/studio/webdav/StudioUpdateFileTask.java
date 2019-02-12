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
    private final CloseableHttpClient httpClient;
    private final HttpClientContext context;
    private final ArrayList<String> remoteDirPaths;
    private final String remoteFilePath;
    private final String localFilePath;
    private final Project project;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    StudioUpdateFileTask(Project project,
                         final String title,
                         final boolean canBeCancelled,
                         final PerformInBackgroundOption backgroundOption,
                         String sourceRootPath,
                         String localFilePath) {
        super(project, title, canBeCancelled, backgroundOption);
        StudioServerConnection serverConnection = ServiceManager.getService(project, StudioServerConnection.class);
        this.project = project;
        this.localFilePath = localFilePath;
        this.httpClient = serverConnection.getClient();
        this.context = serverConnection.getContext();
        this.remoteDirPaths = serverConnection.getRemoteDirPaths(sourceRootPath, localFilePath);
        this.remoteFilePath = serverConnection.getRemoteFilePath(sourceRootPath, localFilePath);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        boolean isNewRemoteFile = true;
        ConsoleView consoleView = ServiceManager.getService(project, StudioConsoleService.class).getConsoleView();
        indicator.setFraction(.33);

        HttpUriRequest getRequest = RequestBuilder.create("HEAD").setUri(remoteFilePath).build();
        try (CloseableHttpResponse response = httpClient.execute(getRequest, context)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                isNewRemoteFile = false;
            }

            if (response.getStatusLine().getStatusCode() == 401) {
                Notifications.Bus.notify(new Notification("Salesforce", "Unauthorized Request",
                        "Please check your server configuration in the project settings panel. (File | Settings | Tools | Commerce Cloud Server)", NotificationType.ERROR));
                Notifications.Bus.notify(new Notification("Salesforce", "Unauthorized Request",
                        getRequest.getURI().toString(), NotificationType.ERROR));

                return;
            }
        } catch (UnknownHostException e) {
            Notifications.Bus.notify(new Notification("Salesforce", "Unknown Host",
                    "Please check your server configuration in the project settings panel. (File | Settings | Tools | Commerce Cloud Server)", NotificationType.ERROR));
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        indicator.setFraction(.5);

        // Create Remote Directories if file is a new local or remote file
        if (isNewRemoteFile) {
            for (String path : remoteDirPaths) {
                HttpUriRequest mkcolRequest = RequestBuilder.create("MKCOL").setUri(path + "/").build();

                try (CloseableHttpResponse response = httpClient.execute(mkcolRequest, context)) {
                    if (response.getStatusLine().getStatusCode() == 201) {
                        Date now = new Date();
                        consoleView.print("[" + timeFormat.format(now) + "] " + "Created folder " + mkcolRequest.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        indicator.setFraction(.80);

        File file = new File(localFilePath);
        // Put remote file
        HttpUriRequest request = RequestBuilder.create("PUT")
                .setUri(remoteFilePath)
                .setEntity(new FileEntity(file))
                .build();

        try (CloseableHttpResponse response = httpClient.execute(request, context)) {
            Date now = new Date();
            if (isNewRemoteFile) {
                consoleView.print("[" + timeFormat.format(now) + "] " + "Created file (" + file.getName() + ") on server " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                consoleView.print("[" + timeFormat.format(now) + "] " + "Updated file (" + file.getName() + ") on server " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            }
        } catch (IOException e) {
            LOG.error(e);
        }

        indicator.setFraction(1);
    }
}
