package com.vrv.rule.source.datasourceconnector.es.api;

import com.vrv.rule.source.datasourceconnector.es.vo.SearchVO;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchClient {

      private static Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class);

    /**
     * 分页查询封装
     * @param client
     * @param searchVO
     * @return
     */
    public static SearchResponse getDocByScrollId(RestHighLevelClient client, SearchVO searchVO) {
            SearchResponse searchResponse = null;

            SearchSourceBuilder builder = new SearchSourceBuilder();
            QueryBuilder queryBuilder = searchVO.getQueryBuilder();
            if (queryBuilder != null) {
                builder.query(queryBuilder);
            }
            SortBuilder sortBuilder = searchVO.getSortBuilder();
            if(sortBuilder != null){
                builder.sort(sortBuilder);
            }
            Integer size = searchVO.getSize();
            if (size > 0) {
                builder.size(size);
            } else {
                throw new RuntimeException("查找size小于0，请检查！");
            }

            Long time = searchVO.getTime();
            SearchRequest searchRequest = new SearchRequest(searchVO.getIndex());   //索引
            searchRequest.scroll(TimeValue.timeValueMinutes(time));
            searchRequest.source(builder);

            try {
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                logger.error("游标分页查询失败，请检查原因！", e);
            }
            return searchResponse;
        }


    public static boolean clearScroll(RestHighLevelClient client,String scrollId) {
        boolean result = false;
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        try {
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            if(clearScrollResponse.isSucceeded()) {
                result = true;
            }
        } catch (IOException e) {
            logger.error("清除Scroll游标报错",e);
        }
        return result;
    }



    public static SearchResponse searchByScrollId(RestHighLevelClient client,String scrollId) {
        SearchResponse searchResponse = null;
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
        searchScrollRequest.scrollId(scrollId);
        try {
            searchResponse = client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error("根据ScrollId获得对应的查询结果报错",e);
        }
        return searchResponse;
    }



}
