package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.WebLoginAuditEsService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.WebLoginAudit;
import com.vrv.vap.es.service.ElasticSearchMapManage;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询日志信息
 *
 * 2023-03-23
 * @author liurz
 */
@Service
public  class WebLoginAuditEsServiceImpl implements WebLoginAuditEsService {
    @Autowired
    private ElasticSearchMapManage elasticSearchMapManage;
    // 日志的索引名称
    public static final String WEBLOGIN_AUDIT = "weblogin-audit-*";


    @Override
    public List<WebLoginAudit> findAll(List<String> guids, String reportDevType){
        List<WebLoginAudit> results = new ArrayList<>();
        List<QueryCondition_ES> conditions = new ArrayList<QueryCondition_ES>();
        conditions.add(QueryCondition_ES.in("guid",guids));
        conditions.add(QueryCondition_ES.eq("report_dev_type",reportDevType));
        List<Map<String,Object>> datas =elasticSearchMapManage.findAll(WEBLOGIN_AUDIT,conditions);
        WebLoginAudit audit =null;
        for(Map<String,Object> map: datas){
            audit = new WebLoginAudit();
            audit.setGuid(map.get("guid")==null?"":String.valueOf(map.get("guid")));
            audit.setUserName(map.get("username")==null?"":String.valueOf(map.get("username")));
            audit.setReportDevType(map.get("report_dev_type")==null?"":String.valueOf(map.get("report_dev_type")));
            results.add(audit);
        }
        return results;
    }
}