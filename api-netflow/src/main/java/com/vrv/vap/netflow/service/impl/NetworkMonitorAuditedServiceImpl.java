package com.vrv.vap.netflow.service.impl;


import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.service.NetworkMonitorAuditedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author sj
 * @date 2023/10/07
 * @description 网络监视器已审批
 */
@Service
@Transactional
public class NetworkMonitorAuditedServiceImpl extends BaseServiceImpl<NetworkMonitorAudited> implements NetworkMonitorAuditedService {


    @Override
    public NetworkMonitorAudited getItem(String deviceId) {
        List<NetworkMonitorAudited> logList = this.findByProperty(NetworkMonitorAudited.class, "deviceId", deviceId);
        if(logList!=null&&!logList.isEmpty()){
            return logList.get(0);
        }
        return null;
    }
}
