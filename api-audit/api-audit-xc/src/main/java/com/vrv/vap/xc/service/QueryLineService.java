package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.model.ObjectAnalyseModel;
import com.vrv.vap.xc.model.PortraitModel;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface QueryLineService {
    VData<Map<String, Object>> queryUseIp(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryUseName(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryVisitAppIpAndAcount(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryVisitAppNameIpSecret(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryHistoryVisitAddress(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryHistoryVisitProtoAndPort(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryFileLocalBusiness(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryFileImportTrend(ObjectAnalyseModel param);
    VData<Map<String, Object>> queryFileExportTrend(ObjectAnalyseModel param);

    VData<Map<String, Object>> generalAnalytics(PortraitModel model);
}
