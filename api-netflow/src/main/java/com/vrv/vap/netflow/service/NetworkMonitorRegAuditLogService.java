package com.vrv.vap.netflow.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.model.NetworkMonitorRegAuditLog;

/**
 *@author sj
 *@date 2023/10/07
 *@description 网络监视器注册审批记录
 */
public interface NetworkMonitorRegAuditLogService extends BaseService<NetworkMonitorRegAuditLog> {


    //获取最后一次审批记录

    public  NetworkMonitorRegAuditLog getLastItem(String deviceId);
}
