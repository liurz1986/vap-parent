package com.vrv.vap.line.tools;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.line.client.ElasticSearchManager;
import com.vrv.vap.line.model.BaseLineFrequent;
import com.vrv.vap.line.model.BaseLineFrequentAttr;
import com.vrv.vap.line.model.JUserLogs;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.lang.StringUtils;
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
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class SearchTest {
    private static RestHighLevelClient client = null;

    private static String IPS = "192.168.120.201";
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
    private static int timeSplit = 3;//切分时间间隔 单位 s
    private static int towLevelTimeSplit = 5;//二级切分时间间隔 单位 min
    private static int days = 60;//处理数据天数
    private static int BATCH = 1000;//单次入库量
    private static String timeField = "event_time";//处理数据天数
    private static String urlField = "url";//主体字段
    private static String separator = ",";//项分隔符
    private static String userField = "sip";//项分隔符
    private static String pckField = "content_length";//项分隔
    private static ExecutorService exec = Executors.newFixedThreadPool(6);


    public static void main(String[] args) {
        System.out.println("###################SearchTest start###################");
        Map<String,Object> aggMap = new HashMap<>();
        try {
            init();
            //搜索资源构造器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //创建复合查询条件对象
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.rangeQuery(timeField).from(TimeTools.getNowBeforeByDay(days-1))
                            .to(TimeTools.getNowBeforeByDay2(1)));
            //如以下条件：查询名字中有李四的并且年龄必须大于20的
            //BoolQueryBuilder must = boolQueryBuilder.should(QueryBuilders.matchQuery("name", "张三")).must(QueryBuilders.matchQuery("age", 18));
            //需要条件
            boolQueryBuilder.mustNot(QueryBuilders.termQuery(userField,""));
            SearchSourceBuilder query = new SearchSourceBuilder();
            query.query(boolQueryBuilder);
            searchSourceBuilder.trackTotalHits(true);
            TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
            agg.field("sip").size(1000);
            query.aggregation(agg);
            //query

            //创建一个SearchRequest，查询的请求对象
            SearchRequest request = new SearchRequest(INDEX).source(query);

            Response response = search(request.indices(), request.source().toString());
            String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
//					log.info("*****  responseStr  ******\n" + responseStr + "***********\n");
            ObjectMapper mapper = new ObjectMapper();
            aggMap = mapper.readValue(responseStr, Map.class);

            List<Map<String, Object>> result = new ArrayList<>();
            if (aggMap != null && aggMap.containsKey("aggregations")) {
                Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
                if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                    Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                    if (bucketsMap.containsKey("buckets")) {
                        List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                        aggItems.forEach(aggItem -> {
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("sip", aggItem.get("key"));
                            tmp.put("count", aggItem.get("doc_count"));
                            result.add(tmp);
                        });
                    }
                }
            }
            for(Map<String, Object> map : result){
                long userSize = 0;
                String userId = map.get("sip") != null ? map.get("sip").toString() : "";
                exec.execute(new ItemRunable4Flink(userId,client));
            }
            exec.shutdown();
            while (true) {
                if (exec.isTerminated()) {
                    break;
                }
            }
            System.out.println("###################SearchTest end###################");
        }catch (Exception e){
            e.printStackTrace();
        }
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

        }

        return null;

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
        if (StringUtils.isNotEmpty(USER) && StringUtils.isNotEmpty(PASSWORD)) {
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
}
