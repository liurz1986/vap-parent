package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.StaticData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.StaticsList;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEvent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEventData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEventList;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.ThreeInOneService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util.BusinessUtil;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util.BusinessUtil.getDev;

/**
 * @author wudi
 * @date 2022/4/20 17:03
 */
@Service
public class ThreeInOneServiceImpl implements ThreeInOneService {

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;

    @Override
    public ThreeInOneEvent searchThreeInOneEvent(RequestBean requestBean) {
        ThreeInOneEvent threeInOneEvent = new ThreeInOneEvent();

        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = requestBean.getStartTime();
        String endTime = requestBean.getEndTime();
        if(StringUtils.isNotEmpty(requestBean.getStartTime()) && StringUtils.isNotEmpty(requestBean.getEndTime())){
            conditions.add(QueryCondition_ES.between("eventCreattime",startTime,endTime));
        }
        conditions.add(QueryCondition_ES.like("tag","三合一"));
        List<AlarmEventAttribute> all = alarmEventManagementForESService.findAll(conditions);
        List<ThreeInOneEventList> list = getThreeInOneList(all);
        ThreeInOneEventData data = getStaticData(all);
        threeInOneEvent.setData(data);
        threeInOneEvent.setList(list);
        return threeInOneEvent;
    }

    @Override
    public ThreeInOneEvent searchThreeInOneChange(RequestBean requestBean) {
        ThreeInOneEvent threeInOneEvent = new ThreeInOneEvent();
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = requestBean.getStartTime();
        String endTime = requestBean.getEndTime();
        if(StringUtils.isNotEmpty(requestBean.getStartTime()) && StringUtils.isNotEmpty(requestBean.getEndTime())){
            conditions.add(QueryCondition_ES.between("eventCreattime",startTime,endTime));
        }
        conditions.add(QueryCondition_ES.like("ruleId","f3f95ce0bcbf4e1395191d121b3217cf"));
        List<AlarmEventAttribute> all = alarmEventManagementForESService.findAll(conditions);
        List<ThreeInOneEventList> list = getThreeInOneChangeList(all);
        ThreeInOneEventData data = getStaticData(all);
        threeInOneEvent.setData(data);
        threeInOneEvent.setList(list);
        return threeInOneEvent;
    }


    /**
     * 构造主机审计违规数据数据
     * @param all
     * @return
     */
    private List<ThreeInOneEventList> getThreeInOneList(List<AlarmEventAttribute> all) {
        List<ThreeInOneEventList> list = new ArrayList<>();
        for (AlarmEventAttribute alarmEventAttribute: all) {
            ThreeInOneEventList statics = new ThreeInOneEventList();
            statics.setTime(DateUtil.format(alarmEventAttribute.getEventCreattime()));
            statics.setRemarks(alarmEventAttribute.getRuleName());
            String dev = BusinessUtil.getDev(alarmEventAttribute);
            statics.setIp(dev);
            String org = BusinessUtil.getOrg(alarmEventAttribute);
            statics.setOrg(org);
            list.add(statics);
        }
        return list;
    }


    private List<ThreeInOneEventList> getThreeInOneChangeList(List<AlarmEventAttribute> all) {
        List<ThreeInOneEventList> list = new ArrayList<>();
        for (AlarmEventAttribute alarmEventAttribute: all) {
            ThreeInOneEventList statics = new ThreeInOneEventList();
            statics.setTime(DateUtil.format(alarmEventAttribute.getEventCreattime()));
            statics.setRemarks(alarmEventAttribute.getCauseAnalysis());
            String dev = BusinessUtil.getDev(alarmEventAttribute);
            statics.setIp(dev);
            String org = BusinessUtil.getOrg(alarmEventAttribute);
            statics.setOrg(org);
            list.add(statics);
        }
        return list;
    }



    /**
     * 主机审计策略变更-统计
     * @param all
     * @return
     */
    private ThreeInOneEventData getStaticData(List<AlarmEventAttribute> all) {
        ThreeInOneEventData staticsData = new ThreeInOneEventData();
        staticsData.setChangeNum(all.size());
        int count = BusinessUtil.getDevCount(all);
        staticsData.setDevNum(count);
        return staticsData;
    }


}
