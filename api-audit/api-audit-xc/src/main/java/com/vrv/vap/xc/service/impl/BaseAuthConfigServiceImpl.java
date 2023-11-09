package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.BaseAuthConfigMapper;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.pojo.BaseAuthConfig;
import com.vrv.vap.xc.service.IBaseAuthConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BaseAuthConfigServiceImpl extends ServiceImpl<BaseAuthConfigMapper, BaseAuthConfig> implements IBaseAuthConfigService {
    @Resource
    private BaseAuthConfigMapper baseAuthConfigMapper;
    @Override
    public List<Map<String, Object>> getIpByType(ReportParam model) {
        return baseAuthConfigMapper.getIpByType(model);
    }
}
