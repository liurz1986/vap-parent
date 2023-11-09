package com.vrv.vap.line.tools;

import com.vrv.vap.line.model.EsQueryModel;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MyEsTools {

    private static RestHighLevelClient client = null;
    public static final long ES_CACHE_TIME = 600000;
    private static String IPS = "192.168.121.131";
    private static Integer PORT = 9200;
    private static String USER = "admin";
    private static String PASSWORD = "vrv@12345";
    private static String INDEX = "netflow-http-2022";
    private static final int CONNECT_TIME_OUT = 300000;
    private static final int SOCKET_TIME_OUT = 600000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 300000;
    private static final int MAX_RETRY_TIME_OUT = 300000;
    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;


    static {
        init();
    }

    public static RestHighLevelClient getClient(){
        if(client == null){
            init();
        }
        return client;
    }

    public static void init(){
        //log.info("------------------ClusterName：" + CLUSTERNAME);
        //log.info("------------------User:" + USER);
        if (null != client) {
            return;
        }

        RestClientBuilder builder = RestClient.builder(Arrays.asList(IPS.split(",")).stream().map(m -> {
            //log.info("elasticsearch node : " + m + ":" + PORT);
            return new HttpHost(m, PORT,null);
        }).collect(Collectors.toList()).toArray(new HttpHost[0]));

        // 服务认证
        if (org.apache.commons.lang.StringUtils.isNotEmpty(USER) && org.apache.commons.lang.StringUtils.isNotEmpty(PASSWORD)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USER, PASSWORD));
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);
                    httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }

        builder
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
                        requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);
                        requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
                        return requestConfigBuilder;
                    }
                });


        client = new RestHighLevelClient(builder);
    }

    public static SearchResponse scrollQuery(EsQueryModel queryModel, String scrollId, RestHighLevelClient client) {
        //checkPermissionFilter(queryModel);
        // 缓存时间 分页查询必须在该缓存时间之内进行
        TimeValue keepAlive = new TimeValue(ES_CACHE_TIME);
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
        SearchResponse response = null;
        if (scrollId != null) {
            searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(keepAlive);
            try {
                response = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SearchRequest request = buildQuery(queryModel);
            request.scroll(keepAlive);
            try {
                response = client.search(request, RequestOptions.DEFAULT);
            } catch (IndexNotFoundException e) {
            } catch (Exception e) {
            }
        }
        return response;
    }

    private static SearchRequest buildQuery(EsQueryModel queryModel) {
        SearchRequest request = null;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.from(queryModel.getStart()).size(queryModel.getCount());
        request = new SearchRequest(queryModel.getIndexName());
        // 是否排序
        if (queryModel.isSort()) {
            if (null != queryModel.getSortFields() && 0 < queryModel.getSortFields().length) {
                searchSourceBuilder.sort(
                        SortBuilders.fieldSort(queryModel.getSortFields()[0]).order(queryModel.getSortOrder()));
            }
        }
        // 是否添加聚合查询
        if (queryModel.isUseAggre() && null != queryModel.getAggregationBuilder()) {
            searchSourceBuilder.aggregation(queryModel.getAggregationBuilder());
        }
        // 是否添加多个聚合查询
        if (queryModel.isUseAggre() && null != queryModel.getMulAggregationBuilders()) {
            queryModel.getMulAggregationBuilders().forEach(r -> {
                searchSourceBuilder.aggregation(r);
            });
        }
        // 是否使用过滤器
        if (queryModel.isUseFilter() && null != queryModel.getFilterBuilder()) {
            searchSourceBuilder.postFilter(queryModel.getFilterBuilder());
        }
        // 是否添加时间段
        if (queryModel.isUseTimeRange()) {
            if (queryModel.getQueryBuilder() instanceof BoolQueryBuilder) {
                addTimeRange(queryModel, (BoolQueryBuilder) queryModel.getQueryBuilder());
            }
        }

        searchSourceBuilder.query(queryModel.getQueryBuilder());

        // 是否限制返回字段
        if (queryModel.isLimitResultFields()) {
            if (null != queryModel.getResultFields() && 0 != queryModel.getResultFields().length) {
                FetchSourceContext sourceContext = new FetchSourceContext(true,queryModel.getResultFields(),new String[]{});
                searchSourceBuilder.fetchSource(sourceContext);
                /*for (String field : queryModel.getResultFields()) {
                    searchSourceBuilder.docValueField(field);
                }*/
            }
        }

        if (StringUtils.isNotEmpty(queryModel.getTypeName())) {
            request.types(queryModel.getTypeName());
        }
        return request.source(searchSourceBuilder);
    }

    public static void addTimeRange(EsQueryModel queryModel, BoolQueryBuilder queryBuilder) {
        if (null == queryModel.getStartTime() || null == queryModel.getEndTime()) {
            return;
        }
        if (queryModel.isNeedTimeFormat()) {
            String sDate = TimeTools.format(queryModel.getStartTime(), queryModel.getTimeFormat());
            String eDate = TimeTools.format(queryModel.getEndTime(), queryModel.getTimeFormat());
            queryBuilder.must(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(sDate).to(eDate));
        } else {
            queryBuilder.must(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(queryModel.getStartTime())
                    .to(queryModel.getEndTime()));
        }
    }

    public static List<Map<String, String>> wrapResponse(SearchHits hits, String timeField) {
        List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
        for (SearchHit hit : hits) {
            Map<String, String> tmpData = new HashMap<String, String>();
            tmpData.put("_index", hit.getIndex());
            tmpData.put("_id", hit.getId());
            for (Map.Entry<String, Object> tmp : hit.getSourceAsMap().entrySet()) {
                tmpData.put(tmp.getKey(), null != tmp.getValue() ? tmp.getValue().toString() : "");
            }

            //utc2gmt(tmpData, timeField);

            datas.add(tmpData);
        }
        return datas;
    }

    public <T> void utc2gmt(Map<String, T> tmpData, String timeField) {
        Object time = tmpData.get(timeField);
        if (null == time) {
            return;
        }
        String gmtTime = "";
        if (time.toString().length() == 10) {
            gmtTime = time.toString();
        } else if (time.toString().indexOf("+0800") > -1) {
            gmtTime = TimeTools.chineseTimeFormat(time.toString());
        } else {
            gmtTime = TimeTools.utcToGmtTimeAsString(time.toString());
        }
        tmpData.put(timeField, (T) gmtTime);
    }

    public static EsQueryModel buildQueryModel2(String index, String time,String format,Integer day,String[] resultFields) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day-1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }*/
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(format);
        queryModel.setNeedTimeFormat(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{time});
        queryModel.setSortOrder(SortOrder.ASC);
        queryModel.setCount(10000);
        queryModel.setStart(0);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        queryModel.setLimitResultFields(true);
        queryModel.setResultFields(resultFields);
        return queryModel;
    }

    public static EsQueryModel buildScrollModel(String index, String time,String format,Integer day,String[] resultFields,int start,int count) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day-1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }*/
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(format);
        queryModel.setNeedTimeFormat(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{time});
        queryModel.setSortOrder(SortOrder.ASC);
        queryModel.setCount(count);
        queryModel.setStart(start);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        queryModel.setLimitResultFields(true);
        queryModel.setResultFields(resultFields);
        return queryModel;
    }

    public static Response search(String[] indexList, String queryJsonStr) {

        String indexStr = org.apache.commons.lang.StringUtils.join(indexList, ",");
        String method = "POST";
        String endpoint = "/" + indexStr + "/_search";
        HttpEntity entity = new NStringEntity(queryJsonStr, ContentType.APPLICATION_JSON);
        try {
            Request request = new Request(method, endpoint);
            request.setEntity(entity);
            Response response = client.getLowLevelClient().performRequest(request);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
