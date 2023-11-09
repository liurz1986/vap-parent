package com.vrv.vap.data.component;


import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.util.JsonUtil;
import com.vrv.vap.data.vo.EsAliasMappingVo;
import com.vrv.vap.data.vo.EsQueryResult;
import com.vrv.vap.data.vo.EsSourceVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
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
import java.util.*;

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

    public static Response sendRemoteGet(RestClient restClient,String endPoint) throws IOException {
        Request request = new Request("GET",endPoint);
        Response response = restClient.performRequest(request);
        return response;
    }

    public static Response sendRemotePost(RestClient restClient,String endPoint, HttpEntity entity ) throws IOException {
        Request request = new Request("POST",endPoint);
        request.setEntity(entity);
        Response response = restClient.performRequest(request);
        return response;
    }

    public static Response sendRemotePost(RestClient restClient,String endPoint, String entityStr ) throws IOException {
        HttpEntity entity = new NStringEntity(entityStr, ContentType.APPLICATION_JSON);
        Request request = new Request("POST",endPoint);
        request.setEntity(entity);
        Response response = restClient.performRequest(request);
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

    public static Map<String,List<EsAliasMappingVo>> toEsAliasesResult(Response response){
        if (response == null) {
            return null;
        }
        Map<String,List<EsAliasMappingVo>> esAliasesResult = new HashMap<>();
        try {
            String responseStr = EntityUtils.toString(response.getEntity());
            if (StringUtils.isNotEmpty(responseStr)) {
                Map result = JsonUtil.jsonToMap(responseStr);
                Iterator iterator = result.keySet().iterator();
                while (iterator.hasNext()) {
                    String indexName = (String) iterator.next();
                    Map indexAliases = (Map) result.get(indexName);
                    Map aliases = (Map) indexAliases.get("aliases");
                    Iterator it = aliases.keySet().iterator();
                    List<EsAliasMappingVo> esAliasMappingVoList = new ArrayList<>();
                    while (it.hasNext()) {
                        EsAliasMappingVo esAliasMappingVo = new EsAliasMappingVo();
                        String aliasName = (String) it.next();
                        Map filterMap = (Map) aliases.get(aliasName);
                        Map filter = (Map) filterMap.get("filter");
                        esAliasMappingVo.setAliasName(aliasName);
                        esAliasMappingVo.setFilter(filter);
                        esAliasMappingVoList.add(esAliasMappingVo);
                    }
                    esAliasesResult.put(indexName,esAliasMappingVoList);
                }
            }
            return esAliasesResult;
        } catch (IOException e) {
            log.error("",e);
        }
        return null;
    }

    public static EsQueryResult toEsQueryResult(Response response){
        if (response == null) {
            return null;
        }
        EsQueryResult queryResult = new EsQueryResult();
        try {
            String responseStr = EntityUtils.toString(response.getEntity());
            if (StringUtils.isNotEmpty(responseStr)) {
                Map result = JsonUtil.jsonToMap(responseStr);
                Integer took = (Integer) result.get("took");
                Boolean timeOut = (Boolean) result.get("timed_out");
                String scrollId = (String) result.get("_scroll_id");
                queryResult.setTook(took);
                queryResult.setTimeOut(timeOut);
                queryResult.setScrollId(scrollId);
                Map mapHits = (Map) result.get("hits");
                String version = redisTplUtil.opsForValue().get(SYSTEM.ES_VERSION);
                Long total;
                if (version.compareTo(SYSTEM.VERSION_SEVEN) < 0) {
                    total = Long.valueOf((Integer) mapHits.get("total"));
                    queryResult.setTotal(total);
                } else {
                    Map totalMap = (Map) mapHits.get("total");
                    total = Long.valueOf((Integer) totalMap.get("value"));
                    queryResult.setTotal(total);
                }
                if (total > ES_TOTAL) {
                    queryResult.setTotal(Long.valueOf(ES_TOTAL));
                } else {
                    queryResult.setTotal(total);
                }
                List<Map> hitsList = (List<Map>) mapHits.get("hits");
                List<EsSourceVo> hits = transferHitsToVo(hitsList);
                queryResult.setHits(hits);
                return queryResult;
            }
        } catch (IOException e) {
            log.error("",e);
        }
        return null;
    }

    public static List<EsSourceVo> transferHitsToVo(List<Map> hits) {
        List<EsSourceVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(hits)) {
            return null;
        }
        for (Map hit : hits) {
            EsSourceVo esSourceVo = new EsSourceVo();
            esSourceVo.setId((String) hit.get("_id"));
            esSourceVo.setType((String) hit.get("_type"));
            esSourceVo.setIndex((String) hit.get("_index"));
            esSourceVo.setSource((Map<String, Object>) hit.get("_source"));
            result.add(esSourceVo);
        }
        return result;
    }
}
