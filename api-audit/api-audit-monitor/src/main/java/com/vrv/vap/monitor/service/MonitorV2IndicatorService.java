package com.vrv.vap.monitor.service;

import com.vrv.vap.monitor.entity.Monitor2Indicator;
import com.vrv.vap.monitor.vo.Monitor2DataQuery;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface MonitorV2IndicatorService extends BaseCommonService<Monitor2Indicator> {

    List<Monitor2Indicator> getMonitor2Indicators();

    Map<String, Object> monitorExample(@RequestBody Monitor2DataQuery monitor2DataQuery);
}
