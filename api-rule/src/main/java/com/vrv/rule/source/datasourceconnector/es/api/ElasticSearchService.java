package com.vrv.rule.source.datasourceconnector.es.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.source.datasourceconnector.es.util.ElasticSearchException;
import com.vrv.rule.source.datasourceconnector.es.util.ElasticSearchUtil;
import com.vrv.rule.source.datasourceconnector.es.util.QueryCondition_ES;
import com.vrv.rule.source.datasourceconnector.es.vo.ElasticSearchVO;
import com.vrv.rule.source.datasourceconnector.es.vo.ScrollMapVO;
import com.vrv.rule.source.datasourceconnector.es.vo.SearchVO;
import com.vrv.rule.source.datasourceconnector.es.vo.SortVO;
import com.vrv.rule.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticSearchService {

    private static Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);


    public static ScrollMapVO findAll(RestHighLevelClient client, String[] indexs, SortVO sortVO){
        List<QueryCondition_ES> conditions = sortVO.getConditions();
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
        String order = sortVO.getOrder();
        String key = sortVO.getKey();

        SortBuilder sortBuilder = null;
        if (StringUtils.isNoneEmpty(key) && StringUtils.isNoneEmpty(order)) {
            if ("asc".equalsIgnoreCase(order)) {
                sortBuilder = SortBuilders.fieldSort(key).order(SortOrder.ASC);
            } else {
                sortBuilder = SortBuilders.fieldSort(key).order(SortOrder.DESC);
            }
        }

        try{
            Long time = sortVO.getTime();
            Integer size = sortVO.getSize();
            SearchVO searchVO = SearchVO.builder().sortBuilder(sortBuilder).queryBuilder(queryBuilder)
                    .index(indexs).time(time).size(size).build();
            SearchResponse searchResponse = ElasticSearchClient.getDocByScrollId(client, searchVO);
            ScrollMapVO scrollVO = getScrollResultList(searchResponse);
            return scrollVO;
        }catch (ElasticSearchException e){
            logger.error("游标分页查询异常：{}",e);
            ScrollMapVO scrollVO = new ScrollMapVO();
            scrollVO.setScrollId(null);
            scrollVO.setList(new ArrayList<>());
            scrollVO.setTotal(0L);
            return scrollVO;
        }

    }

    public static Boolean cleanScrollId(String scrollId,RestHighLevelClient client) {
        boolean clearScroll = ElasticSearchClient.clearScroll(client,scrollId);
        return clearScroll;
    }


    public static ScrollMapVO searchByScrollId(RestHighLevelClient client,String scrollId) {
        SearchResponse searchResponse = ElasticSearchClient.searchByScrollId(client,scrollId);
        ScrollMapVO scrollMapVO = getScrollResultList(searchResponse);
        return scrollMapVO;
    }



    private static Gson getGson(){
        Gson gson = new GsonBuilder()
                .setDateFormat(DateUtil.DEFAULT_DATE_PATTERN)
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create();
        return gson;
    }

    private static ScrollMapVO getScrollResultList(SearchResponse searchResponse) {
        ScrollMapVO scroll = new ScrollMapVO();
        Gson gson = getGson();
        String result = searchResponse.toString();
        ElasticSearchVO<Map<String, Object>> elasticSearchVO = gson.fromJson(result, new TypeToken<ElasticSearchVO<Map<String, Object>>>() {
        }.getType());
        List<Map<String, Object>> list = elasticSearchVO.getList();
        transferMathToLong(list);
        String _scroll_id = elasticSearchVO.get_scroll_id();
        long total = elasticSearchVO.getHits().getTotal().getValue();
        scroll.setList(list);
        scroll.setScrollId(_scroll_id);
        scroll.setTotal(total);
        return scroll;
    }

    private static void transferMathToLong(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Double) {
                    map.put(key, new BigDecimal(value.toString()).longValue());
                }
            }
        }
    }


}
