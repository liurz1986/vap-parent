package com.vrv.vap.monitor.server.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.monitor.server.model.NetworkMonitor;
import com.vrv.vap.monitor.server.service.NetworkMonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lilang
 * @date 2021/8/10
 * @description 网络监视器实现类
 */
@Service
@Transactional
public class NetworkMonitorServiceImpl extends BaseServiceImpl<NetworkMonitor> implements NetworkMonitorService {

}
