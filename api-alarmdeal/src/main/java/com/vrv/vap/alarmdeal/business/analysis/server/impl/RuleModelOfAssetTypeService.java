package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.RuleModelOfAssetType;
import com.vrv.vap.alarmdeal.business.analysis.repository.RuleModelOfAssetTypeRespository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleModelOfAssetTypeService extends BaseServiceImpl<RuleModelOfAssetType,String> {

    @Autowired
    private RuleModelOfAssetTypeRespository ruleModelOfAssetTypeRespository;


    @Override
    public RuleModelOfAssetTypeRespository getRepository() {
        return ruleModelOfAssetTypeRespository;
    }
}
