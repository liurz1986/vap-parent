package com.vrv.vap.xc.service.impl.report;

import com.google.gson.*;
import com.vrv.vap.toolkit.constant.CauseTypeEnum;
import com.vrv.vap.toolkit.constant.EventTypeEnum;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.pojo.SuperviseDataSubmit;
import com.vrv.vap.xc.service.ISuperviseDataSubmitService;
import com.vrv.vap.xc.service.report.ClueInfoIndicatorsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ClueInfoIndicatorsServiceImpl implements ClueInfoIndicatorsService {
    @Resource
    private ISuperviseDataSubmitService iSuperviseDataSubmitService;

    private Map<String, Integer> count(String countField) {
        List<SuperviseDataSubmit> list = iSuperviseDataSubmitService.queryClueInfo(3);
        List<JsonObject> dataList = list.stream()
                .map(s -> JsonParser.parseString(s.getData()).getAsJsonObject().getAsJsonArray("data"))
                .flatMap(jsonArray -> StreamSupport.stream(jsonArray.spliterator(), false))
                .map(JsonElement::getAsJsonObject)
                .collect(Collectors.toList());
        return dataList.stream()
                .filter(entry -> StringUtils.isNotEmpty(entry.get(countField).getAsString()))
                .collect(Collectors.groupingBy(entry -> entry.get(countField).getAsString(),
                        Collectors.mapping(entry -> 1, Collectors.reducing(0, Integer::sum))));
    }

    private VList<EventSortModel> fillCauseInfo(String countField, Map<String, String> eventTypeNames) {
        Map<String, Integer> causeTypeCount = count(countField);
        List<EventSortModel> eventModels = eventTypeNames.entrySet().stream().map(entry -> {
            EventSortModel eventSortModel = new EventSortModel();
            eventSortModel.setName(entry.getValue());
            eventSortModel.setCount(causeTypeCount.getOrDefault(entry.getKey(), 0));
            return eventSortModel;
        }).sorted(Comparator.comparing(EventSortModel::getCount).reversed()).collect(Collectors.toList());
        return VoBuilder.vl(eventModels.size(), eventModels);
    }

    @Override
    public VList<EventSortModel> statisticsCauseTypeTimes(ReportParam model, String countField) {
        return fillCauseInfo(countField, CauseTypeEnum.getKeyToValueMap());
    }

    @Override
    public VList<EventSortModel> statisticsClueInfoType(ReportParam model, String countField) {
        return fillCauseInfo(countField, EventTypeEnum.getKeyToValueMap());
    }

    @Override
    public VList<ClueInfoModel> statisticsClueInfoTotal(ReportParam model) {
        List<SuperviseDataSubmit> list = iSuperviseDataSubmitService.queryClueInfo(3);
        return VoBuilder.vl(1, Collections.singletonList(new ClueInfoModel(list.size(), 0)));
    }

    @Override
    public VList<ClueInfoModel> statisticsNewlyAddedClueInfo(ReportParam model) {
        List<SuperviseDataSubmit> list = iSuperviseDataSubmitService.queryClueInfoByTime(3, model.getStartTime(), model.getEndTime());
        return VoBuilder.vl(1, Collections.singletonList(new ClueInfoModel(0, list.size())));
    }
}
