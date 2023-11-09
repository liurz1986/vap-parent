package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.FocalResultResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.FocalTotalResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.FocalReportService;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:04
 */
@Service
public class FocalReportServiceImpl implements FocalReportService {
    // 日志
    private Logger logger = LoggerFactory.getLogger(FocalReportServiceImpl.class);

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForESService;

    @Autowired
    AssetService assetService;

    public List<QueryCondition_ES> getBaseQueryParam(RequestBean req){
        List<QueryCondition_ES> params = new ArrayList<>();
        params.add(QueryCondition_ES.ge("eventCreattime", req.getStartTime()));
        params.add(QueryCondition_ES.le("eventCreattime", req.getEndTime()));
        return params;
    };

    @Override
    public FocalTotalResponse queryFocalTotal(RequestBean req) {
        FocalTotalResponse result = new FocalTotalResponse();
        List<QueryCondition_ES> reqs = new ArrayList<>();
        reqs.addAll(getBaseQueryParam(req));

        // 重点监管事件总数
        reqs.add(QueryCondition_ES.ge("alarmRiskLevel",3));
        long count =alarmEventManagementForESService.count(reqs);
        result.setTotal(count);

        SearchField searchField = new SearchField("principalIp", FieldType.String, 0, 10000, null);
        try {
            List<Map<String, Object>> queryStatistics = alarmEventManagementForESService.queryStatistics(reqs, searchField);
            result.setFocalViolationAssets(Long.valueOf(queryStatistics.size()));
        }catch (Exception ex){
            result.setFocalViolationAssets(Long.valueOf(0));
        }

        SearchField staffNoField = new SearchField("staffInfos.staffNo", FieldType.String, 0, 10000, null);
        try {
            List<Map<String, Object>> staffNoStatistics = alarmEventManagementForESService.queryStatistics(reqs, staffNoField);
            result.setFocalViolations(Long.valueOf(staffNoStatistics.size()));
        }catch (Exception ex){
            result.setFocalViolations(Long.valueOf(0));
        }

        return result;
    }

    @Override
    public List<EventTypeResponse> queryFocalType(RequestBean req,String type,int topNum) {
        List<EventTypeResponse> result = new ArrayList<>();
        List<QueryCondition_ES> reqs = new ArrayList<>();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.ge("alarmRiskLevel",3));
        switch (type){
            case "number":
                List<EventTypeResponse> numberList = new ArrayList<>();
                Map<String,Long> eventNameMap = alarmEventManagementForESService.getCountGroupByField(alarmEventManagementForESService.getIndexName(),"eventName",reqs);
                eventNameMap.forEach((key,value)->{
                    EventTypeResponse response = new EventTypeResponse();
                    response.setIndexName(key);
                    response.setIndexCount(value);
                    numberList.add(response);
                });
                Collections.sort(numberList, Comparator.comparing(EventTypeResponse::getIndexCount).reversed());
                result = numberList.stream().limit(10).collect(Collectors.toList());
                break;
            case "asset":
                SearchField searchField = new SearchField("principalIp", FieldType.String, 0, 10, null);
                List<Map<String, Object>> queryStatistics = alarmEventManagementForESService.queryStatistics(reqs, searchField);
                for(Map<String, Object> map:queryStatistics){
                    EventTypeResponse response = new EventTypeResponse();
                    Asset asset = assetService.queryAssetByIp(String.valueOf(map.get("principalIp")));
                    response.setIndexName(String.valueOf(map.get("principalIp")));
                    if(asset != null){
                        response.setIndexName(asset.getName());
                    }
                    response.setIndexCount(Integer.valueOf(String.valueOf(map.get("doc_count"))));
                    result.add(response);
                }
                break;
            case "staff":
                SearchField staffSearchField = new SearchField("relatedStaffInfos.staffName", FieldType.String, 0, 10, null);
                List<Map<String, Object>> staffStatistics = alarmEventManagementForESService.queryStatistics(reqs, staffSearchField);
                for(Map<String, Object> map:staffStatistics){
                    EventTypeResponse response = new EventTypeResponse();
                    response.setIndexName(String.valueOf(map.get("relatedStaffInfos.staffName")));
                    response.setIndexCount(Integer.valueOf(String.valueOf(map.get("doc_count"))));
                    result.add(response);
                }
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public FocalResultResponse queryFocalResult(RequestBean req) {
        FocalResultResponse result = new FocalResultResponse();
        List<QueryCondition_ES> reqs = new ArrayList<>();
        reqs.addAll(getBaseQueryParam(req));

        // 重点监管事件总数
        reqs.add(QueryCondition_ES.ge("alarmRiskLevel",3));
        long count =alarmEventManagementForESService.count(reqs);
        result.setTotalResultTotal(count);

        // 重点监管事件违规资产
        SearchField searchField = new SearchField("principalIp", FieldType.String, 0, 10000, null);
        try {
            List<Map<String, Object>> queryStatistics = alarmEventManagementForESService.queryStatistics(reqs, searchField);
            result.setDistinctDeviceIpValue(Long.valueOf(String.valueOf(queryStatistics.size())));
        }catch (Exception ex){
            result.setDistinctDeviceIpValue(Long.valueOf(0));
        }

        // 重点监管事件违规人员
        SearchField staffNoField = new SearchField("staffInfos.staffNo", FieldType.String, 0, 10000, null);
        try {
            List<Map<String, Object>> staffNameStatistics = alarmEventManagementForESService.queryStatistics(reqs, staffNoField);
            result.setDistinctStaffNoValue(Long.valueOf(String.valueOf(staffNameStatistics.size())));
        }catch (Exception ex){
            result.setDistinctStaffNoValue(Long.valueOf(0));
        }

        // 监管平台监测发现新增监管事件
        long total = alarmEventManagementForESService.count(getBaseQueryParam(req));
        result.setTotalAddResultTotal(total);

        // 特别重大事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("alarmRiskLevel",5));
        long count5 = alarmEventManagementForESService.count(reqs);
        result.setHigh5CountResultTotal(count5);

        // 重大事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("alarmRiskLevel",4));
        long count4 = alarmEventManagementForESService.count(reqs);
        result.setHigh4CountResultTotal(count4);

        // 较大事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("alarmRiskLevel",3));
        long count3 = alarmEventManagementForESService.count(reqs);
        result.setHigh3CountResultTotal(count3);

        // 已处置事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("alarmDealState",3));
        long processedCount = alarmEventManagementForESService.count(reqs);
        result.setProcessedCountResultTotal(processedCount);

        // 待处置事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("alarmDealState",0));
        long untreatedCount = alarmEventManagementForESService.count(reqs);
        result.setUntreatedCountResultTotal(untreatedCount);

        // 督促事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("isUrge",true));
        long urgeCount = alarmEventManagementForESService.count(reqs);
        result.setUrgeCountResultTotal(urgeCount);

        // 督办事件
        reqs.clear();
        reqs.addAll(getBaseQueryParam(req));
        reqs.add(QueryCondition_ES.eq("isSupervise",true));
        long superviseCount = alarmEventManagementForESService.count(reqs);
        result.setSuperviseCountResultTotal(superviseCount);
        return result;
    }
}
