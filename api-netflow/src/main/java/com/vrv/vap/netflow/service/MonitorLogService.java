package com.vrv.vap.netflow.service;

import java.util.Map;

/**
 *@author lilang
 *@date 2021/8/12
 *@description
 */
public interface MonitorLogService {

    Integer updateStatusNew(Map map);

    Integer updateStatus(Map map);

    Integer register(Map map);
}
