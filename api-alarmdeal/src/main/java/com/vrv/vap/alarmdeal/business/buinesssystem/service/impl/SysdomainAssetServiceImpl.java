package com.vrv.vap.alarmdeal.business.buinesssystem.service.impl;

import com.vrv.vap.alarmdeal.business.buinesssystem.model.BuinessSystemAsset;
import com.vrv.vap.alarmdeal.business.buinesssystem.repository.SysdomainAssetRepository;
import com.vrv.vap.alarmdeal.business.buinesssystem.service.SysdomainAssetService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysdomainAssetServiceImpl extends BaseServiceImpl<BuinessSystemAsset, String> implements SysdomainAssetService {

    @Autowired
    private SysdomainAssetRepository sysdomainAssetRepository;
    @Override
    public BaseRepository<BuinessSystemAsset, String> getRepository() {
        return sysdomainAssetRepository;
    }
}
