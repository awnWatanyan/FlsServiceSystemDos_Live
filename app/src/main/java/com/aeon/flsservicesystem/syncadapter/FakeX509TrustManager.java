package com.aeon.flsservicesystem.syncadapter;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

public class FakeX509TrustManager implements X509TrustManager {

    private static TrustManager[] trustManagers;
    private static final X509Certificate[] acceptedIssuers = new X509Certificate[] {};

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return acceptedIssuers;
    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[] { new FakeX509TrustManager() };
        }

        try {
            context = SSLContext.getInstance("TLSv1.2");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException| KeyManagementException e) {
            Timber.e("Exception encountered: %s", e.getMessage());
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }
}
