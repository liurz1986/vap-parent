package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.DeviceInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.HostAuditService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util.BusinessUtil;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.ArrayUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 主机审计违规审计统计实现接口
 * @author wudi
 * @date 2022/4/20 14:23
 */
@Service
public class HostAuditServiceImpl implements HostAuditService {

    private static Logger logger = LoggerFactory.getLogger(HostAuditServiceImpl.class);

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForEsService;


    @Override
    public HostAuditEventResponse searchHostAuditEvent(RequestBean requestBean) {

        HostAuditEventResponse hostAuditEventResponse = new HostAuditEventResponse();
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = requestBean.getStartTime();
        String endTime = requestBean.getEndTime();
        if(StringUtils.isNotEmpty(requestBean.getStartTime()) && StringUtils.isNotEmpty(requestBean.getEndTime())){
            conditions.add(QueryCondition_ES.between("eventCreattime",startTime,endTime));
        }
        conditions.add(QueryCondition_ES.like("tag","主审"));
        List<AlarmEventAttribute> all = alarmEventManagementForEsService.findAll(conditions);

        List<StaticsList> hostAuditList = getHostAuditList(all);

        StaticData staticData = getStaticData(all);
        hostAuditEventResponse.setList(hostAuditList);
        hostAuditEventResponse.setData(staticData);
        return hostAuditEventResponse;
    }

    @Override
    public HostAuditTerminalStrategy searchHostAuditTerminalEvent(RequestBean requestBean) {

            HostAuditTerminalStrategy hostAuditTerminalStrategy = new HostAuditTerminalStrategy();
            List<QueryCondition_ES> conditions = new ArrayList<>();
            String startTime = requestBean.getStartTime();
            String endTime = requestBean.getEndTime();
            if(StringUtils.isNotEmpty(requestBean.getStartTime()) && StringUtils.isNotEmpty(requestBean.getEndTime())){
                conditions.add(QueryCondition_ES.between("eventCreattime",startTime,endTime));
            }
            conditions.add(QueryCondition_ES.eq("ruleId","e1d867cf90834766953d9d13b00f783c"));
            List<AlarmEventAttribute> all = alarmEventManagementForEsService.findAll(conditions);

            List<TerminalList> terminalListsList = getTerminalStrategeList(all);

            TerminalData terminalData = getTerminalData(all);
            hostAuditTerminalStrategy.setList(terminalListsList);
            hostAuditTerminalStrategy.setData(terminalData);
            return hostAuditTerminalStrategy;

    }

    /**
     * 主机审计策略变更-集合
     * @param all
     * @return
     */
    private List<TerminalList> getTerminalStrategeList(List<AlarmEventAttribute> all) {
        List<TerminalList> list = new ArrayList<>();
        for (AlarmEventAttribute alarmEventAttribute: all) {
            TerminalList statics = new TerminalList();
            statics.setTime(DateUtil.format(alarmEventAttribute.getEventCreattime()));
            statics.setType(alarmEventAttribute.getRuleName());
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
    private StaticData getStaticData(List<AlarmEventAttribute> all) {
        StaticData staticsData = new StaticData();
        staticsData.setHappenNum(all.size());
        int count = BusinessUtil.getDevCount(all);
        staticsData.setDevNum(count);
        return staticsData;
    }

    /**
     * 终端统计数据
     * @param all
     * @return
     */
    private TerminalData getTerminalData(List<AlarmEventAttribute> all) {
        TerminalData terminalData = new TerminalData();
        terminalData.setChangeNum(all.size());
        int count = BusinessUtil.getDevCount(all);
        terminalData.setDevNum(count);
        return terminalData;
    }




    /**
     * 构造主机审计违规数据数据
     * @param all
     * @return
     */
    private List<StaticsList> getHostAuditList(List<AlarmEventAttribute> all) {
        List<StaticsList> list = new ArrayList<>();
        for (AlarmEventAttribute alarmEventAttribute: all) {
             StaticsList statics = new StaticsList();
            statics.setTime(DateUtil.format(alarmEventAttribute.getEventCreattime()));
            statics.setType(alarmEventAttribute.getRuleName());
            String dev = BusinessUtil.getDev(alarmEventAttribute);
            statics.setDev(dev);
            String staff = getStaff(alarmEventAttribute);
            statics.setUser(staff);
            String org = BusinessUtil.getOrg(alarmEventAttribute);
            statics.setOrg(org);
            list.add(statics);
        }
        return list;
    }

    /**
     * 获得人员
     * @param alarmEventAttribute
     * @return
     */
    private String getStaff(AlarmEventAttribute alarmEventAttribute) {
        String staffsStr = null;
        List<String> staffs = new ArrayList<>();
        List<StaffInfo> staffInfos = alarmEventAttribute.getStaffInfos();
        if(staffInfos!=null){
            for (StaffInfo staffInfo:staffInfos) {
                staffs.add(staffInfo.getStaffName());
            }
            String[] staffArr = staffs.toArray(new String[staffInfos.size()]);
            staffsStr = ArrayUtil.join(staffArr,",");
        }
        return staffsStr;
    }






}
