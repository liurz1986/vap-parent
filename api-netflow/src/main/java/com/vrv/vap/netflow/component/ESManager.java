package com.vrv.vap.netflow.component;


import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 获取es客户端
 *
 * @author xw
 * @date 2015年10月20日
 */
@Component
public class ESManager {

    private static final Logger log = LoggerFactory.getLogger(ESManager.class);

    public static int ES_TOTAL;

    private static final String EMPTY_STRING = "";

    @Autowired
    private StringRedisTemplate redisTpl;

    public static StringRedisTemplate redisTplUtil;

    @Value("${elk.total:10000}")
    public void setEsTotal(int esTotal) {
        ES_TOTAL = esTotal;
    }

    private ESManager() {

    }

    @PostConstruct
    public void init() {
        redisTplUtil = this.redisTpl;
    }

    public static RestClient getClient() {
        log.info("--------------SearchManager getClient start---------------");
        return ESClient.getInstance();
    }

    public static RestClient getRemoteClient(String clusterName,String ips,int port,String user,String password) {
        log.info("--------------SearchManager getRemoteClient start---------------");
        return ESClient.getClusterInstance(clusterName,ips,port,user,password);
    }

    public static Response sendGet(String endPoint) throws IOException {
        Request request = new Request("GET",endPoint);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response sendPost(String endPoint, HttpEntity entity ) throws IOException {
        Request request = new Request("POST",endPoint);
        request.setEntity(entity);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response sendPost(String endPoint, String entityStr ) throws IOException {
        HttpEntity entity = new NStringEntity(entityStr, ContentType.APPLICATION_JSON);
        Request request = new Request("POST",endPoint);
        request.setEntity(entity);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response sendRemoteGet(String endPoint) throws IOException {
        Request request = new Request("GET",endPoint);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response sendRemotePost(String endPoint, HttpEntity entity ) throws IOException {
        Request request = new Request("POST",endPoint);
        request.setEntity(entity);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response sendRemotePost(String endPoint, String entityStr ) throws IOException {
        HttpEntity entity = new NStringEntity(entityStr, ContentType.APPLICATION_JSON);
        Request request = new Request("POST",endPoint);
        request.setEntity(entity);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response sendPut(String endPoint, String entityStr ) throws IOException {
        HttpEntity entity = new NStringEntity(entityStr, ContentType.APPLICATION_JSON);
        Request request = new Request("PUT",endPoint);
        request.setEntity(entity);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response scrollSearch(String endPoint, String entityStr, long cacheTime) throws IOException {
        HttpEntity entity = new NStringEntity(entityStr, ContentType.APPLICATION_JSON);
        Request request = new Request("POST",endPoint);
        request.addParameter("scroll",cacheTime+"s");
        request.setEntity(entity);
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response search(String endpoint, String queryJsonStr) {
        HttpEntity entity = new NStringEntity(queryJsonStr, ContentType.APPLICATION_JSON);
        try {
            Response response = sendPost(endpoint, entity);
            return response;
        } catch (IOException e) {
            log.error(EMPTY_STRING, e);
        }
        return null;
    }

    public static Response scrollSearchById(String scrollId, long cacheTime) throws IOException {
        Request request = new Request("POST", "/_search/scroll");
        request.addParameter("scroll", cacheTime + "s");
        request.setJsonEntity(new StringBuilder("{\"scroll_id\":\"").append(scrollId).append("\",\"scroll\":\"")
                .append(cacheTime + "s").append("\"}").toString());
        Response response = getClient().performRequest(request);
        return response;
    }

    public static Response deleteByQuery(String index, String entityStr) {
        String query = "{\"query\": " + entityStr + "}";
        log.debug(query);
        Request request = new Request("POST", index + "/_delete_by_query");
        HttpEntity entity = new StringEntity(query, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            Response resp = getClient().performRequest(request);
            log.debug(resp.toString());
            return resp;
        } catch (IOException e) {
            log.error(EMPTY_STRING, e);
        }
        return null;
    }

    public static Response deleteById(String index, String type, String id) {
        Request request = new Request("DELETE", index + "/" + type + "/" + id);
        HttpEntity entity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            Response resp = getClient().performRequest(request);
            log.debug(resp.toString());
            return resp;
        } catch (IOException e) {
            log.error(EMPTY_STRING, e);
        }
        return null;
    }

}
