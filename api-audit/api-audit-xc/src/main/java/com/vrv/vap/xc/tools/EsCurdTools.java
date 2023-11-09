package com.vrv.vap.xc.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.xc.client.ElasticSearchManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * es增删改查
 * Created by lizj on 2018/12/27.
 */
public class EsCurdTools {

    private static final Log log = LogFactory.getLog(EsCurdTools.class);
    /**
     * 华为云认证模式需要重置为https
     */
    //public static String SCHEMA = "http";

    //private static SSLContext sslContext;

    private static RestHighLevelClient client = ElasticSearchManager.getClient();
/*
    static {
        try {
            //绕过https证书
            sslContext = HttpTools.getSslContext();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NoopHostnameVerifier());
        } catch ( Exception e) {
            log.error("create sslcontext fail",e);
        }
    }*/

    /**
     * 新增
     *
     * @param request
     * @return
     */
    public static IndexResponse add(IndexRequest request) {
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    public static DeleteResponse delete(DeleteRequest request) {
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改
     *
     * @param request
     * @return
     */
    public static UpdateResponse update(UpdateRequest request) {
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询
     *
     * @param request
     * @return
     */
    public static GetResponse get(GetRequest request) {
        try {
            System.out.println(request);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索（高级查询）
     *
     * @param request
     * @return
     */
    public static SearchResponse search(SearchRequest request) {
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量操作
     *
     * @param request
     * @return
     */
    public static BulkResponse batch(BulkRequest request) {
        try {
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExistIndex4Http(String index) {
        try {
            Request request = new Request("GET", index);
            Response response = client.getLowLevelClient().performRequest(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            log.error("ES isExistIndex4Http error", e);
        } finally {

        }
        return false;
    }

    public static Optional<JSONObject> simpleGetQueryHttp(String tailUrl) {
        log.debug("ES simpleGetQueryHttp, 请求url!!!!!!!!!!!!!!!!!!!!!!" + tailUrl);
        try {
            Request request = new Request("GET", tailUrl);
            Response response = client.getLowLevelClient().performRequest(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return Optional.of(JSON.parseObject(EntityUtils.toString(response.getEntity()), JSONObject.class));
            }
        } catch (Exception e) {
            log.error("ES simpleGetQueryHttp error", e);
        } finally {

        }
        return Optional.empty();
    }

    private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    /*private static void addAuth(EsConfig esConfig, HttpURLConnection connection) throws IOException {
        if (StringUtils.isNotEmpty(EsClient.getJaasPath())) {
            connection.setRequestProperty("Authorization", "Negotiate " + HWRestTokenBuilder.getSecurityToken(false));
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);

        } else {
            if (StringUtils.isNotEmpty(esConfig.getUser()) && StringUtils.isNotEmpty(esConfig.getPassword())) {
                String userAndPwd = esConfig.getUser() + ":" + esConfig.getPassword();
                byte[] authEncBytes = Base64.getEncoder().encode(userAndPwd.getBytes("utf-8"));
                String authStringEnc = new String(authEncBytes);
                connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            }
        }
    }*/

    public static String simpleGetQueryHttp2(String tailUrl) {

        try {
            Request request = new Request("GET", tailUrl);
            Response response = client.getLowLevelClient().performRequest(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return  EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            log.error("ES simpleGetQueryHttp2 error", e);
        } finally {

        }
        return null;
    }


    private static String getIp(String ip) {
        if (ip.contains(",")) {
            return ip.split(",")[0];
        }
        return ip;
    }

    public static void deleteIndex(String index) {
        try {
            Request request = new Request("DELETE", index);
            Response response = client.getLowLevelClient().performRequest(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                log.info(EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            log.error("ES deleteIndex error", e);
        } finally {

        }
    }

    public static Double getDiskUsedPercent() {
        Double result = 0d;
        /*EsConfig esConfig = VapEsApplication.getApplicationContext().getBean(EsConfig.class);

        HttpURLConnection connection = null;
        String url1 = SCHEMA + "://" + getIp(esConfig.getIp()) + ":" + esConfig.getPort() + "/" + "_cat/allocation";*/
        /*
        82 870.5mb 79gb 650.9gb 729.9gb 10 192.168.119.213 192.168.119.213 wUzcvaP
        30                                                                 UNASSIGNED
         */
        try {
            String diskInfo = simpleGetQueryHttp2("_cat/allocation");
            double allDisk = 0d;
            double allUsedDisk = 0d;
            if (StringUtils.isNotEmpty(diskInfo)) {
                String[] lines = diskInfo.toString().split("\n");
                if (lines.length > 0) {
                    for (String line : lines) {
                        String[] fields = line.replaceAll(" +", " ").split(" ");
                        if (fields.length > 2) {
                            allDisk += dataHandler(fields[4]);
                            allUsedDisk += dataHandler(fields[2]);
                        }
                    }
                }
            }
            result = allUsedDisk / allDisk * 100;
        } catch (Exception e) {
            log.error("ES getDiskUsedPercent error", e);
        } finally {

        }
        // 四舍五入保留两位小数
        return new BigDecimal(result).setScale(2, RoundingMode.UP).doubleValue();
    }

    /**
     * 磁盘大小单位统一转换
     *
     * @param size
     * @return
     */
    private static double dataHandler(String size) {
        double result = 0d;
        if (StringUtils.isEmpty(size)) {
            return result;
        }
        // 统一使用gb作为单位
        if (size.endsWith("tb")) {
            result = Double.parseDouble(size.replace("tb", "")) * 1024;
        } else if (size.endsWith("mb")) {
            result = Double.parseDouble(size.replace("mb", "")) / 1024;
        } else if (size.endsWith("kb")) {
            result = Double.parseDouble(size.replace("kb", "")) / 1024 / 1024;
        } else {
            result = Double.parseDouble(size.replace("gb", ""));
        }
        return result;
    }
}
