package com.vrv.vap.toolkit.tools;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * http请求工具类(支持https)
 */
public class HttpTools {

    private static final Logger log = LoggerFactory.getLogger(HttpTools.class);

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int STATUS_200 = 200;

    public static String doGetJson(String url) throws Exception {
        CloseableHttpClient client = null;
        try {
            if (url == null || url.trim().length() == 0) {
                throw new Exception("URL is null");
            }
            // client = HttpClients.createDefault();
            client = createAcceptSelfSignedCertificateClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse resp = client.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() == STATUS_200) {
                return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
            }
            if ((resp.getStatusLine().getStatusCode()) != STATUS_200) {
                httpGet = new HttpGet(url);
                resp = client.execute(httpGet);
                if (resp.getStatusLine().getStatusCode() == STATUS_200) {
                    return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
                }
                throw new Exception("httpCode is " + resp.getStatusLine().getStatusCode() + "，未获取到响应数据");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(client);
        }
        return "";
    }

    public static String doPostJson(String url, String json) throws Exception {
        CloseableHttpClient client = null;
        try {
            if (url == null || url.trim().length() == 0) {
                throw new Exception("URL is null");
            }
            client = createAcceptSelfSignedCertificateClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));

            HttpResponse resp = client.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == STATUS_200) {
                return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
            }
            if ((resp.getStatusLine().getStatusCode()) != STATUS_200) {
                httpPost = new HttpPost(url);
                resp = client.execute(httpPost);
                if (resp.getStatusLine().getStatusCode() == STATUS_200) {
                    return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
                }
                throw new Exception("httpCode is " + resp.getStatusLine().getStatusCode() + "，未获取到响应数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            close(client);
        }
        return "";
    }

    public static String doPutJson(String url, String json) throws Exception {
        CloseableHttpClient client = null;
        try {
            if (url == null || url.trim().length() == 0) {
                throw new Exception("URL is null");
            }
            client = createAcceptSelfSignedCertificateClient();
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader("Content-Type", "application/json");
            httpPut.setEntity(new StringEntity(json, "UTF-8"));

            HttpResponse resp = client.execute(httpPut);
            if (resp.getStatusLine().getStatusCode() == STATUS_200) {
                return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
            }
            if ((resp.getStatusLine().getStatusCode()) != STATUS_200) {
                httpPut = new HttpPut(url);
                resp = client.execute(httpPut);
                if (resp.getStatusLine().getStatusCode() == STATUS_200) {
                    return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
                }
                throw new Exception("httpCode is " + resp.getStatusLine().getStatusCode() + "，未获取到响应数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            close(client);
        }
        return "";
    }

    private static void close(CloseableHttpClient client) {
        if (client == null) {
            return;
        }
        try {
            client.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient()
            throws KeyManagementException, NoSuchAlgorithmException,
            KeyStoreException {

        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        // SSLContext sslContext = SSLContextBuilder
        // .create()
        // .loadTrustMaterial(new TrustSelfSignedStrategy())
        // .build();
        SSLConnectionSocketFactory connectionFactory = getSslConnectionSocketFactory();

        // finally create the HttpClient using HttpClient factory methods and
        // assign the ssl socket factory
        return HttpClients.custom().setSSLSocketFactory(connectionFactory)
                .build();
    }

    public static SSLConnectionSocketFactory getSslConnectionSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = getSslContext();

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to
        // include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust
        // self signed certificate strategy
        // and allow all hosts verifier.
        return new SSLConnectionSocketFactory(sc, allowAllHosts);
    }

    public static SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new CustomTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        return sc;
    }

    public static SchemeIOSessionStrategy getSchemeIOSessionStrategy() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = getSslContext();

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to
        // include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust
        // self signed certificate strategy
        // and allow all hosts verifier.
        return new SSLIOSessionStrategy(sc, allowAllHosts);
    }

    static class CustomTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

    public static void main(String[] args) throws Exception {
//        String result = doGetJson("https://www.cnblogs.com/lkeji388/p/9677464.html");
        String result = doGetJson("http://192.168.119.213:8848/nacos");
        System.out.println("result:" + result);
    }
}
