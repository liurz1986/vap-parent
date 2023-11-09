package com.vrv.vap.xc.service;


import com.vrv.vap.xc.model.PageModel;

import java.util.List;
import java.util.Map;

public interface XgsDataChannelService {

    Map<String, Object> esDataSummary();

    Map<String, Object> dataCount();

    List<Map<String, Object>> dataLevel(PageModel model);

    Map<String, Object> dataTop();

    List<Map<String, Object>> dataTrend();

    void esDataSendKafka();

    void test();

    List<Map<String, Object>> dataKind(PageModel model);

    List<Map<String, Object>> dataAlert(PageModel model);

    List<Map<String, String>> dataAlertLastInfo(PageModel model);
}