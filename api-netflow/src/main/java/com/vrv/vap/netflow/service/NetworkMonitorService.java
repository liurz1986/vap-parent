package com.vrv.vap.netflow.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.netflow.model.NetworkMonitor;

/**
 *@author lilang
 *@date 2021/8/10
 *@description 网络监视器接口
 */
public interface NetworkMonitorService extends BaseService<NetworkMonitor> {

    void deleteByDeviceId(String deviceId);

    String getValueFromMapStringByKey(String key , String original);

    public NetworkMonitor getLastItem(String deviceId);
}
