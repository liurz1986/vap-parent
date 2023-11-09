package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.BaseKoalOrg;

import java.util.List;
import java.util.Map;

public interface IBaseKoalOrgService extends IService<BaseKoalOrg> {

   List<Map<String,String>> getOrgKeyValuePair();
}
