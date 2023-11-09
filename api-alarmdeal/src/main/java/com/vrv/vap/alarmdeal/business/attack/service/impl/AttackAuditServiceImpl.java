package com.vrv.vap.alarmdeal.business.attack.service.impl;

import com.vrv.vap.alarmdeal.business.attack.service.AttackAuditService;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatManageService;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.NameValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

@Service
public class AttackAuditServiceImpl implements AttackAuditService {
    @Resource
    private ElasticSearchRestClientService attackAuditServiceEs;
    @Override
    public List<NameValue> getAttackStageCount(Map<String, String> threatReq) {
        List<NameValue> nameValues=new ArrayList<>();
        List<QueryCondition_ES> queryConditionEs=new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(threatReq.get("beginTime"))){
                String beginTime = threatReq.get("beginTime");
                Date date = DateUtil.parseDate(beginTime, DateUtil.DEFAULT_DATE_PATTERN);
                String format = DateUtil.format(date, DateUtil.UTC_TIME);
                queryConditionEs.add(QueryCondition_ES.ge("event_time",format));
            }
            if (StringUtils.isNotBlank(threatReq.get("endTime"))){
                String endTime = threatReq.get("endTime");
                Date date = DateUtil.parseDate(endTime, DateUtil.DEFAULT_DATE_PATTERN);
                String format = DateUtil.format(date, DateUtil.UTC_TIME);
                queryConditionEs.add(QueryCondition_ES.le("event_time",format));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        SearchField searchField=new SearchField("attack_stage", FieldType.String,0, 1000, null);
        List<Map<String, Object>> list=attackAuditServiceEs.queryStatistics(queryConditionEs,searchField);
        for (Map<String, Object> map:list){
            nameValues.add(new NameValue(map.get("doc_count").toString(),map.get("attack_stage").toString()));
        }
        return nameValues;
    }
}
