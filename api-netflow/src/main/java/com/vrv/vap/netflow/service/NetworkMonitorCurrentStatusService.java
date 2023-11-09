package com.vrv.vap.netflow.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.netflow.model.NetworkMonitorCurrentStatus;

import java.util.List;
import java.util.Map;

/**
 * @author sj
 * @date 2023/10/07
 *@description 网络监视器接口
 */
public interface NetworkMonitorCurrentStatusService extends BaseService<NetworkMonitorCurrentStatus> {
    public List<Map> getMonitorStatuses(String device_id,int pageSize);

   // public Map getLastStatus (String device_id);

    public NetworkMonitorCurrentStatus getCurrentStatus (String device_id);

}
