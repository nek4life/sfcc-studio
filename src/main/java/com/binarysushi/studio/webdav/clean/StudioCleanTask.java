package com.binarysushi.studio.webdav.clean;

import com.binarysushi.studio.configuration.StudioConfigurationProvider;
import com.binarysushi.studio.toolWindow.StudioConsoleService;
import com.binarysushi.studio.webdav.StudioServerConnection;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.io.ZipUtil;
import org.apache.http.Consts;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class StudioCleanTask extends Task.Backgroundable {
    private final Project myProject;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    StudioCleanTask(Project project, String title, boolean canBeCancelled, PerformInBackgroundOption backgroundOption) {
        super(project, title, canBeCancelled, backgroundOption);
        myProject = project;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(myProject);
        ArrayList<String> CartridgeRoots = configurationProvider.getCartridgeRoots();
        if (configurationProvider.getCartridgeRoots().size() < 1) {
            return;
        }

        ConsoleView consoleView = ServiceManager.getService(myProject, StudioConsoleService.class).getConsoleView();
        StudioServerConnection serverConnection = ServiceManager.getService(myProject, StudioServerConnection.class);

        String version = configurationProvider.getVersion();
        File tempDir = Paths.get(FileUtil.getTempDirectory(), myProject.getName()).toFile();
        File versionDir = Paths.get(tempDir.toString(), version).toFile();
        File zipFile = Paths.get(tempDir.toString(), version + ".zip").toFile();
        FileUtil.createDirectory(versionDir);

        indicator.setText("Preparing Archive");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (String cartridgeRoot : CartridgeRoots) {
                File dir = new File(cartridgeRoot);
                if (dir.exists()) {
                    FileUtil.copyDir(dir, Paths.get(versionDir.toString(), dir.getName()).toFile());
                }
            }

            ZipUtil.addDirToZipRecursively(zipOutputStream, null, versionDir, version, null, null);
            zipOutputStream.close();
            indicator.setFraction(.166);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CloseableHttpClient client = serverConnection.getClient();
        HttpClientContext context = serverConnection.getContext();

        HttpUriRequest uploadRequest = RequestBuilder.put()
                .setUri(serverConnection.getBaseServerPath() + ".zip")
                .setEntity(new FileEntity(zipFile))
                .build();

        HttpUriRequest deleteVersionRequest = RequestBuilder.delete().setUri(serverConnection.getBaseServerPath()).build();

        List<BasicNameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("method", "UNZIP"));

        HttpUriRequest unzipRequest = RequestBuilder.post()
                .setUri(serverConnection.getBaseServerPath() + ".zip")
                .setEntity(new UrlEncodedFormEntity(data, Consts.UTF_8))
                .build();

        HttpUriRequest deleteZipRequest = RequestBuilder.delete().setUri(serverConnection.getBaseServerPath() + ".zip").build();

        indicator.setText("Uploading archive...");
        try (CloseableHttpResponse response = client.execute(uploadRequest, context)) {
            indicator.setFraction(.332);
        } catch (IOException e) {
            e.printStackTrace();
        }

        indicator.setText("Removing previous version...");
        try (CloseableHttpResponse response = client.execute(deleteVersionRequest, context)){
            indicator.setFraction(.498);
        } catch (IOException e) {
            e.printStackTrace();
        }

        indicator.setText("Unzipping archive...");
        try (CloseableHttpResponse response = client.execute(unzipRequest, context)) {
            indicator.setFraction(.664);
        } catch (IOException e) {
            e.printStackTrace();
        }

        indicator.setText("Removing temporary files...");
        try (CloseableHttpResponse response = client.execute(deleteZipRequest, context)) {
            indicator.setFraction(.83);
        } catch (IOException e) {
            e.printStackTrace();
        }

        consoleView.print("[" + timeFormat.format(new Date()) + "] " + "Cleaned " + serverConnection.getBaseServerPath() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        FileUtil.delete(tempDir);
        indicator.setFraction(1);
    }
}
