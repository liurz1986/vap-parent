package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;/**
 * @author liangguolu
 * @date 2021/11/12 18:58
 * @version 1.0
 */

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.ExecutorConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.HaveAlarmLogService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * 功能描述
 * @author liangguolu
 *
 * @date 2021年11月12日 18:58
 */
@Service
public class HaveAlarmLogServiceImpl implements HaveAlarmLogService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(HaveAlarmLogServiceImpl.class);

    // 告警索引名称
    private static final String ALARM_INDEX_NAME="alarmeventmanagement";

    @Autowired
    private ElasticSearchRestClient elasticSearchRestClient;

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

    @Override
    public List<SearchHit> queryAlarmDataForEs() {
        // 1、查询告警数据中的logs未补全数据
        List<QueryCondition_ES> conditions = new CopyOnWriteArrayList<>();
        conditions.add(QueryCondition_ES.like("logs.indexName","file"));
        conditions.add(QueryCondition_ES.isNull("logs.ids"));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
        SearchResponse searchResponse = elasticSearchRestClient.getDocs(new String[]{ALARM_INDEX_NAME},queryBuilder,null,null,0,1000);
//        SearchResponse searchResponse =null;
        // 没有查到数据则返回
        if(searchResponse == null){
            return new ArrayList<>();
        }

        // 2、获取数据
        SearchHit[] esResult = searchResponse.getHits().getHits();
        List<SearchHit> esResults = new CopyOnWriteArrayList<>(Arrays.asList(esResult));
        logger.debug("queryAlarmDataForES size={}",esResults.size());
        return esResults;
    }

    /**
     *
     * 补充告警日志数据
     *
     * @param esResults 告警初数据
     * @return list
     */
    @Override
    public List<AlarmEventAttribute> haveAlarmLogData(List<SearchHit> esResults, Map<String, List<EventTable>> eventTableMap) {
        List<AlarmEventAttribute> result = new ArrayList<>();
        for(SearchHit searchHit : esResults){
            String sourceAsString = searchHit.getSourceAsString();
            // 源数据转换
            AlarmEventAttribute doc = JSONObject.parseObject(sourceAsString,AlarmEventAttribute.class);
            List<LogIdVO> logs = doc.getLogs();
            if(CollectionUtils.isNotEmpty(logs)){
                // 不为空时
                for(LogIdVO logIdVO : logs){
                    EventTable eventTable = new EventTable();
                    List<EventTable> list = eventTableMap.get(logIdVO.getEventTableName());
                    if(CollectionUtils.isNotEmpty(list)){
                        eventTable.setId(list.get(0).getId());
                    }
                    eventTable.setName(logIdVO.getEventTableName());
                    if(logIdVO.getIds() !=null){
                        // 补充日志数据
                        alarmDataHandleService.haveLogData(logIdVO.getIds().toArray(new String[logIdVO.getIds().size()]),logIdVO.getIndexName(),eventTable,doc);
                    }
                }
            }
            result.add(doc);
        }
        return result;
    }

}
