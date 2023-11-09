package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.AlarmRiskLevelEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.AlarmDistributionRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.AlarmEventRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.AlarmEventRankRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.EventLevelTotalRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.EventTrendRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.ViolationPersonRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AlarmScreenService;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 梁国露
 * @since: 2022/9/13 10:33
 * @description:
 */
@Service
public class AlarmScreenServiceImpl implements AlarmScreenService {

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForEsService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 获取资产告警分布
     * @param req
     * @return
     */
    @Override
    public List<AlarmDistributionRes> getAssetAlarmDistribution(RequestBean req) {
        List<AlarmDistributionRes> result = new ArrayList<>();
        // 通过设备IP进行分组
        List<QueryCondition_ES> contidions = new ArrayList<>();
        contidions.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        contidions.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));
        Map<String,Long> alarmMap = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(), "principalIp",contidions);

        // 计算高危数
        List<QueryCondition_ES> highContidions = new ArrayList<>();
        highContidions.add(QueryCondition_ES.in("alarmRiskLevel",new String[]{"4","5"}));
        highContidions.addAll(contidions);
        Map<String,Long> highAlarmMap = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(), "principalIp",highContidions);
        // 计算中危数
        List<QueryCondition_ES> mediumContidions = new ArrayList<>();
        mediumContidions.add(QueryCondition_ES.in("alarmRiskLevel",new String[]{"3"}));
        mediumContidions.addAll(contidions);
        Map<String,Long> mediumAlarmMap = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(), "principalIp",mediumContidions);
        // 计算低危数
        List<QueryCondition_ES> lowContidions = new ArrayList<>();
        lowContidions.add(QueryCondition_ES.in("alarmRiskLevel",new String[]{"1","2"}));
        lowContidions.addAll(contidions);
        Map<String,Long> lowAlarmMap = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(), "principalIp",lowContidions);

        // 处理结果数据
        alarmMap.forEach((ip,alarmCount)->{
            AlarmDistributionRes res = new AlarmDistributionRes();
            res.setIp(ip);
            res.setAlarmCount(alarmCount);

            // 设置高危数
            res.setHighCount(highAlarmMap.get(ip) ==  null ?0:highAlarmMap.get(ip));

            // 设置中危
            res.setMediumCount(mediumAlarmMap.get(ip) ==  null ?0:mediumAlarmMap.get(ip));

            // 设置低危
            res.setLowCount(lowAlarmMap.get(ip) ==  null ?0:lowAlarmMap.get(ip));
            result.add(res);
        });
        // 根据告警总数排序，取前5
        if(CollectionUtils.isNotEmpty(result)){
            Collections.sort(result, Comparator.comparing(AlarmDistributionRes::getAlarmCount).reversed());
            List<AlarmDistributionRes> resultNew = result.stream().limit(5).collect(Collectors.toList());
            return resultNew;
        }
        return result;
    }

    /**
     * 获取资产告警事件
     * @param req
     * @return
     */
    @Override
    public List<AlarmEventRes> getAssetAlarmEvent(RequestBean req) {
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        querys.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        List<AlarmEventRes> result = queryEsForAgg(alarmEventManagementForEsService.getIndexName(),"eventName",queryBuilder);
        if(CollectionUtils.isNotEmpty(result)){
            // 根据告警总数排序，取前5
            Collections.sort(result, Comparator.comparing(AlarmEventRes::getAlarmCount).reversed());
            List<AlarmEventRes> resultNew = result.stream().limit(5).collect(Collectors.toList());
            return resultNew;
        }
        return result;
    }

    /**
     * 违规人员统计top5
     * @param req
     * @param top
     * @return
     */
    @Override
    public List<ViolationPersonRes> getViolationPersonTotal(RequestBean req, Integer top) {
        List<ViolationPersonRes> result = new ArrayList<>();
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        querys.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));
        // 查询数据
        Map<String,Long> map = queryPersonData(querys);

        // 构建对象
        map.forEach((key,value)->{
            String[] keyArr = key.split("@#@#@");
            ViolationPersonRes res = new ViolationPersonRes();
            res.setStaffName(keyArr[0]);
            res.setStaffDepartment(keyArr[1]);
            res.setCount(value);
            result.add(res);
        });

        // 取前五
        Collections.sort(result, Comparator.comparing(ViolationPersonRes::getCount).reversed());
        List<ViolationPersonRes> result1 = result.stream().limit(top).collect(Collectors.toList());
        return result1;
    }

    /**
     * 威胁事件排名top5
     * @param req
     * @param top
     * @return
     */
    @Override
    public List<AlarmEventRankRes> getAlarmEventRank(RequestBean req, Integer top) {
        List<AlarmEventRankRes> result = new ArrayList<>();
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        querys.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));
        // 查询数据
        Map<String,Long> map = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(),"eventName",querys);

        // 构建对象
        map.forEach((key,value)->{
            AlarmEventRankRes res = new AlarmEventRankRes();
            res.setEventName(key);
            res.setAlarmCount(value);
            result.add(res);
        });

        // 取前五
        Collections.sort(result, Comparator.comparing(AlarmEventRankRes::getAlarmCount).reversed());
        List<AlarmEventRankRes> result1 = result.stream().limit(top).collect(Collectors.toList());
        return result1;
    }

    /**
     * 违规行为
     * @param req
     * @return
     */
    @Override
    public List<String> getViolations(RequestBean req) {
        List<String> result = new ArrayList<>();
        List<AlarmEventRankRes> list = getAlarmEventRank(req, 10);
        if(CollectionUtils.isNotEmpty(list)){
            List<String> eventNames = list.stream().map(AlarmEventRankRes::getEventName).collect(Collectors.toList());
            result.addAll(eventNames);
        }
        return result;
    }

    /**
     * 告警统计
     * @param req
     * @return
     */
    @Override
    public EventLevelTotalRes getAlarmEventLevelTotal(RequestBean req) {
        EventLevelTotalRes result = new EventLevelTotalRes();
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        querys.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));

        // 紧急告警数
        List<QueryCondition_ES> urgentConditions = new ArrayList<>();
        urgentConditions.addAll(querys);
        urgentConditions.add(QueryCondition_ES.eq("alarmRiskLevel", AlarmRiskLevelEnum.FIVE.getCode()));
        long urgentCount = alarmEventManagementForEsService.count(urgentConditions);
        result.setUrgentCount(urgentCount);

        // 严重事件总数
        List<QueryCondition_ES> seriousConditions = new ArrayList<>();
        seriousConditions.addAll(querys);
        seriousConditions.add(QueryCondition_ES.eq("alarmRiskLevel", AlarmRiskLevelEnum.FOUR.getCode()));
        long seriousCount = alarmEventManagementForEsService.count(urgentConditions);
        result.setSeriousCount(seriousCount);

        // 重要事件总数
        List<QueryCondition_ES> majorConditions = new ArrayList<>();
        majorConditions.addAll(querys);
        majorConditions.add(QueryCondition_ES.eq("alarmRiskLevel", AlarmRiskLevelEnum.THREE.getCode()));
        long majorCount = alarmEventManagementForEsService.count(majorConditions);
        result.setMajorCount(majorCount);

        // 一般事件总数
        List<QueryCondition_ES> commonlyConditions = new ArrayList<>();
        commonlyConditions.addAll(querys);
        commonlyConditions.add(QueryCondition_ES.eq("alarmRiskLevel", AlarmRiskLevelEnum.TWO.getCode()));
        long commonlyCount = alarmEventManagementForEsService.count(commonlyConditions);
        result.setCommonlyCount(commonlyCount);

        // 已处置事件总数
        List<QueryCondition_ES> disposedConditions = new ArrayList<>();
        disposedConditions.addAll(querys);
        disposedConditions.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        long disposedCount = alarmEventManagementForEsService.count(disposedConditions);
        result.setDisposedCount(disposedCount);

        // 已处置事件总数
        List<QueryCondition_ES> notDisposedConditions = new ArrayList<>();
        notDisposedConditions.addAll(querys);
        notDisposedConditions.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.UNTREATED.getCode()));
        long notDisposedCount = alarmEventManagementForEsService.count(notDisposedConditions);
        result.setNotDisposedCount(notDisposedCount);

        // 进入新增
        List<QueryCondition_ES> todayConditions = new ArrayList<>();
        Date now = new Date();
        querys.add(QueryCondition_ES.gt("eventCreattime", DateUtil.format(now,"yyyy-MM-dd")+" 00:00:00"));
        querys.add(QueryCondition_ES.le("eventCreattime",DateUtil.format(now,"yyyy-MM-dd")+" 23:59:59"));
        long todayCount = alarmEventManagementForEsService.count(todayConditions);
        result.setTodayCount(todayCount);
        return result;
    }

    /**
     * 安全事件趋势
     * @param req
     * @return
     */
    @Override
    public List<EventTrendRes> getAlarmEventTrend(RequestBean req) {
        List<EventTrendRes> result = new ArrayList<>();
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        querys.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));
        // 查询数据
        Map<String,Long> map = alarmEventManagementForEsService.queryStatisticsByTime(alarmEventManagementForEsService.getIndexName(), querys,"eventCreattime", DateHistogramInterval.DAY,"yyyy-MM-dd");

        // 填补数据
        List<Map<String,Object>> list = new ArrayList<>();
        map.forEach((key,value)->{
            Map<String,Object> newMap = new HashMap<>();
            newMap.put("triggerTime",key);
            newMap.put("doc_count",value);
            list.add(newMap);
        });
        long interval = (long)24*60*60*1000;
        try {
            SocUtil.getTimeFullMap(DateUtil.parseDate(req.getStartTime(),DateUtil.DEFAULT_DATE_PATTERN), DateUtil.parseDate(req.getEndTime(),DateUtil.DEFAULT_DATE_PATTERN),interval,"yyyy-MM-dd",list);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 转换数据
        list.stream().forEach(item->{
            EventTrendRes res = new EventTrendRes();
            item.forEach((key,value)->{
                if("triggerTime".equals(key)){
                    res.setEventTime(String.valueOf(value));
                }else if("doc_count".equals(key)){
                    res.setEventCount(Long.valueOf(String.valueOf(value)));
                }
            });
            result.add(res);
        });
        return result;
    }

    @Override
    public Integer getAssetCountByAlarm(RequestBean req) {
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.gt("eventCreattime",req.getStartTime()));
        querys.add(QueryCondition_ES.le("eventCreattime",req.getEndTime()));
        Map<String,Long> ipMap = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(),"principalIp",querys);
        return ipMap.size();
    }

    private Map<String,Long> queryPersonData(List<QueryCondition_ES> querys){
        querys.add(QueryCondition_ES.notNull("relatedStaffInfos"));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        Map<String, Long> result = new HashMap<>();
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field("relatedStaffInfos.staffName");
        Script queryScript = AlarmDealUtil.initScript(new String[]{"relatedStaffInfos.staffName","relatedStaffInfos.staffDepartment"});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(10);
        groupByFieldAgg.subAggregation(fieldCounts);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(alarmEventManagementForEsService.getIndexName());
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    String id = terms.getBuckets().get(i).getKey().toString();
                    Long sum = terms.getBuckets().get(i).getDocCount();
                    result.put(id, sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 分组统计
     * @param indexName
     * @param fieldName
     * @param queryBuilder
     */
    private List<AlarmEventRes> queryEsForAgg(String indexName, String fieldName, QueryBuilder queryBuilder) {
        List<AlarmEventRes> result = new ArrayList<>();
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field(fieldName);
        AggregationBuilder maxEventTime = AggregationBuilders.max("eventTime")
                .field("eventCreattime");
        Script queryScript = AlarmDealUtil.initScript(new String[]{fieldName,"alarmRiskLevel"});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(10);
        groupByFieldAgg.subAggregation(fieldCounts);
        groupByFieldAgg.subAggregation(maxEventTime);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    AlarmEventRes res = new AlarmEventRes();
                    String eventName = terms.getBuckets().get(i).getKey().toString();
                    Long sum = terms.getBuckets().get(i).getDocCount();
                    String[] eventNamtArr = eventName.split("@#@#@");
                    res.setEventName(eventNamtArr[0]);
                    res.setAlarmCount(sum);
                    String level = getAlarmLevelStr(eventNamtArr[1]);
                    res.setAlarmRiskLevel(level);
                    Aggregation childtermsLevel = terms.getBuckets().get(i).getAggregations().get("eventTime");
                    String time = ((org.elasticsearch.search.aggregations.metrics.ParsedMax)childtermsLevel).getValueAsString();
                    res.setUpdateTime(time);
                    result.add(res);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public String getAlarmLevelStr(String level){
        if("1".equals(level) || "2".equals(level)){
            return "低危";
        }else if("3".equals(level)){
            return "中危";
        }else{
            return "高危";
        }
    };
}
