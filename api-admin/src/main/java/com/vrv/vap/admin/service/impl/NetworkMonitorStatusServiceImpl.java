package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.model.NetworkMonitorStatus;
import com.vrv.vap.admin.service.NetworkMonitorStatusService;
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
public class NetworkMonitorStatusServiceImpl extends BaseServiceImpl<NetworkMonitorStatus> implements NetworkMonitorStatusService {

}
