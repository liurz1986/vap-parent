package com.vrv.vap.alarmdeal.business.asset.service.impl;


import com.vrv.vap.alarmdeal.business.asset.model.AssetImportLog;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetImportLogRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetImportLogService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetImportLogServiceImpl extends BaseServiceImpl<AssetImportLog, String> implements AssetImportLogService {

    @Autowired
    private AssetImportLogRepository assetImportLogRepository;

    @Override
    public BaseRepository<AssetImportLog, String> getRepository() {
        return assetImportLogRepository;
    }

}
