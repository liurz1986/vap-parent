package com.vrv.vap.monitor.agent.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

public class HTTPUtil {

    private static Logger logger = LoggerFactory.getLogger(HTTPUtil.class);

    private static CloseableHttpClient httpsClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
    }

    private static CloseableHttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

    private static void setHeader(AbstractHttpMessage httpMessage, Map<String, String> headers) {
        if (headers != null) {
            Iterator<String> it = headers.keySet().iterator();
            while (it.hasNext()) {
                String name = it.next();
                String value = headers.get(name);
                httpMessage.setHeader(name, CleanUtil.cleanString(value));
            }
        }

    }

    public static String GET(String url, Map<String, String> headers) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = url.startsWith("https://") ? httpsClient() : httpClient();
       // HttpEntity entity;
        String result = "";
        try {
            HttpGet get = new HttpGet(url);
            setHeader(get, headers);
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
        } finally {
            httpClient.close();
        }
        //输出网页源码
//        logger.info("GET response:" + result);
        return result;
    }


    public static String POST(String url, Map<String, String> headers, String request) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = url.startsWith("https://") ? httpsClient() : httpClient();
        HttpEntity entity;
        String result ="";
        try {
            HttpPost post = new HttpPost(url);
            setHeader(post, headers);
            if (StringUtils.isNotBlank(request)) {
                StringEntity postingString = new StringEntity(request, "utf-8");
                post.setEntity(postingString);
            }
            CloseableHttpResponse response = httpClient.execute(post);
            entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
        } finally {
            httpClient.close();
        }
        //输出网页源码

//        logger.info("POST response:" + result);
        return result;
    }

    public static String PUT(String url, Map<String, String> headers, String request) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = url.startsWith("https://") ? httpsClient() : httpClient();
        HttpEntity entity;
        String result ="";
        try {
            HttpPut put = new HttpPut(url);
            setHeader(put, headers);
            if (StringUtils.isNotBlank(request)) {
                StringEntity postingString = new StringEntity(request, "utf-8");
                put.setEntity(postingString);
            }
            CloseableHttpResponse response = httpClient.execute(put);
            entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
        } finally {
            httpClient.close();
        }
        return result;
    }
}
