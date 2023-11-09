package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BaseSecurityDomainService;
import com.vrv.vap.admin.service.ScreenDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScreenDataServiceImpl implements ScreenDataService {

    @Autowired
    BaseSecurityDomainService baseSecurityDomainService;

    @Autowired
    BaseKoalOrgService baseKoalOrgService;

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("domainNum", baseSecurityDomainService.findAll().size());
        resMap.put("orgNum", baseKoalOrgService.findAll().size());
        return resMap;
    }
}
