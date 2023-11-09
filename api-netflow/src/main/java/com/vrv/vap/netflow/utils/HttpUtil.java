package com.vrv.vap.netflow.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
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

public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);


    private static CloseableHttpClient httpsClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        RequestConfig requestConfig = RequestConfig.custom()
                // 从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(5 * 1000)
                //与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
                .setConnectTimeout(5 * 1000)
                //socket读数据超时时间：从服务器获取响应数据的超时时间
                .setSocketTimeout(30 * 1000)
                .build();
        return HttpClients.custom().setSSLSocketFactory(connectionFactory).setDefaultRequestConfig(requestConfig).build();
    }

    private static CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                // 从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(5 * 1000)
                //与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
                .setConnectTimeout(5 * 1000)
                //socket读数据超时时间：从服务器获取响应数据的超时时间
                .setSocketTimeout(30 * 1000)
                .build();
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
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
        } catch (Exception e) {
            logger.error("",e);
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
        } catch (Exception e) {
            logger.error("https调用异常！",e);
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
