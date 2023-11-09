package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.constant.EventTypeEnum;
import com.vrv.vap.toolkit.constant.EventTypeStatusEnum;
import com.vrv.vap.toolkit.constant.NoticeTypeEnum;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.IBaseKoalOrgService;
import com.vrv.vap.xc.service.ISuperviseTaskService;
import com.vrv.vap.xc.service.report.EventIndicatorsService;
import com.vrv.vap.xc.tools.QueryTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventIndicatorsServiceImpl implements EventIndicatorsService {

    @Resource
    private PrintIndicatorsServiceImpl printIndicatorsService;

    @Resource
    private ISuperviseTaskService iSuperviseTaskService;

    @Resource
    private IBaseKoalOrgService iBaseKoalOrgService;

    @Override
    public VList<EventSortModel> statisticsEventTypeTimes(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, printIndicatorsService.parseModel(model),
                LogTypeConstants.ALARM_EVENT_MANAGEMENT, "eventCreattime", "yyyy-MM-dd HH:mm:ss", false);
        List<Map<String, Object>> result = QueryTools.simpleAgg(queryModel, wrapper, "eventType", 10, "eventType", "count");
        List<EventSortModel> resultList = fillEventResult(result);
        return VoBuilder.vl(resultList.size(), resultList);
    }

    @Override
    public VList<EventSortModel> statisticsEventStatusTimes(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, printIndicatorsService.parseModel(model),
                LogTypeConstants.ALARM_EVENT_MANAGEMENT, "eventCreattime", "yyyy-MM-dd HH:mm:ss", false);
        List<Map<String, Object>> result = QueryTools.simpleAgg(queryModel, wrapper, "alarmDealState", 10, "alarmDealState", "count");
        List<EventSortModel> eventModel = fillEventStatusResult(result);
        return VoBuilder.vl(eventModel.size(), eventModel);
    }

    @Override
    public VList<EventSortModel> statisticsDepartmentEvents(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, printIndicatorsService.parseModel(model),
                LogTypeConstants.ALARM_EVENT_MANAGEMENT, "eventCreattime", "yyyy-MM-dd HH:mm:ss", false);
        List<Map<String, Object>> result = QueryTools.simpleAgg(queryModel, wrapper, "unitList.unitGeoIdent", 10, "deptNo", "count");
        List<EventSortModel> convert = convert(result);
        return VoBuilder.vl(convert.size(), convert);
    }

    public String getValue(String key) {
        List<Map<String, String>> orgKeyValuePair = iBaseKoalOrgService.getOrgKeyValuePair();
        return orgKeyValuePair.stream()
                .filter(entry -> key.equals(entry.get("code")))
                .map(entry -> entry.get("name"))
                .findFirst()
                .orElse(null);
    }

    public List<EventSortModel> convert(List<Map<String, Object>> result){
        return result.stream().map(entry -> {
            EventSortModel eventSortModel = new EventSortModel();
            eventSortModel.setName(getValue((String) entry.get("deptNo")));
            eventSortModel.setCount((int) entry.get("count"));
            return eventSortModel;
        }).collect(Collectors.toList());
    }

    @Override
    public VList<EventTypeModel> statisticsInvestigationTotal(ReportParam model, boolean total) {
        List<EventTypeModel> eventTypeModels = iSuperviseTaskService.countByStatus();
        Map<String, EventTypeModel> maps = eventTypeModels.stream()
                .collect(Collectors.toMap(EventTypeModel::getName, et -> et));
        List<NoticeTypeEnum> typeEnums = Arrays.asList(NoticeTypeEnum.JOINT_INVESTIGATION_REPORTING, NoticeTypeEnum.ORGANIZER_FEEDBACK);
        List<EventTypeModel> result = typeEnums.stream()
                .map(typeEnum -> {
                    EventTypeModel eventTypeModel = maps.getOrDefault(typeEnum.getKey(), new EventTypeModel(0, 0, typeEnum.getValue()));
                    eventTypeModel.setName(typeEnum.getValue());
                    return eventTypeModel;
                }).collect(Collectors.toList());
        if (total) {
            int totalUntreated = result.stream().mapToInt(EventTypeModel::getUntreated).sum();
            int totalProcessed = result.stream().mapToInt(EventTypeModel::getProcessed).sum();
            EventTypeModel totalEventType = new EventTypeModel();
            totalEventType.setName("总计");
            totalEventType.setUntreated(totalUntreated);
            totalEventType.setProcessed(totalProcessed);
            totalEventType.setTotal(totalUntreated, totalProcessed);
            result.add(totalEventType);
        }
        return VoBuilder.vl(result.size(), result);
    }

    /**
     * 事件状态结果封装
     *
     * @param result 查询结果集
     * @return
     */
    public List<EventSortModel> fillEventStatusResult(List<Map<String, Object>> result) {
        Map<String, Integer> eventTypeCounts = result.stream()
                .collect(Collectors.groupingBy(m -> String.valueOf(m.get("alarmDealState")), Collectors.summingInt(m -> (Integer) m.get("count"))));
        List<EventSortModel> eventTypeModels = new ArrayList<>();
        eventTypeModels.add(createEventSortModel(eventTypeCounts, EventTypeStatusEnum.UNDISPOSED));
        eventTypeModels.add(createEventSortModel(eventTypeCounts, EventTypeStatusEnum.DISPOSED));
        return eventTypeModels;
    }

    private EventSortModel createEventSortModel(Map<String, Integer> eventTypeCounts, EventTypeStatusEnum statusEnum) {
        EventSortModel model = new EventSortModel();
        model.setName(statusEnum.getValue());
        if (statusEnum == EventTypeStatusEnum.UNDISPOSED) {
            model.setCount(eventTypeCounts.getOrDefault(EventTypeStatusEnum.UNDISPOSED.getKey(), 0)
                    + eventTypeCounts.getOrDefault(EventTypeStatusEnum.UNDER_DISPOSAL.getKey(), 0));
        } else {
            model.setCount(eventTypeCounts.getOrDefault(statusEnum.getKey(), 0));
        }
        return model;
    }

    /**
     * 各类型事件结果封装
     *
     * @param result 查询结果集
     * @return
     */
    public List<EventSortModel> fillEventResult(List<Map<String, Object>> result) {
        return result.stream()
                .collect(Collectors.groupingBy(m -> (Integer) m.get("eventType"), Collectors.summingInt(m -> (Integer) m.get("count"))))
                .entrySet().stream()
                .map(entry -> new EventSortModel(EventTypeEnum.forString(String.valueOf(entry.getKey())).getValue(), entry.getValue()))
                .sorted(Comparator.comparing(EventSortModel::getCount).reversed())
                .collect(Collectors.toList());
    }
}
