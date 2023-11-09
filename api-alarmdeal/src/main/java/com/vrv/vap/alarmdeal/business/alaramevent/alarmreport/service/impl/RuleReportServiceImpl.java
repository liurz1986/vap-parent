package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventRuleTotalResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.RuleTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.RuleReportService;
import com.vrv.vap.alarmdeal.business.analysis.vo.RuleStartedStatisticsData;
import com.vrv.vap.alarmdeal.business.asset.enums.AssetTrypeGroupEnum;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleReportServiceImpl implements RuleReportService {
    @Autowired
    private EventCategoryService eventCategoryService;
    @Autowired
    private AlarmAnalysisService alarmAnalysisService;
    @Override
    public EventRuleTotalResponse queryTotal(EventRuleTotalResponse eventRuleTotalResponse) {

        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
        conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));//不包含第三级
        List<EventCategory> categorys = eventCategoryService.findAll(conditions);
        List<Map<String, Object>> eventRuleStartedStatistics = alarmAnalysisService.getEventRuleStartedStatistics();
        Integer total=0;
        for (EventCategory category : categorys) {
            Map<String,Integer> map=getRuleTotal(category,eventRuleStartedStatistics);
            total=total+map.get("total");
            switch(category.getTitle()){
                case "网络安全异常":
                    eventRuleTotalResponse.setNetworkNum(map.get("total"));
                    break;
                case "互联互通异常" :
                    eventRuleTotalResponse.setConnectivityNum(map.get("total"));
                    break;
                case "应用异常" :
                    eventRuleTotalResponse.setApplicationNum(map.get("total"));
                    break;
                case "用户行为异常" :
                    eventRuleTotalResponse.setUserNum(map.get("total"));
                    break;
                case "运维行为异常" :
                    eventRuleTotalResponse.setOperationaNum(map.get("total"));
                    break;
                case "配置合规性" :
                    eventRuleTotalResponse.setConfigurationNum(map.get("total"));
                    break;
                default:
                    break;
            }
        }
        eventRuleTotalResponse.setRuleTotal(total);
        return eventRuleTotalResponse;
    }

    @Override
    public List<EventTypeResponse> isStarted() {
        List<EventTypeResponse> eventTypeResponses=new ArrayList<>();
        List<Map<String, Object>> eventRuleStartedStatistics = alarmAnalysisService.getEventRuleStartedStatistics();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
        conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));//不包含第三级
        List<EventCategory> categorys = eventCategoryService.findAll(conditions);
        for (EventCategory category:categorys){
            EventTypeResponse eventTypeResponse=new EventTypeResponse();
            eventTypeResponse.setIndexName(category.getTitle());
            Map<String,Integer> map=getRuleTotal(category,eventRuleStartedStatistics);
            eventTypeResponse.setIndexCount(map.get("openCount"));
            eventTypeResponses.add(eventTypeResponse);
        }
        return eventTypeResponses;
    }

    @Override
    public List<RuleTypeResponse> statistics() {
        List<RuleTypeResponse> ruleTypeResponses=new ArrayList<>();
        List<Map<String, Object>> eventRuleStartedStatistics = alarmAnalysisService.getEventRuleStartedStatistics();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
        conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));//不包含第三级
        List<EventCategory> categorys = eventCategoryService.findAll(conditions);
        Integer total=0;
        Integer openCount=0;
        Integer closeCount=0;
        for (EventCategory category:categorys){
            Map<String,Integer> map=getRuleTotal(category,eventRuleStartedStatistics);
            RuleTypeResponse ruleTypeResponse=new RuleTypeResponse();
            ruleTypeResponse.setName(category.getTitle());
            ruleTypeResponse.setTotal(map.get("total"));
            ruleTypeResponse.setIsStartedCount(map.get("openCount"));
            ruleTypeResponse.setNotStartedCount(map.get("closeCount"));
            total+=map.get("total");
            openCount+=map.get("openCount");
            closeCount+=map.get("closeCount");
            ruleTypeResponses.add(ruleTypeResponse);
        }
        RuleTypeResponse ruleTypeResponse=new RuleTypeResponse();
        ruleTypeResponse.setName("总计");
        ruleTypeResponse.setTotal(total);
        ruleTypeResponse.setIsStartedCount(openCount);
        ruleTypeResponse.setNotStartedCount(closeCount);
        ruleTypeResponses.add(ruleTypeResponse);
        return ruleTypeResponses;
    }

    private Map<String, Integer> getRuleTotal(EventCategory category, List<Map<String, Object>> eventRuleStartedStatistics) {
        Map<String,Integer> integerMap=new HashMap<>();
        int openCount = 0;
        int total = 0;
        for (Map<String, Object> map : eventRuleStartedStatistics) {
            if (map.containsKey("eventRuleParentId") && category.getId().equals(map.get("eventRuleParentId").toString())) {
                total++;
                if (map.containsKey("isStarted") && "1".equals(map.get("isStarted").toString())) {
                    openCount++;
                }
            }
        }
        integerMap.put("total",total);
        integerMap.put("openCount",openCount);
        integerMap.put("closeCount",total-openCount);
        return integerMap;
    }

}
