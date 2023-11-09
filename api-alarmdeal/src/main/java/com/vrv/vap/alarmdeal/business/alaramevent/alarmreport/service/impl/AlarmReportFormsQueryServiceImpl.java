package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AlarmEventAttributeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AlarmReportFormsQueryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.WebLoginAuditEsService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AlarmUniteResultVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AlarmUniteSearchVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.WebLoginAudit;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询告警关联信息
 * 2023-3-23
 *  @author liurz
 */
@Service
public class AlarmReportFormsQueryServiceImpl implements AlarmReportFormsQueryService {
    private static Logger logger = LoggerFactory.getLogger(AlarmReportFormsQueryServiceImpl.class);
     @Autowired
     private AlarmEventAttributeService alarmEventAttributeService;
     @Autowired
     private WebLoginAuditEsService webLoginAuditEsService;
     @Autowired
     private BusinessIntanceService businessIntanceService;

    /**
     * 查询告警信息
     * 1. 只查询50条告警信息
     * 2. 根据告警id获取对应日志中username的值
     * 3. 根据告警id获取对应工单中成因分析 zjgReason、技术整改措施 zjgRevise
     *
     * @param alarmUniteSearchVO
     * @return
     */
    @Override
    public List<AlarmUniteResultVO> queryAlarmDetail(AlarmUniteSearchVO alarmUniteSearchVO) {
        logger.debug("==========613报表告警查询开始=========");
        logger.debug("请求参数："+ JSON.toJSONString(alarmUniteSearchVO));
        // 获取时间范围内前50条数据告警数据
        List<AlarmEventAttribute> alarms = alarmEventAttributeService.getPageQueryResult(alarmUniteSearchVO.getStartTime(),alarmUniteSearchVO.getEndTime());
        if(CollectionUtil.isEmpty(alarms)){
            logger.debug("查询告警数据为空");
            return null;
        }
        List<String> eventIds = new ArrayList<>();
        alarms.stream().forEach(item -> eventIds.add(item.getEventId()));
        // 获取告警中对应日志的id
        List<String> logIds = getLogIds(alarms);
        // 获取日志信息
        List<WebLoginAudit> logDatas = webLoginAuditEsService.findAll(logIds,alarmUniteSearchVO.getReportDevType());
        // 获取告警对应工单信息
        List<BusinessIntance> businessIntances = getBusinessIntances(eventIds);
        // 数据组装
        List<AlarmUniteResultVO> results =  alarmUniteResultVOStructure(alarms,logDatas,businessIntances);
        logger.debug("==========613报表告警查询结束=========");
        return results;
    }


    /**
     * 获取告警中日志id
     * @param alarms
     * @return
     */
    private List<String> getLogIds(List<AlarmEventAttribute> alarms) {
        List<String> logIds = new ArrayList<>();
        for(AlarmEventAttribute data : alarms){
            List<LogIdVO> logs =   data.getLogs();
            if(CollectionUtil.isEmpty(logs)){
                continue;
            }
            addIds(logs,logIds);
        }
      return logIds;
    }
    private List<BusinessIntance> getBusinessIntances(List<String> eventIds) {
        if(CollectionUtil.isEmpty(eventIds)){
            return null;
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("guid",eventIds));
        List<BusinessIntance> businessIntances = businessIntanceService.findAll(conditions);
        return businessIntances;
    }

    private void addIds(List<LogIdVO> logs, List<String> logIds) {
        for(LogIdVO data : logs){
            List<String> ids =  data.getIds();
            if(CollectionUtil.isEmpty(ids)){
                continue;
            }
            logIds.addAll(ids);
        }
    }


    /**
     * 数据组装
     * 以原始日志为基础组装数据
     * @param alarms
     * @param logDatas
     * @param businessIntances
     * @return
     */
    private List<AlarmUniteResultVO> alarmUniteResultVOStructure(List<AlarmEventAttribute> alarms, List<WebLoginAudit> logDatas, List<BusinessIntance> businessIntances) {
        List<AlarmUniteResultVO> results = new ArrayList<>();
        if(CollectionUtil.isEmpty(logDatas)){
            return results;
        }
        AlarmUniteResultVO alarmUniteResultVO = null;
        for(WebLoginAudit log: logDatas){
           String logId=  log.getGuid();
           AlarmEventAttribute alarmVO=  getAlarm(logId,alarms);
           if(null == alarmVO){
               continue;
           }
            alarmUniteResultVO = new AlarmUniteResultVO();
            alarmUniteResultVO.setRemarks(alarmVO.getEventDetails());
            alarmUniteResultVO.setAlarmGuid(alarmVO.getEventId());
            addContent(businessIntances,alarmVO.getEventId(),alarmUniteResultVO);
            alarmUniteResultVO.setKey(log.getUserName());
            results.add(alarmUniteResultVO);
        }
        return results;
    }

    private AlarmEventAttribute getAlarm(String logId, List<AlarmEventAttribute> alarms) {
        for(AlarmEventAttribute data : alarms ){
            List<LogIdVO> logIdVos = data.getLogs();
            if(isExist(logId,logIdVos)){
                return data;
            }
        }
        return null;
    }

    private boolean isExist(String id, List<LogIdVO> logIdVos) {
        for(LogIdVO log : logIdVos){
            List<String> ids = log.getIds();
            for(String idVO : ids){
                if(id.equals(idVO)){
                    return true;
                }
            }
        }
        return false;
    }


    private void addContent(List<BusinessIntance> businessIntances, String eventId, AlarmUniteResultVO alarmUniteResultVO) {
        if(CollectionUtil.isEmpty(businessIntances)){
            return ;
        }
        for(BusinessIntance businessIntance : businessIntances){
            if(eventId.equals(businessIntance.getGuid())){
               String busiArgs = businessIntance.getBusiArgs();
               if(StringUtils.isEmpty(busiArgs)){
                   return;
               }
                Map<String,Object> formArgMap= JSON.parseObject(busiArgs,Map.class);
                // 成因分析 zjgReason
                String zjgReason = formArgMap.get("zjgReason")==null?"":String.valueOf(formArgMap.get("zjgReason"));
                alarmUniteResultVO.setContent(zjgReason);
                // 技术整改措施 zjgRevise
                String zjgRevise = formArgMap.get("zjgRevise")==null?"":String.valueOf(formArgMap.get("zjgRevise"));
                alarmUniteResultVO.setInfo(zjgRevise);
                return;
            }
        }
    }

}
