package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.model.NetworkMonitor;
import com.vrv.vap.admin.service.NetworkMonitorService;
import com.vrv.vap.base.BaseServiceImpl;
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
