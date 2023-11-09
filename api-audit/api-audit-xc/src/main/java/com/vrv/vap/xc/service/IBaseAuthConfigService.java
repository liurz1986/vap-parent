package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.pojo.BaseAuthConfig;

import java.util.List;
import java.util.Map;

public interface IBaseAuthConfigService extends IService<BaseAuthConfig> {

    List<Map<String,Object>> getIpByType(ReportParam model);
}
