package com.vrv.vap.netflow.service.impl;


import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.netflow.model.NetworkMonitor;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.model.NetworkMonitorRegAuditLog;
import com.vrv.vap.netflow.service.NetworkMonitorAuditedService;
import com.vrv.vap.netflow.service.NetworkMonitorRegAuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * @author sj
 * @date 2023/10/07
 * @description 网络监视器已审批
 */
@Service
@Transactional
public class NetworkMonitorRegAuditLogServiceImpl extends BaseServiceImpl<NetworkMonitorRegAuditLog> implements NetworkMonitorRegAuditLogService {


    @Override
    public NetworkMonitorRegAuditLog getLastItem(String deviceId) {

        List<NetworkMonitorRegAuditLog> logList = this.findByProperty(NetworkMonitorRegAuditLog.class, "deviceId", deviceId);
        if(logList!=null&&!logList.isEmpty()){
            logList.sort(Comparator.comparing(NetworkMonitorRegAuditLog::getAuditTime).reversed());
            return logList.get(0);
        }

        return null;
    }
}
