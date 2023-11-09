package com.vrv.vap.netflow.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;

/**
 *@author sj
 *@date 2023/10/07
 *@description 网络监视器已审批接口
 */
public interface NetworkMonitorAuditedService extends BaseService<NetworkMonitorAudited> {

    public NetworkMonitorAudited getItem(String deviceId);
}
