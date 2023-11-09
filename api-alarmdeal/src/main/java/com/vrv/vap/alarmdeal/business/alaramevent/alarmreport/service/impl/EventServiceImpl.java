package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.IdTitleValue;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventListResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTotalResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.RuleTypeInfoResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.EventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.AlarmRiskLevelEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AbnormalEventVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AbnormalUserVo;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import scala.Int;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:02
 */
@Service
public class EventServiceImpl implements EventService {
    // 日志
    private Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventCategoryService eventCategoryService;
    @Autowired
    private RiskEventRuleService riskEventRuleService;

    public List<QueryCondition_ES> getBaseQueryParam(RequestBean req){
        List<QueryCondition_ES> params = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getStartTime())){
            params.add(QueryCondition_ES.ge("eventCreattime", req.getStartTime()));
        }
        if (StringUtils.isNotBlank(req.getEndTime())){
            params.add(QueryCondition_ES.le("eventCreattime", req.getEndTime()));
        }
        return params;
    };

    @Override
    public EventTotalResponse queryEventTotal(RequestBean req) {
        logger.info("EventServiceImpl queryEventTotal start");
        EventTotalResponse result = new EventTotalResponse();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(req);
        List<QueryCondition_ES> querys = new ArrayList<>();

        // 统计新增
        querys.addAll(baseQuery);
        long total = alarmEventManagementForESService.count(querys);
        result.setTotal(total);

        logger.info("EventServiceImpl queryEventTotal total success");

        // 特别重大事件/重大事件/较大事件
        try {
            Map<String,Long> eventLevelMap = alarmEventManagementForESService.getCountGroupByField("alarmeventmanagement","alarmRiskLevel",baseQuery);
            eventLevelMap.forEach((key,value)->{
                if("5".equals(key)){
                    result.setHocNum(value);
                }else if("4".equals(key)){
                    result.setMajorNum(value);
                }else if("3".equals(key)){
                    result.setMoreNum(value);
                }
            });
        }catch (Exception ex){
            result.setHocNum(0);
            result.setMajorNum(0);
            result.setMoreNum(0);
        }
        logger.info("EventServiceImpl queryEventTotal eventLevel success");

        // 已处置事件，待处置事件
        try {
            Map<String,Long> dealStatusMap = alarmEventManagementForESService.getCountGroupByField("alarmeventmanagement","alarmDealState",baseQuery);
            dealStatusMap.forEach((key,value)->{
                if("3".equals(key)){
                    result.setDealNum(value);
                }else if("0".equals(key)){
                    result.setNotDealNum(value);
                }
            });
        }catch (Exception ex){
            result.setDealNum(0);
            result.setNotDealNum(0);
        }


        logger.info("EventServiceImpl queryEventTotal dealStatus success");

        // 督促
        querys.add(QueryCondition_ES.eq("isUrge",true));
        try {
            long urgeNum = alarmEventManagementForESService.count(querys);
            result.setUrgeNum(urgeNum);
        }catch (Exception ex){
            result.setUrgeNum(0);
        }

        logger.info("EventServiceImpl queryEventTotal isUrge success");

        // 督办
        querys.clear();
        querys.addAll(baseQuery);
        querys.add(QueryCondition_ES.eq("isSupervise",true));
        try {
            long superviseNum = alarmEventManagementForESService.count(querys);
            result.setSuperviseNum(superviseNum);
        }catch (Exception ex){
            result.setSuperviseNum(0);
        }

        logger.info("EventServiceImpl queryEventTotal isSupervise success");

        return result;
    }

    @Override
    public List<EventTypeResponse> queryEventTrend(RequestBean req) {
        List<EventTypeResponse> result = new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(req);
        Map<String,Long> res1 = alarmEventManagementForESService.queryStatisticsByTime(alarmEventManagementForESService.getIndexName(), baseQuery,"eventCreattime", DateHistogramInterval.DAY,"MM-dd");
        res1.forEach((key,value)->{
            EventTypeResponse response = new EventTypeResponse();
            response.setIndexName(key);
            response.setIndexCount(value);
            result.add(response);
        });
        if(CollectionUtils.isNotEmpty(result)){
            Collections.sort(result, new Comparator<EventTypeResponse>() {
                @Override
                public int compare(EventTypeResponse o1, EventTypeResponse o2) {
                    return (int) (DateUtil.stringToDate(o1.getIndexName(),"MM-dd").getTime()-DateUtil.stringToDate(o2.getIndexName(),"MM-dd").getTime());
                }
            });
        }
        return result;
    }

    @Override
    public List<EventTypeResponse> queryEventByType(RequestBean req, String type) {
        logger.info("queryEventByType方法请求参数："+ JSON.toJSONString(req)+",其中type的值："+type);
        List<EventTypeResponse> result = new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(req);
        switch (type){
            case "level":
                Map<String,Long> map = alarmEventManagementForESService.getCountGroupByField("alarmeventmanagement","alarmRiskLevel",baseQuery);
                map.forEach((key,value)->{
                    EventTypeResponse response = new EventTypeResponse();
                    response.setIndexName(AlarmRiskLevelEnum.getDesc(Integer.valueOf(key)));
                    response.setIndexCount(value);
                    result.add(response);
                });
                break;
            case "type":
                Map<String,Long> ruleNameMap = alarmEventManagementForESService.getCountGroupByField("alarmeventmanagement","eventType",baseQuery);
                ruleNameMap.forEach((key,value)->{
                    EventTypeResponse response = new EventTypeResponse();
                    List<QueryCondition> conditions = new ArrayList<>();
                    conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
                    conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));
                    List<EventCategory> list = eventCategoryService.findAll(conditions);
                    for(EventCategory eventCategory : list){
                        String riskEventCode=eventCategory.getCodeLevel();
                        int eventTypeNum = AlarmDealUtil.getEventTypeNum(riskEventCode);
                        if(Integer.toString(eventTypeNum).equals(key)){
                            response.setIndexName(eventCategory.getTitle());
                            response.setIndexCount(value);
                            result.add(response);
                            break;
                        }
                    }
                });
                break;
            case "dept":
                try {
                    Map<String,Long> deptMap = alarmEventManagementForESService.getCountGroupByField(alarmEventManagementForESService.getIndexName(),"unitList.unitDepartName",baseQuery);
                    deptMap.forEach((key,value)->{
                        if (StringUtils.isNotBlank(key)){
                            EventTypeResponse response = new EventTypeResponse();
                            response.setIndexName(key);
                            response.setIndexCount(value);
                            result.add(response);
                        }
//                        EventTypeResponse response = new EventTypeResponse();
//                        if(StringUtils.isEmpty(key)){
//                            response.setIndexName("未知部门");
//                        }else{
//                            response.setIndexName(key);
//                        }
//                        response.setIndexCount(value);
//                        result.add(response);
                    });
                }catch (Exception ex){
                    logger.error("根据部门统计，es数据无部门信息！error message={}",ex.getMessage());
                }
                break;
            case "status":
                Map<String,Long> stateMap = alarmEventManagementForESService.getCountGroupByField(alarmEventManagementForESService.getIndexName(),"alarmDealState",baseQuery);
                stateMap.forEach((key,value)->{
                    EventTypeResponse response = new EventTypeResponse();
                    response.setIndexName(AlarmDealStateEnum.getDesc(Integer.valueOf(key)));
                    response.setIndexCount(value);
                    result.add(response);
                });
                break;
            case "staff":
                try {
                    Map<String,Long> staffMap = alarmEventManagementForESService.getCountGroupByField(alarmEventManagementForESService.getIndexName(),"relatedStaffInfos.staffName",baseQuery);
                    staffMap.forEach((key,value)->{
                        EventTypeResponse response = new EventTypeResponse();
                        if(StringUtils.isEmpty(key)){
                            response.setIndexName("未知人员");
                        }else{
                            response.setIndexName(key);
                        }
                        response.setIndexCount(value);
                        result.add(response);
                    });
                }catch (Exception ex){
                    logger.error("根据部门同步，es数据无relatedStaffInfos信息！error message={}",ex.getMessage());
                }
                break;
            default:
                break;
        }
        return result;
    }
    @Override
    public List<EventTypeResponse> typeTop10(RequestBean item, Integer integer) {
        List<EventTypeResponse> result = new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(item);
        baseQuery.add(QueryCondition_ES.eq("eventType",integer));
        Map<String, Long> ruleName = alarmEventManagementForESService.getCountGroupNumByFieldSize(alarmEventManagementForESService.getIndexName(), "ruleName", baseQuery, 10);
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(ruleName.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        list.forEach(a->{
            EventTypeResponse response = new EventTypeResponse();
            if(StringUtils.isEmpty(a.getKey())){
                response.setIndexName("未知事件");
            }else{
                response.setIndexName(a.getKey());
            }
            response.setIndexCount(a.getValue());
            result.add(response);
        });

        return result;
    }

    @Override
    public List<AbnormalEventVo> abnormalInfo10(RequestBean item, Integer integer) {
        List<AbnormalEventVo> result = new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(item);
        baseQuery.add(QueryCondition_ES.eq("eventType",integer));
        Map<String, Long> ruleName = alarmEventManagementForESService.getCountGroupNumByFieldSize(alarmEventManagementForESService.getIndexName(), "ruleName", baseQuery, 10);
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(ruleName.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        list.forEach(a->{
            AbnormalEventVo response = new AbnormalEventVo();
            if(StringUtils.isEmpty(a.getKey())){
                response.setRuleName("未知事件");
            }else{
                response.setRuleName(a.getKey());
            }
            response.setEventCount(Math.toIntExact(a.getValue()));
            List<QueryCondition> queryConditions=new ArrayList<>();
            queryConditions.add(QueryCondition.eq("name", response.getRuleName()));
            List<RiskEventRule> all = riskEventRuleService.findAll(queryConditions);
            if (all.size()>0){
                RiskEventRule riskEventRule = all.get(0);
                if (riskEventRule.getLevelstatus()!=null){
                    response.setAlarmRiskLevel(AlarmRiskLevelEnum.getDesc(Integer.valueOf(riskEventRule.getLevelstatus())));
                }
            }
            //得到异常用户数量
            List<QueryCondition_ES> queryConditionEs = getBaseQueryParam(item);
            queryConditionEs.add(QueryCondition_ES.eq("eventType",integer));
            queryConditionEs.add(QueryCondition_ES.eq("ruleName",response.getRuleName()));
            Map<String, Long> countGroupNumByFieldSize = alarmEventManagementForESService.getCountGroupNumByFieldSize(alarmEventManagementForESService.getIndexName(), "relatedStaffInfos.staffName", queryConditionEs, 1000);
            response.setStaffInfoCount(countGroupNumByFieldSize.size());
            result.add(response);
        });
        return result;
    }

    @Override
    public List<AbnormalUserVo> abnormalUserInfo(RequestBean item,Integer integer ) {
        List<AbnormalUserVo> abnormalUserVos=new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(item);
        baseQuery.add(QueryCondition_ES.eq("eventType",integer));
        Map<String, Long> ruleName = alarmEventManagementForESService.getCountGroupByField(alarmEventManagementForESService.getIndexName(), "relatedStaffInfos.staffName", baseQuery);
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(ruleName.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        list.forEach(a->{
            if (StringUtils.isNotBlank(a.getKey())){
                AbnormalUserVo abnormalUserVo=new AbnormalUserVo();
                abnormalUserVo.setStaffName(a.getKey());
                abnormalUserVo.setEventCount(Math.toIntExact(a.getValue()));
                List<QueryCondition_ES> queryConditionEs = getBaseQueryParam(item);
                queryConditionEs.add(QueryCondition_ES.eq("eventType",integer));
                queryConditionEs.add(QueryCondition_ES.eq("relatedStaffInfos.staffName",a.getKey()));
                EventDetailQueryVO query=new EventDetailQueryVO();
                query.setEventType(integer);
                query.setUserName(a.getKey());
                try {
                    query.setBeginTime(DateUtil.parseDate(item.getStartTime(),DateUtil.DEFAULT_DATE_PATTERN));
                    query.setEndTime(DateUtil.parseDate(item.getEndTime(),DateUtil.DEFAULT_DATE_PATTERN));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                query.setBy_("desc");
                query.setOrder_("alarmRiskLevel");
                query.setCount_(1);
                query.setStart_(0);
                PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForESService.getPageQueryResult(query, query, false);
                if (pageQueryResult.getList().size()>0){
                    AlarmEventAttribute alarmEventAttribute = pageQueryResult.getList().get(0);
                    List<StaffInfo> relatedStaffInfos = alarmEventAttribute.getRelatedStaffInfos();
                    abnormalUserVo.setAlarmRiskLevel(AlarmRiskLevelEnum.getDesc(Integer.valueOf(alarmEventAttribute.getAlarmRiskLevel())));
                    if (relatedStaffInfos.size()>0){
                        List<StaffInfo> collect = relatedStaffInfos.stream().filter(m -> m.getStaffName().equals(a.getKey())).collect(Collectors.toList());
                        if (collect.size()>0){
                            StaffInfo staffInfo = collect.get(0);
                            abnormalUserVo.setStaffNo(staffInfo.getStaffNo());
                            abnormalUserVo.setStaffDepartment(staffInfo.getStaffDepartment());
                            abnormalUserVo.setStaffLevel(getLeverl(staffInfo.getStaffLevel()));
                        }
                    }
                }
                abnormalUserVos.add(abnormalUserVo);
            }

        });
        if (abnormalUserVos.size()>0){
            int id=1;
            for (AbnormalUserVo abnormalUserVo:abnormalUserVos){
                abnormalUserVo.setId(id);
                id++;
            }
        }
        return abnormalUserVos;
    }
    @Override
    public List<AbnormalUserVo> eventUser(RequestBean item) {
        List<AbnormalUserVo> result=new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(item);
        Map<String,Long> staffMap = alarmEventManagementForESService.getCountGroupNumByFieldSize(alarmEventManagementForESService.getIndexName(),"relatedStaffInfos.staffNo",baseQuery,10);
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(staffMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        list.forEach((a)->{
            if(StringUtils.isNotBlank(a.getKey())){
                List<QueryCondition_ES> baseQueryParam = getBaseQueryParam(item);
                baseQueryParam.add(QueryCondition_ES.eq("relatedStaffInfos.staffNo",a.getKey()));
                AbnormalUserVo response = new AbnormalUserVo();
                response.setStaffNo(a.getKey());
                response.setEventCount(Math.toIntExact(a.getValue()));
                AlarmEventAttribute all = alarmEventManagementForESService.findOne(baseQueryParam);
                if (all!=null){
                    List<StaffInfo> staffInfos = all.getStaffInfos();
                    if (staffInfos.size()>0){
                        List<StaffInfo> collect = staffInfos.stream().filter(p -> p.getStaffNo().equals(a.getKey())).collect(Collectors.toList());
                        if (collect.size()>0){
                            response.setStaffName(collect.get(0).getStaffName());
                        }
                    }
                }
                result.add(response);
            }
        });
        return result;
    }

    @Override
    public List<EventTypeResponse> eventUserTop10(RequestBean item) {
        List<EventTypeResponse> result=new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(item);
        Map<String,Long> staffMap = alarmEventManagementForESService.getCountGroupNumByFieldSize(alarmEventManagementForESService.getIndexName(),"relatedStaffInfos.staffName",baseQuery,10);
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(staffMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        list.forEach((a)->{
            if(StringUtils.isNotBlank(a.getKey())){
                EventTypeResponse response = new EventTypeResponse();
                response.setIndexName(a.getKey());
                response.setIndexCount(Math.toIntExact(a.getValue()));
                result.add(response);
            }
        });
        return result;
    }

    @Override
    public List<RuleTypeInfoResponse> eventTypeInfo(RequestBean item) {
        List<RuleTypeInfoResponse> ruleTypeInfoResponses=new ArrayList<>();
        List<QueryCondition_ES> baseQuery = getBaseQueryParam(item);
        SearchField childField = new SearchField(getBaseField() + "alarmRiskLevel", FieldType.String, 0, 10000, null);
        SearchField searchField=new SearchField(getBaseField() + "eventType", FieldType.String, childField);
        List<Map<String, Object>> maps = alarmEventManagementForESService.queryStatistics(baseQuery, searchField);
        for (Map<String, Object> map:maps){
            RuleTypeInfoResponse response=new RuleTypeInfoResponse();
            String eventCreattime = map.get("eventType").toString();
            setTypeName(eventCreattime,response);
            List<Map<String,Object>> principalIp = (List<Map<String, Object>>) map.get("alarmRiskLevel");
            if (principalIp!=null&&principalIp.size()>0){
                Long num= 0L;
                for (Map<String,Object> objectMap:principalIp){
                    String principalIp1 = objectMap.get("alarmRiskLevel").toString();
                    Long count = (Long)objectMap.get("doc_count");
                    setTypeCount(principalIp1,count,response);
                    num=num+count;
                }
                response.setTotal(num);
            }
            ruleTypeInfoResponses.add(response);
        }
        RuleTypeInfoResponse response=new RuleTypeInfoResponse();
        response.setName("总计");
        long sum = ruleTypeInfoResponses.stream().mapToLong(a -> a.getTotal()).sum();
        long gen = ruleTypeInfoResponses.stream().mapToLong(a -> a.getTotalGeneral()).sum();
        long low = ruleTypeInfoResponses.stream().mapToLong(a -> a.getTotalLower()).sum();
        long imp = ruleTypeInfoResponses.stream().mapToLong(a -> a.getTotalImportant()).sum();
        long ser = ruleTypeInfoResponses.stream().mapToLong(a -> a.getTotalSerious()).sum();
        long eme = ruleTypeInfoResponses.stream().mapToLong(a -> a.getTotalEmergent()).sum();
        response.setTotalLower(low);
        response.setTotal(sum);
        response.setTotalGeneral(gen);
        response.setTotalImportant(imp);
        response.setTotalSerious(ser);
        response.setTotalEmergent(eme);
        ruleTypeInfoResponses.add(response);
        return ruleTypeInfoResponses;
    }

    private void setTypeCount(String principalIp1, Long count, RuleTypeInfoResponse response) {
        switch (principalIp1) {
            case "1":
                response.setTotalLower(count);
                break;
            case "2":
                response.setTotalGeneral(count);
                break;
            case "3":
                response.setTotalImportant(count);
                break;
            case "4":
                response.setTotalSerious(count);
                break;
            case "5":
                response.setTotalEmergent(count);
                break;
            default:
                break;
        }
    }

    private void setTypeName(String eventCreattime, RuleTypeInfoResponse response) {
        switch (eventCreattime) {
            case "1":
                response.setName("配置合规信息");
                break;
            case "2":
                response.setName("网络安全异常");
                break;
            case "3":
                response.setName("用户行为异常");
                break;
            case "4":
                response.setName("运维行为异常");
                break;
            case "5":
                response.setName("应用异常");
                break;
            case "6":
                response.setName("互联互通异常");
                break;
            default:
                break;
        }
    }

    public String getBaseField() {
        return "";
    }
    @Value("${classifiedLevel.parentType.app:f5a4ae5b-3cee-a84f-7471-8f23ezjg1100}")
    private String appParentType;
    private String getLeverl(String staffLevel) {
        String sql = "select base_dict_all.code_value from base_dict_all where code='"+staffLevel+"' and parent_type ='"+appParentType+"'";
        List<String> strings = jdbcTemplate.queryForList(sql, String.class);
        if (strings.size()>0){
            return strings.get(0);
        }
        return "";
    }

    @Override
    public List<EventListResponse> queryEventList(RequestBean req) {
        String sql = "SELECT  JSON_UNQUOTE(JSON_EXTRACT(busi_args,'$.zjgEventsName')) AS zjgEventsName, JSON_UNQUOTE(JSON_EXTRACT(busi_args,'$.zjgReason')) AS zjgReason ," +
                "JSON_UNQUOTE(JSON_EXTRACT(busi_args,'$.zjgRevise')) AS zjgRevise, create_date AS startTime,finish_date AS endTime , '已审核' AS `status` " +
                "FROM business_intance WHERE stat_enum='end' and process_def_name='事件处置流程'";
        List<EventListResponse> result = jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(EventListResponse.class));
        return result;
    }


}
