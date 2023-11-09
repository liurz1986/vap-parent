package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.model.ObjectAnalyseModel;

import java.util.List;
import java.util.Map;

public interface ObjectAnalyseService {

    VData<List<Map<String, Object>>> queryVisitAppIpAndAcount(ObjectAnalyseModel param);

    VData<Map<String, List<String>>> queryUseIpAndAccount(ObjectAnalyseModel param);

    VData<Map<String, Object>> queryFileUpOrDown(ObjectAnalyseModel param);

    VData<Map<String, Object>> queryLoginAvg(ObjectAnalyseModel param);

    VData<List<Map<String, String>>> queryLoginDetailInfo(ObjectAnalyseModel param);

    VData<List<Map<String, Object>>> queryHistoryVisitProtoAndPort(ObjectAnalyseModel param);

    VData<List<Map<String, String>>> queryHistoryVisitAddress(ObjectAnalyseModel param);

    VData<Map<String, Object>> queryVisitCount(ObjectAnalyseModel param);

    VData<List<Map<String, Object>>> queryVisitAppNameIpSecret(ObjectAnalyseModel param);

    VData<List<Map<String, Object>>> queryVisitDeviceNameIpSecret(ObjectAnalyseModel param);

    VData<List<Map<String, Object>>> queryFileLocalBusiness(ObjectAnalyseModel param);

    VData<List<Map<String, String>>> queryFileImportList(ObjectAnalyseModel param);

    VData<Map<String, Object>> queryFileExportCount(ObjectAnalyseModel param);

    VData<List<Map<String, String>>> queryFileExportList(ObjectAnalyseModel param);

    VData<List<Map<String, Object>>> queryFileImportTrend(ObjectAnalyseModel param);

    VData<Map<String, List<Map<String, Object>>>> queryFileExportTrend(ObjectAnalyseModel param);

    VData<Map<String, Object>> queryOperationMethodCount(ObjectAnalyseModel param);

    VData<List<Map<String, Object>>> loginAvgTrend(ObjectAnalyseModel record);

    VData<Map<String, Object>> visitTrend(ObjectAnalyseModel record);

    VData<Map<String, Object>> queryFileUpOrDownTrend(ObjectAnalyseModel record);
}
