package com.vrv.vap.netflow.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.netflow.model.NetworkMonitorReport;

/**
 *@author lilang
 *@date 2021/8/10
 *@description 监测器上报信息接口
 */
public interface NetworkMonitorReportService extends BaseService<NetworkMonitorReport> {

    void deleteByDeviceId(String deviceId);
}
