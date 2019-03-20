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
    private final PoolingHttpClientConnectionManager myConnectionManager;

    public StudioServerConnection(StudioConfigurationProvider configurationProvider) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        myConfigurationProvider = configurationProvider;
        // SSLContextFactory to allow all hosts. Without this an SSLException is thrown with self signed certs
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", socketFactory).build();

        myConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        myConnectionManager.setMaxTotal(3);
        myConnectionManager.setDefaultMaxPerRoute(2);
    }

    public String getBaseServerPath() {
        return String.format("https://%s/on/demandware.servlet/webdav/Sites/Cartridges/%s", myConfigurationProvider.getHostname(), myConfigurationProvider.getVersion());
    }

    private String getCartridgeName(String rootPath) {
        return Paths.get(rootPath).getFileName().toString();
    }

    String getRemoteFilePath(String rootPath, String filePath) {
        String relPath = filePath.substring(rootPath.length());
        String cartridgeName = getCartridgeName(rootPath);
        return getBaseServerPath() + "/" + cartridgeName + relPath;
    }

    ArrayList<String> getRemoteDirPaths(String rootPath, String filePath) {
        ArrayList<String> serverPaths = new ArrayList<>();
        // There may be no parent path in root directory
        Path relPath = Paths.get(rootPath).relativize(Paths.get(filePath)).getParent();
        String cartridgeName = getCartridgeName(rootPath);

        String dirPath = "";

        if (relPath != null) {
            for (Path subPath : relPath) {
                dirPath = dirPath + "/" + subPath.getFileName();
                serverPaths.add(getBaseServerPath() + "/" + cartridgeName + dirPath);
            }
        }

        return serverPaths;
    }

    public CloseableHttpClient getClient() {
        return HttpClients.custom().setConnectionManager(myConnectionManager).build();
    }

    public HttpClientContext getContext() {
        final HttpClientContext context;
        context = new HttpClientContext();
        context.setCredentialsProvider(getCredentials());
        return context;
    }

    private CredentialsProvider getCredentials() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(myConfigurationProvider.getHostname(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(myConfigurationProvider.getUsername(), myConfigurationProvider.getPassword()));
        return credentialsProvider;
    }

}

