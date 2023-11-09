package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.BaseKoalOrgMapper;
import com.vrv.vap.xc.pojo.BaseKoalOrg;
import com.vrv.vap.xc.service.IBaseKoalOrgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BaseKoalOrgServiceImpl extends ServiceImpl<BaseKoalOrgMapper, BaseKoalOrg> implements IBaseKoalOrgService {

    @Resource
    private BaseKoalOrgMapper baseKoalOrgMapper;
    @Override
    public List<Map<String, String>> getOrgKeyValuePair() {
        return baseKoalOrgMapper.getOrgKeyValuePair();
    }
}
