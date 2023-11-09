package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AlarmEventAttributeService;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询告警信息
 * 告警名称为：非领导IP登录领导账号
 * 2023-3-23
 *  @author liurz
 */
@Service
public class AlarmEventAttributeServiceImpl implements AlarmEventAttributeService {

    public static final String WARN_RESULT_TMP = "alarmeventmanagement";

    private String alarmName="冒用应用系统用户帐号";
    @Autowired
    private ElasticSearchRestClientService elasticSearchRestService;

    // 查询告警的数量：默认是50条
    @Value("${alarm.query.maxcount:50}")
    private int alarmMaxCount;

    @Override
    public String getIndexName() {
        return WARN_RESULT_TMP;
    }

    /**
     * 默认取50条告警数据
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<AlarmEventAttribute> getPageQueryResult(String startTime, String endTime) {
        List<QueryCondition_ES> conditions = new ArrayList<QueryCondition_ES>();
        conditions.add(QueryCondition_ES.eq("eventName",alarmName));
        conditions.add(QueryCondition_ES.between("eventCreattime",startTime,endTime));
        PageReq_ES pageQuery = new PageReq_ES();
        pageQuery.setCount_(alarmMaxCount);
        pageQuery.setStart_(0);
        pageQuery.setOrder_("eventCreattime");
        pageQuery.setBy_("desc");
        PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, conditions);
       return findByPage.getList();
    }

}
