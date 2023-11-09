package com.vrv.vap.alarmdeal.business.analysis.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventAlarmSettingService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.RuleStartedStatisticsData;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(description = "预警管理")
@RestController
@RequestMapping("/workbench")
public class WorkbenchInterfaceController extends BaseController {

    @Autowired
    RiskEventRuleService riskEventRuleService;

    @Autowired
    AlarmAnalysisService alarmAnalysisService;

    @Autowired
    EventCategoryService eventCategoryService;

    @Autowired
    EventAlarmSettingService eventAlarmSettingService;

    @Autowired
    MapperUtil mapper;

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForESService;

    private static Logger logger = LoggerFactory.getLogger(WorkbenchInterfaceController.class);

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    @PostMapping("/getStatisticsCount")
    @ApiOperation(value = "统计数量", notes = "")
    @SysRequestLog(description = "预警管理统计数量", actionType = ActionType.SELECT, manually = false)
    public Result<List<NameValue>> getStatisticsCount(@RequestBody EventDetailQueryVO query) {

        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<String> roleCode = currentUser.getRoleCode();
        if (roleCode == null || roleCode.isEmpty()) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户权限异常");
        }

        List<QueryCondition_ES> basequerys = new ArrayList<>();
        //业务主管看用户  其他人看全部
        if (roleCode.size() == 1 && roleCode.contains("businessMgr")) {
            basequerys.add(QueryCondition_ES.eq(alarmEventManagementForESService.getBaseField() + "eventType", 3));
        }

        List<NameValue> result = new ArrayList<>();

        //IndexsInfoVO index = getIndex();
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.addAll(alarmEventManagementForESService.getQuerys(query));
        querys.addAll(alarmEventManagementForESService.getDataPermissions());
        querys.addAll(basequerys);
        long total = alarmEventManagementForESService.count(querys);
        result.add(new NameValue(Long.toString(total), "total"));

        //督促事件数
        querys.clear();
        querys.addAll(getTimeRangeConditionByField("urgeInfos.urgeTime"));
        querys.addAll(alarmEventManagementForESService.getDataPermissions());
        querys.addAll(basequerys);
        querys.add(QueryCondition_ES.eq(alarmEventManagementForESService.getBaseField() + "isUrge", true));
        Long urgeCount = alarmEventManagementForESService.count(querys);
        result.add(new NameValue(Long.toString(urgeCount), "urgeCount"));


        //督办事件数
        querys.clear();
        querys.addAll(getTimeRangeConditionByField("superviseTime"));
        querys.addAll(alarmEventManagementForESService.getDataPermissions());
        querys.addAll(basequerys);
        querys.add(QueryCondition_ES.eq(alarmEventManagementForESService.getBaseField() + "isSupervise", true));
        Long superviseCount = alarmEventManagementForESService.count(querys);
        result.add(new NameValue(Long.toString(superviseCount), "superviseCount"));

        return ResultUtil.success(result);
    }


    /**
     * 今日概览，只会筛选出当天的
     * @param field 筛选字段
     * */
    private  static  List<QueryCondition_ES> getTimeRangeConditionByField(String field){
        Date now = new Date();
        List<QueryCondition_ES> queryConditionEs=new ArrayList<>();
        queryConditionEs.add(QueryCondition_ES.ge(field, DateUtil.format(now,DateUtil.Year_Mouth_Day+" 00:00:00")));
        queryConditionEs.add(QueryCondition_ES.le(field,DateUtil.format(now,DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN))));
        return queryConditionEs;
    }


    @GetMapping("/getEventRuleStartedStatistics")
    @ApiOperation(value = "获取监管策略开启状态统计", notes = "")
    @SysRequestLog(description = "获取监管策略开启状态统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<RuleStartedStatisticsData>> getEventRuleStartedStatistics() {

        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<String> roleCode = currentUser.getRoleCode();
        if (roleCode == null || roleCode.isEmpty()) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户权限异常");
        }

