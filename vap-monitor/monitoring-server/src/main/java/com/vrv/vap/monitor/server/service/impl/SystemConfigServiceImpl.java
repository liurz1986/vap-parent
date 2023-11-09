package com.vrv.vap.monitor.server.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.monitor.server.mapper.SystemConfigMapper;
import com.vrv.vap.monitor.server.model.SystemConfig;
import com.vrv.vap.monitor.server.service.SystemConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SystemConfigServiceImpl extends BaseServiceImpl<SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    private static String LOGIN_PAGE = "_LOGIN_PAGE";

    @Override
    public SystemConfig findByConfId(String confId) {
        /*if (configCache.containsKey(confId)) {
            return configCache.get(confId);
        }*/
        SystemConfig config = systemConfigMapper.selectByPrimaryKey(confId);
        if (config == null) {
            config = new SystemConfig();
            config.setConfId(confId);
            this.save(config);
        }
        return config;
    }

    @Override
    public Integer updateSelective(SystemConfig config) {
       SystemConfig systemConfig = new SystemConfig();
       systemConfig.setConfId(config.getConfId());
       List<SystemConfig> list =  systemConfigMapper.select(systemConfig);
       if(CollectionUtils.isEmpty(list)){
            return this.save(config);
       }
       else {
           config.setConfTime(new Date());
           return super.updateSelective(config);
       }
    }

}
