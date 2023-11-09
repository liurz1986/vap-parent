package com.vrv.vap.server.zuul.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

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
                httpMessage.setHeader(name, value);
            }
        }

    }

    public static String GET(String url, Map<String, String> headers) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = url.startsWith("https://") ? httpsClient() : httpClient();
        String result = "";
        try {
            HttpGet get = new HttpGet(url);
            setHeader(get, headers);
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            //输出网页源码
            result = EntityUtils.toString(entity, "utf-8");
            logger.info("GET response:" + LogForgingUtil.validLog(result));
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            httpClient.close();
        }
        return result;
    }


    public static String POST(String url, Map<String, String> headers, String request) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = url.startsWith("https://") ? httpsClient() : httpClient();
        String result = "";
        try {
            HttpPost post = new HttpPost(url);
            setHeader(post, headers);
            if (StringUtils.isNotBlank(request)) {
                StringEntity postingString = new StringEntity(request, "utf-8");
                post.setEntity(postingString);
            }
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            //输出网页源码
            result = EntityUtils.toString(entity, "utf-8");
            //logger.info("POST response:" + result);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            httpClient.close();
        }
        return result;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", "aa");
//        headers.put("appid", "402892a36bbd9b2c016bfa049b8c0094");
        headers.put("appid", "402892a36bbd9b2c016c27ec296204a9");
//
//
        headers.put("Content-Type", "application/json");
//        headers.put("Accept", "application/json");
        Map<String, String> param = new HashMap<>();
////        param.put("roleId", "402892a36bbd9b2c016bfdd3dd1c01ff");
        param.put("roleId", "402892a36bbd9b2c016c282a3c91056b");
//        param.put("systemId", "402892a36bbd9b2c016c27ec296204a9");
//        param.put("userId", "402892a36bbd9b2c016c176e29cd02f6");


//        String request = JSON.toJSONString(param);
//
//
//        System.out.println(request);
////        appid = "402892a36bbd9b2c016c27ec296204a9"
//
//
//        String method = "/user/getPermissionInfoByUserId.do";
//        String method = "/systemResource/getBusiSystem.do";
//        String method = "/user/getObjectsByUserId.do";
//        String method = "/user/getBulkUserInfo.do";
        String method = "/resource/getFuncTreeByRoleId.do";
//        String method = "/resource/getSubFuncs.do";
//        String method = "/user/getObjectsByUserId.do";
//        String method = "/resource/getFuncTree.do";

//        String request = JSON.toJSONString(param);
//        System.out.println(request);
//        String url = "http://sgcc.isc.com:22001/isc_frontmv_serv" + method;
//        System.out.println(POST(url, headers, request));
//
//
//        Object o = new RestTemplate().postForObject(url + method, entity, Object.class);
////        System.out.println(JSON.toJSONString(o));
////
////
//
////        String request = "{\"roleId\":\"402892a36bbd9b2c016bfa049b8c0094\"}";
//        String url = "http://sgcc.isc.com:22001/isc_frontmv_serv" + method;
//        System.out.println(POST(url, headers, request));
//        String result  = GET("https://192.168.8.225/CEMS/cascade/cascadeTerminalAwarenessAction_createTicket.do",null);

//        String result  = GET("https://192.168.8.225/CEMS/cascade/cascadeTerminalAwarenessAction_authenticationTicket.do?ticket=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjU1ODc3MjUsInVzZXJJZCI6ImZkMDNkZGI3OTdkMTQxYTdiYWExMzZhZTc5NGUzNmRlIiwibG9naW5OYW1lIjoic2VjcmVjeSJ9.wi0KrUAWB9Dm-UhKyUZah8YEoOnHws-r7Wfz3SpKtHQ",null);
//        System.out.println(result);
    }





}