//		name	code	description
//		平台维护员	admin	平台维护，具备系统管理员所有功能，但平台维护员不能管理系统管理员，但系统管理员可以管理平台维护员
//		系统管理员	sysAdmin	三权之系统管理员，具有用户管理、升级维护、系统运行状态日志管理功能。
//		安全审计员	audAdmin	三权之安全审计员，具有系统管理员和安全保密管理员日志管理等功能。
//		安全保密管理员	secAdmin	三权之安全保密管理员，具有用户权限管理、分析模型停用授权、用户行为及安全审计员日志管理等功能。
//		保密主管	secretMgr	查看单位保密态势，督促单位内各部门监管事件处置，处置上级监管平台下发的预警和督办任务、生成涉密网络保密监管情况报告。
//		业务主管	businessMgr	查看单位内所属部门保密态势，管理所属部门用户行为监测策略，处置所属部门用户行为异常事件，填报处置结果。
//		运维主管	operationMgr	管理网络基础信息，查看单位保密态势，处置配置合规性、网络安全异常、应用服务异常、运维行为异常、跨单位互联异常等监管事件，填报处置结果。

        List<RuleStartedStatisticsData> result = new ArrayList<>();

        List<Map<String, Object>> eventRuleStartedStatistics = alarmAnalysisService.getEventRuleStartedStatistics();

        List<String> guids = new ArrayList<>();

        if (!roleCode.contains("admin") && !roleCode.contains("secretMgr")) {
            List<QueryCondition> cons = new ArrayList<>();

            List<QueryCondition> ors = new ArrayList<>();
            for (String code : roleCode) {
                ors.add(QueryCondition.like("toRole", "\"" + code + "\""));
            }
            ors.add(QueryCondition.like("toUser", ":" + currentUser.getId() + ","));

            cons.add(QueryCondition.or(ors));
            cons.add(QueryCondition.eq("linkType", "category"));
            List<EventAlarmSetting> findAll = eventAlarmSettingService.findAll(cons);

            for (EventAlarmSetting setting : findAll) {
                guids.add(setting.getLinkGuid());
            }
        } else {
            guids = null;
        }

        //业务主管看用户  其他人看全部
        if (roleCode.size() == 1 && roleCode.contains("businessMgr")) {
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/AbnormalUserBehavior/"));//仅看用户行为下的
            conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/AbnormalUserBehavior/%/")));//不包含第四级

            List<EventCategory> categorys = eventCategoryService.findAll(conditions);
            for (EventCategory category : categorys) {
                RuleStartedStatisticsData item = new RuleStartedStatisticsData();
                item.setCategoryId(category.getId());
                item.setCategoryHierarchy("3");
                item.setCodeLevel(category.getCodeLevel());
                item.setCategoryName(category.getTitle());

                int openCount = 0;
                int total = 0;

                for (Map<String, Object> map : eventRuleStartedStatistics) {
                    if (guids != null) {
                        if (!guids.contains(map.get("eventRuleId").toString()) && !guids.contains(map.get("eventRuleParentId").toString())) {
                            continue;
                        }
                    }

                    if (map.containsKey("eventRuleId") && category.getId().equals(map.get("eventRuleId").toString())) {
                        total++;
                        if (map.containsKey("isStarted") && "1".equals(map.get("isStarted").toString())) {
                            openCount++;
                        }
                    }
                }
                item.setRuleTotal(total);
                item.setOpenRuleCount(openCount);
                result.add(item);
            }
        } else {

            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
            if (roleCode.size() == 1 && roleCode.contains("operationMgr"))//运维管理员
            {
                conditions.add(QueryCondition.notEq("codeLevel", "/safer/AbnormalUserBehavior"));//不包含用户行为
            }
            conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));//不包含第三级
            List<EventCategory> categorys = eventCategoryService.findAll(conditions);

            for (EventCategory category : categorys) {
                RuleStartedStatisticsData item = new RuleStartedStatisticsData();
                item.setCategoryId(category.getId());
                item.setCategoryHierarchy("2");
                item.setCodeLevel(category.getCodeLevel());
                item.setCategoryName(category.getTitle());
                int openCount = 0;
                int total = 0;

                for (Map<String, Object> map : eventRuleStartedStatistics) {
                    if (guids != null) {
                        if (!guids.contains(map.get("eventRuleId").toString()) && !guids.contains(map.get("eventRuleParentId").toString())) {
                            continue;
                        }
                    }
                    if (map.containsKey("eventRuleParentId") && category.getId().equals(map.get("eventRuleId").toString())) {
                        total++;
                        if (map.containsKey("isStarted") && "1".equals(map.get("isStarted").toString())) {
                            openCount++;
                        }
                    }
                }

                item.setRuleTotal(total);
                item.setOpenRuleCount(openCount);
                result.add(item);
            }
        }

        return ResultUtil.success(result);
    }

    @PostMapping("/getEventRuleTypeStatistics")
    @ApiOperation(value = "获取监管类型处理状态统计", notes = "")
    @SysRequestLog(description = "获取监管类型处理状态统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<RuleStartedStatisticsData>> getEventRuleTypeStatistics(@RequestBody EventDetailQueryVO query) {

        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<String> roleCode = currentUser.getRoleCode();
        if (roleCode == null || roleCode.isEmpty()) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户权限异常");
        }

        List<RuleStartedStatisticsData> result = new ArrayList<>();

        //业务主管看用户  其他人看全部
        if (roleCode.size() == 1 && roleCode.contains("businessMgr")) {
            // 分六次查询
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/AbnormalUserBehavior/"));
            conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/AbnormalUserBehavior/%/")));
            List<EventCategory> categorys = eventCategoryService.findAll(conditions);
            for (EventCategory category : categorys) {
                List<QueryCondition_ES> querys = new ArrayList<>();
                querys.addAll(alarmEventManagementForESService.getQuerys(query));
                querys.addAll(alarmEventManagementForESService.getDataPermissions());// 数据权限
                querys.add(QueryCondition_ES.eq(alarmEventManagementForESService.getBaseField() + "eventType", 3));
                querys.add(QueryCondition_ES.likeBegin(alarmEventManagementForESService.getBaseField() + "eventCode",
                        category.getCodeLevel()));
                SearchField childField = new SearchField(
                        alarmEventManagementForESService.getBaseField() + "alarmDealState", FieldType.String, 0, 5,
                        null);

                List<Map<String, Object>> queryStatistics = alarmEventManagementForESService.queryStatistics(querys,
                        childField);

                RuleStartedStatisticsData item = new RuleStartedStatisticsData();
                item.setCategoryId(category.getId());
                item.setCategoryHierarchy("3");
                item.setCodeLevel(category.getCodeLevel());
                item.setCategoryName(category.getTitle());
                int openCount = 0;
                int total = 0;

                for (Map<String, Object> map : queryStatistics) {
                    // result.add(new
                    // NameValue(map.get("doc_count").toString(),map.get(groupByName).toString()));
                    String state = map.get(alarmEventManagementForESService.getBaseField() + "alarmDealState")
                            .toString();
                    String value = map.get("doc_count").toString();
                    int count = Integer.parseInt(value);
                    total += count;
                    if (!"3".equals(state)) {
                        openCount += count;
                    }
                }

                item.setRuleTotal(total);
                item.setOpenRuleCount(openCount);
                result.add(item);
            }
        } else {
            //按type 分组
            List<QueryCondition_ES> querys = new ArrayList<>();
            if (roleCode.size() == 1 && roleCode.contains("operationMgr"))//运维管理员
            {
                querys.add(QueryCondition_ES.notEq(alarmEventManagementForESService.getBaseField() + "eventType", 3));
            }
            querys.addAll(alarmEventManagementForESService.getQuerys(query));
            querys.addAll(alarmEventManagementForESService.getDataPermissions());//数据权限
            SearchField childField = new SearchField(alarmEventManagementForESService.getBaseField() + "alarmDealState", FieldType.String, 0, 5, null);
            SearchField searchField = new SearchField(alarmEventManagementForESService.getBaseField() + "eventType", FieldType.String, 0, 10, childField);

            List<Map<String, Object>> queryStatistics = alarmEventManagementForESService.queryStatistics(querys, searchField);
            logger.debug(gson.toJson(queryStatistics));
            //[{"alarmDealState":[{"doc_count":117,"alarmDealState":"0"}],"eventType":"2"}]

            for (Map<String, Object> mapEventType : queryStatistics) {
                String eventType = mapEventType.get("eventType").toString();

                RuleStartedStatisticsData item = new RuleStartedStatisticsData();
                //eventType

                List<QueryCondition> conditions = new ArrayList<>();

                switch (eventType) {
                    case "1":
                        conditions.add(QueryCondition.eq("codeLevel", "/safer/ConfigurationCompliance"));
                        break;
                    case "2":
                        conditions.add(QueryCondition.eq("codeLevel", "/safer/NetworkSecurityException"));
                        break;
                    case "3":
                        conditions.add(QueryCondition.eq("codeLevel", "/safer/AbnormalUserBehavior"));
                        break;
                    case "7":
                        conditions.add(QueryCondition.eq("codeLevel", "/safer/adminException"));
                        break;
                    case "8":
                        conditions.add(QueryCondition.eq("codeLevel", "/safer/boundaryException"));
                        break;
//                    case "6":
//                        conditions.add(QueryCondition.eq("codeLevel", "/safer/ConnectivityAbnormal"));
//                        break;
                    default:
                        break;
                }

                List<EventCategory> categorys = eventCategoryService.findAll(conditions);
                if (categorys == null || categorys.isEmpty()) {
                    continue;
                }
                EventCategory category = categorys.get(0);

                item.setCategoryId(category.getId());
                item.setCategoryHierarchy("3");
                item.setCodeLevel(category.getCodeLevel());
                item.setCategoryName(category.getTitle());
                int openCount = 0;
                int total = 0;

                List<Map<String, Object>> mapAlarmDealState = (List<Map<String, Object>>) mapEventType.get("alarmDealState");
                for (Map<String, Object> map : mapAlarmDealState) {
                    int count = Integer.parseInt(map.get("doc_count").toString());
                    if (!"3".equals(map.get("alarmDealState").toString())) {
                        openCount += count;
                    }
                    total += count;
                }
                item.setRuleTotal(total);
                item.setOpenRuleCount(openCount);
                result.add(item);
            }

            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.like("codeLevel", "/safer/%"));
            conditions.add(QueryCondition.not(QueryCondition.like("codeLevel", "/safer/%/")));
            if (roleCode.size() == 1 && roleCode.contains("operationMgr"))//运维管理员
            {
                conditions.add(QueryCondition.not(QueryCondition.like("codeLevel", "/safer/AbnormalUserBehavior")));
            }
            List<EventCategory> categorys = eventCategoryService.findAll(conditions);

            for (EventCategory category : categorys) {
                RuleStartedStatisticsData data = null;
                for (RuleStartedStatisticsData item : result) {
                    if (item.getCodeLevel().equals(category.getCodeLevel())) {
                        data = item;
                        break;
                    }
                }

                if (data == null) {
                    data = new RuleStartedStatisticsData();
                    data.setCategoryId(category.getId());
                    data.setCategoryHierarchy("3");
                    data.setCodeLevel(category.getCodeLevel());
                    data.setCategoryName(category.getTitle());
                    data.setRuleTotal(0);
                    data.setOpenRuleCount(0);
                    result.add(data);
                }
            }

        }

        return ResultUtil.success(result);
    }

    @Data
    class PageRowData {

        String categoryName;

        String codeLevel;

        List<AlarmEventAttributeVO> values;
    }

    @PostMapping("/getAlarmDealPager")
    @ApiOperation(value = "获得告警事件列表", notes = "")
    @SysRequestLog(description = "获得告警事件列表", actionType = ActionType.SELECT, manually = false)
    public Result<List<PageRowData>> getAlarmDealPager(
            @RequestBody EventDetailQueryVO query) {

        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<String> roleCode = currentUser.getRoleCode();
        if (roleCode == null || roleCode.isEmpty()) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户权限异常");
        }

        List<PageRowData> result = new ArrayList<>();

        // 业务主管看用户 其他人看全部
        if (roleCode.size() == 1 && roleCode.contains("businessMgr")) {
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/AbnormalUserBehavior/"));// 仅看用户行为下的
            conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/AbnormalUserBehavior/%/")));// 不包含第四级
            List<EventCategory> categorys = eventCategoryService.findAll(conditions);
            for (EventCategory category : categorys) {
                List<AlarmEventAttributeVO> values = getPageValues(query, category);
                PageRowData row = new PageRowData();
                row.setCategoryName(category.getTitle());
                row.setCodeLevel(category.getCodeLevel());
                row.setValues(values);
                result.add(row);
            }
        } else {

            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
            if (roleCode.size() == 1 && roleCode.contains("operationMgr"))// 运维管理员
            {
                conditions.add(QueryCondition.notEq("codeLevel", "/safer/AbnormalUserBehavior"));// 不包含用户行为
            }
            conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));// 不包含第三级
            List<EventCategory> categorys = eventCategoryService.findAll(conditions);

            for (EventCategory category : categorys) {
                List<AlarmEventAttributeVO> values = getPageValues(query, category);
                PageRowData row = new PageRowData();
                row.setCategoryName(category.getTitle());
                row.setCodeLevel(category.getCodeLevel());
                row.setValues(values);
                result.add(row);
            }
        }

        return ResultUtil.success(result);
    }

    private List<AlarmEventAttributeVO> getPageValues(EventDetailQueryVO query,
                                                      EventCategory category) {
        EventDetailQueryVO querys = mapper.map(query, EventDetailQueryVO.class);
        querys.setEventCodeBeginLike(category.getCodeLevel());
        NameValue key = new NameValue(category.getCodeLevel(), category.getTitle());
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForESService.getPageQueryResult(querys,
                querys, true);
        List<AlarmEventAttribute> sourceList = pageQueryResult.getList();
        List<AlarmEventAttributeVO> list = mapper.mapList(sourceList, AlarmEventAttributeVO.class);

        return list;
    }
}
