package com.vrv.vap.monitor.server.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.monitor.server.model.SystemConfig;

public interface SystemConfigService extends BaseService<SystemConfig> {

    public SystemConfig findByConfId(String confId);

    Integer updateSelective(SystemConfig config);
}
