package com.vrv.vap.alarmdeal.business.asset.analysis.service.impl;

import com.vrv.vap.alarmdeal.business.asset.analysis.Repository.AssetAnalysisOnLineStatisticRepository;
import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisOnLineStatistic;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisOnLineStatisticService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetAnalysisOnLineStatisticServiceImpl extends BaseServiceImpl<AssetAnalysisOnLineStatistic, String> implements AssetAnalysisOnLineStatisticService {
    @Autowired
    private AssetAnalysisOnLineStatisticRepository assetAnalysisOnLineStatisticRepository;
    @Override
    public BaseRepository<AssetAnalysisOnLineStatistic, String> getRepository() {
        return assetAnalysisOnLineStatisticRepository;
    }
}
