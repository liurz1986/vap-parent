package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.NetworkMonitorReport;
import com.vrv.vap.base.BaseService;

/**
 *@author lilang
 *@date 2021/8/10
 *@description 监测器上报信息接口
 */
public interface NetworkMonitorReportService extends BaseService<NetworkMonitorReport> {

    void deleteByDeviceId(String deviceId);
}
