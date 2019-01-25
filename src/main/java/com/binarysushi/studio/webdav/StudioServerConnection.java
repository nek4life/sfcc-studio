package com.binarysushi.studio.webdav;

import com.binarysushi.studio.configuration.StudioConfigurationProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;

public class StudioServerConnection {
    private final StudioConfigurationProvider myConfigurationProvider;
    private final CloseableHttpClient client;
    private final HttpClientContext context;

    public StudioServerConnection(StudioConfigurationProvider configurationProvider) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        myConfigurationProvider = configurationProvider;

        // SSLContextFactory to allow all hosts. Without this an SSLException is thrown with self signed certs
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(3);
        connectionManager.setDefaultMaxPerRoute(2);

        client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        context = new HttpClientContext();
        context.setCredentialsProvider(getCredientials());
    }

    public String getBaseServerPath() {
        return String.format("https://%s/on/demandware.servlet/webdav/Sites/Cartridges/%s", myConfigurationProvider.getHostname(), myConfigurationProvider.getVersion());
    }

    public String getCartridgeName(String rootPath) {
        return Paths.get(rootPath).getFileName().toString();
    }

    public String getRemoteFilePath(String rootPath, String filePath) {
        String relPath = filePath.substring(rootPath.length(), filePath.length());
        String cartridgeName = getCartridgeName(rootPath);
        return getBaseServerPath() + "/" + cartridgeName + relPath;
    }

    public ArrayList<String> getRemoteDirPaths(String rootPath, String filePath) {
        ArrayList<String> serverPaths = new ArrayList<String>();
        Path relPath = Paths.get(rootPath).relativize(Paths.get(filePath)).getParent();
        String cartridgeName = getCartridgeName(rootPath);

        String dirPath = "";
        for (Path subPath : relPath) {
            dirPath = dirPath + "/" + subPath.getFileName();
            serverPaths.add(getBaseServerPath() + "/" + cartridgeName + dirPath);
        }

        return serverPaths;
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public HttpClientContext getContext() {
        return context;
    }

    public CredentialsProvider getCredientials() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(myConfigurationProvider.getHostname(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(myConfigurationProvider.getUsername(), myConfigurationProvider.getPassword()));
        return credentialsProvider;
    }

}

