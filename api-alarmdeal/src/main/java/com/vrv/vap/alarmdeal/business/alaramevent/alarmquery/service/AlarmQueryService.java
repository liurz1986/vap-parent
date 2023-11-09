package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmCountRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmQuery;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.AlarmQueryRespository;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmQueryVO;
import com.vrv.vap.common.model.User;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.PageReqVap;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;

@Service
public class AlarmQueryService extends BaseServiceImpl<AlarmQuery,String> {

   @Autowired
   private AlarmQueryRespository alarmQueryRespository;

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;

    @Autowired
    BusinessIntanceService businessIntanceService;


    @Override
    public  AlarmQueryRespository getRepository(){
        return alarmQueryRespository;
    }


    public PageRes<AlarmQuery> queryConditionPager(AlarmQueryVO alarmQueryVO){
        String conditionName=alarmQueryVO.getQueryName();
        User user= SessionUtil.getCurrentUser();
        List<QueryCondition> conditions = new ArrayList<QueryCondition>();
        if(StringUtils.isNotEmpty(conditionName)){
            conditions.add(QueryCondition.eq("queryName",conditionName));
        }
        if(user!=null){
            conditions.add(QueryCondition.eq("userId",user.getId()));
        }
        alarmQueryVO.setOrder_("createTime");
        Pageable pageable=PageReqVap.getPageable(alarmQueryVO);
        Page<AlarmQuery> page=findAll(conditions, pageable);
        PageRes<AlarmQuery> res = PageRes.toRes(page);
        return res;
    }

    public AlarmCountRes getAlarmCountRes(RequestBean req){
        AlarmCountRes res = new AlarmCountRes();
        String startTime = req.getStartTime();
        String endTime = req.getEndTime();

        // 获取告警总数
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.ge("eventCreattime", startTime));
        conditions.add(QueryCondition_ES.le("eventCreattime", endTime));

        long alarmCount =alarmEventManagementForEsService.count(conditions);
        res.setAlarmCount(alarmCount);

        // 获取工单数
        res.setFlowCount(0L);
        try {
            long flowCount = businessIntanceService.getAlarmCount(DateUtil.parseDate(startTime,DateUtil.DEFAULT_DATE_PATTERN),DateUtil.parseDate(endTime,DateUtil.DEFAULT_DATE_PATTERN));
            res.setFlowCount(flowCount);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 获取已处置状态告警数
        conditions.add(QueryCondition_ES.eq("alarmDealState",3));
        long dealCount =alarmEventManagementForEsService.count(conditions);
        res.setDealCount(dealCount);

        return res;
    }

}
