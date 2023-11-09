package com.vrv.rule.source.datasourceconnector.es;

import com.vrv.rule.source.datasourceconnector.es.api.ElasticSearchService;
import com.vrv.rule.source.datasourceconnector.es.util.QueryCondition_ES;
import com.vrv.rule.source.datasourceconnector.es.vo.ScrollMapVO;
import com.vrv.rule.source.datasourceconnector.es.vo.SortVO;
import com.vrv.rule.source.datasourceparam.impl.EsDatasourceParam;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.types.Row;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * es
 */
public class ElasticSearchSource extends RichSourceFunction<Row> {


   private static Logger logger = LoggerFactory.getLogger(ElasticSearchSource.class);

    public static int MAX_CONN_PER_ROUTE = 10;
    public static int MAX_CONN_TOTAL = 30;

    private volatile boolean isRunning = true;

    private RestHighLevelClient client;

   private EsDatasourceParam esDatasourceParam;

   public ElasticSearchSource(EsDatasourceParam esDatasourceParam){
       this.esDatasourceParam = esDatasourceParam;
   }



    private HttpHost[] getHttpHost() {
        String hostArrays = esDatasourceParam.getHostArrays();
        String[] hostArray = hostArrays.split(",");
        HttpHost[] httpHosts = new HttpHost[hostArray.length];
        for (int i = 0; i < hostArray.length; i++) {
            httpHosts[i] = new HttpHost(hostArray[i].split(":")[0], Integer.parseInt(hostArray[i].split(":")[1]), "http");
        }
        return httpHosts;
    }


    private RestClientBuilder contructBuilder() {
        String userName = esDatasourceParam.getUserName();
        String password = esDatasourceParam.getPassword();
        HttpHost[] httpHost = getHttpHost();
        RestClientBuilder builder = RestClient.builder(httpHost);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(MAX_CONN_TOTAL);
            httpClientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE);
            httpClientBuilder.disableAuthCaching();
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            return httpClientBuilder;
        });
        return builder;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        RestClientBuilder builder = contructBuilder();
        client = new RestHighLevelClient(builder);
    }

    @Override
    public void run(SourceContext<Row> sourceContext) throws Exception {
        String indexName = esDatasourceParam.getIndexName();
        String[] indexNames = new String[]{indexName};
        SortVO sortVO = getSortVO();
        List<FieldInfoVO> fieldInfoVOs = esDatasourceParam.getFieldInfoVOs();
        addSourceContextByScroll(sourceContext, indexNames, sortVO, fieldInfoVOs);
    }


    private void getFirstScrollList(SourceContext<Row> sourceContext, ScrollMapVO scrollMapVO,List<FieldInfoVO> fieldInfoVOs) {
        scrollMapVO.getList().forEach(map -> {
            Row row = new Row(fieldInfoVOs.size());
            for (int i = 0; i < fieldInfoVOs.size(); i++) {
                Object s = map.get(fieldInfoVOs.get(i).getFieldName());
                s = getObjectByLong(fieldInfoVOs, i, i, s);

                row.setField(i, s);
            }
            try {
                sourceContext.collect(row);
            }catch (Exception e){
                logger.error("报错信息：{},row:{}",e.getMessage(),row.toString());
            }
        });
    }


    /**
     * 通过游标读取对应es的数据
     * @param sourceContext
     * @param indexNames
     * @param sortVO
     * @param fieldInfoVOs
     */
    private void addSourceContextByScroll(SourceContext<Row> sourceContext, String[] indexNames, SortVO sortVO, List<FieldInfoVO> fieldInfoVOs) {
        ScrollMapVO scrollMapVO = ElasticSearchService.findAll(client, indexNames, sortVO);
        long total = scrollMapVO.getTotal();
        if(total > 0){
            getFirstScrollList(sourceContext, scrollMapVO,fieldInfoVOs);
            String scrollId = scrollMapVO.getScrollId();
            while (isRunning) {
                ScrollMapVO searchByScrollId = ElasticSearchService.searchByScrollId(client, scrollId);
                List<Map<String, Object>> list = searchByScrollId.getList();
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> element = list.get(i);
                        Row row = new Row(fieldInfoVOs.size());
                        for (int j = 0; j < fieldInfoVOs.size(); j++) {
                            String fieldName = fieldInfoVOs.get(j).getFieldName();
                            Object s = element.getOrDefault(fieldName, null);
                            s = getObjectByLong(fieldInfoVOs, i, j, s);
                            row.setField(j, s);
                        }
                        try {
                            sourceContext.collect(row);
                        }catch (Exception e){
                            logger.error("报错信息：{},row:{}",e.getMessage(),row.toString());
                        }
                    }
                } else {
                    logger.info("数据分页已到结尾结束!scrollId:{}", scrollId);
                    ElasticSearchService.cleanScrollId(scrollId, client);
                    isRunning = false;
                }
            }
        }else {
            logger.info("es索引：{}中没有数据",indexNames);
        }
    }

    private static Object getObjectByLong(List<FieldInfoVO> fieldInfoVOs, int i, int j, Object s) {
        if(s instanceof Long && fieldInfoVOs.get(j).getFieldType().equals("int")){
            try {
                s = Math.toIntExact((Long) s);
            }catch (Exception e){
                logger.error("s:{},字段名称:{}", s, fieldInfoVOs.get(i).getFieldName());
            }
        }
        return s;
    }

    private SortVO getSortVO() {
        List<QueryCondition_ES> conditions = esDatasourceParam.getConditions();
        if(conditions==null){
            conditions = new ArrayList<>();
        }
        Long time = esDatasourceParam.getTime();
        Integer size = esDatasourceParam.getSize();
        String sort = esDatasourceParam.getSort();
        String key = esDatasourceParam.getKey();
        SortVO sortVO = SortVO.builder().conditions(conditions).time(time).size(size).build();
        if(sort!=null && key!=null){
            sortVO.setKey(key);
            sortVO.setOrder(sort);
        }
        return sortVO;
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
