package com.vrv.vap.netflow.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.netflow.model.NetworkMonitorStatus;
import com.vrv.vap.netflow.service.NetworkMonitorStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lilang
 * @date 2021/8/10
 * @description 网络监视器实现类
 */
@Service
@Transactional
public class NetworkMonitorStatusServiceImpl extends BaseServiceImpl<NetworkMonitorStatus> implements NetworkMonitorStatusService {

}
