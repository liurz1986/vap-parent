package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.util.CleanUtil;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequestUtil {
    private static final String ACCEPT = "accept";
    private static final String CONNECTION = "connection";
    private static final String KEEP_ALIVE = "Keep-Alive";
    private static final String USER_AGENT = "user-agent";
    private static final String MOZILLA_VERSION = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";
    private static final String URL_VALUE = "*/*";
    private static final String ERROR_MESSAGE = "发送GET请求出现异常！";
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(CleanUtil.cleanString(urlNameString));
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty(ACCEPT, URL_VALUE);
            connection.setRequestProperty(CONNECTION, KEEP_ALIVE);
            connection.setRequestProperty(USER_AGENT, MOZILLA_VERSION);
            connection.setConnectTimeout(3000);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            logger.info("返回状态码：" + connection.getResponseCode());
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println(ERROR_MESSAGE + e);
            logger.error("",e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("",e2);
            }
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    /*public static byte[] sendGetMethod(String url) {
        byte[] bytes = new byte[0];
        InputStream in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty(ACCEPT, URL_VALUE);
            connection.setRequestProperty(CONNECTION, KEEP_ALIVE);
            connection.setRequestProperty(USER_AGENT, MOZILLA_VERSION);
            connection.setConnectTimeout(3000);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = connection.getInputStream();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            bytes = swapStream.toByteArray();
        } catch (Exception e) {
            System.out.println(ERROR_MESSAGE + e);
            logger.error("",e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("",e2);
            }
        }
        return bytes;
    }

    *//**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(CleanUtil.cleanString(url));
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty(ACCEPT, URL_VALUE);
            conn.setRequestProperty(CONNECTION, KEEP_ALIVE);
            conn.setRequestProperty(USER_AGENT, MOZILLA_VERSION);
            conn.setConnectTimeout(3000);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            logger.info("返回状态码：" + conn.getResponseCode());
            InputStream xx = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(xx));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            logger.error("",e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error("",ex);
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPostRedircet(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(CleanUtil.cleanString(url));
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty(ACCEPT, URL_VALUE);
            conn.setRequestProperty(CONNECTION, KEEP_ALIVE);
            conn.setRequestProperty(USER_AGENT, MOZILLA_VERSION);
            conn.setConnectTimeout(3000);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(CleanUtil.cleanString(param));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            logger.info("返回状态码：" + conn.getResponseCode());
            InputStream xx = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(xx));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            if (conn.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY || conn.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                //如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
                String location = conn.getHeaderField("Location");
               return location;
            }

        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            logger.error("",e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error("",ex);
            }
        }
        return result;
    }


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendHttpsRestGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            HttpsURLConnection.setDefaultHostnameVerifier(new HttpRequestUtil().new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL realUrl = new URL(CleanUtil.cleanString(urlNameString));
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty(ACCEPT, URL_VALUE);
            connection.setRequestProperty(CONNECTION, KEEP_ALIVE);
            connection.setRequestProperty(USER_AGENT, MOZILLA_VERSION);
            connection.setConnectTimeout(3000);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            InputStream xx = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(xx));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println(ERROR_MESSAGE + e);
            logger.error("",e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("",e2);
            }
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param httpsUrl 发送请求的URL
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendHttpsSslGet(String httpsUrl) {
        String result = "";
        BufferedReader in = null;
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HttpRequestUtil().new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier(){
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            URL url = new URL(CleanUtil.cleanString(httpsUrl));
            // 打开和URL之间的连接
            HttpsURLConnection  connection = (HttpsURLConnection)url.openConnection();
            SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(sslSocketFactory);
            // 设置通用的请求属性
            connection.setRequestProperty(ACCEPT, URL_VALUE);
            connection.setRequestProperty(CONNECTION, KEEP_ALIVE);
            connection.setRequestProperty(USER_AGENT, MOZILLA_VERSION);
            connection.setConnectTimeout(3000);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            InputStream xx = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(xx));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println(ERROR_MESSAGE + e);
            logger.error("",e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("",e2);
            }
        }
        return result;
    }

    static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return new java.security.cert.X509Certificate[0];
        }
    } };

    public class NullHostNameVerifier implements HostnameVerifier {
        /*
         * (non-Javadoc)
         *
         * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
         * javax.net.ssl.SSLSession)
         */
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            // TODO Auto-generated method stub
            return true;
        }
    }
}
