package com.vrv.vap.admin.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;

/**ilang
 * @date 2020/11/28
 * @description
 */

public class WebSocketClientUtil extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientUtil.class);

    @Autowired
    private String token;


    public WebSocketClientUtil(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake shake) {
        System.out.println("握手...");
        for(Iterator<String> it = shake.iterateHttpFields(); it.hasNext();) {
            String key = it.next();
            System.out.println(key+":"+shake.getFieldValue(key));
        }
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        System.out.println("关闭...");
    }

    @Override
    public void onError(Exception e) {
        System.out.println("异常"+e);

    }

    @Override
    public void onMessage(String message) {
        // TODO Auto-generated method stub
        logger.info("-------- 接收到服务端数据： " + message + "--------");
        try {
            logger.info("转换前的结果 :" +message);
            JSONObject result = (JSONObject) JSON.parse(message);
            logger.info("转换后的结果 :" +result);
            if (result != null) {
                String stateCode = result.getString("stateCode");
                logger.info("stateCode is:" + stateCode);
                if (StringUtils.isNotEmpty(stateCode) && "001".equals(stateCode)) {
                    String tokenMessage = result.getString("token");
                    this.setToken(tokenMessage);
                    logger.info("token is:" + token);
                }
            }
        } catch (Exception e) {
            logger.info("转换异常"+e.getMessage());
            e.printStackTrace();
        }
    }


    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    public static void trustAllHosts(WebSocketClientUtil appClient) {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }


            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }


            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};


        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
