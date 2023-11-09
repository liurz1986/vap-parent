package com.vrv.vap.alarmdeal.business.asset.analysis.service.impl;

import com.vrv.vap.alarmdeal.business.asset.analysis.Repository.AssetAnalysisTypeStatisticRepository;
import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisTypeStatistic;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisTypeStatisticService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetAnalysisTypeStatisticServiceImpl extends BaseServiceImpl<AssetAnalysisTypeStatistic, String> implements AssetAnalysisTypeStatisticService {

    @Autowired
    private AssetAnalysisTypeStatisticRepository assetAnalysisTypeStatisticRepository;
    @Override
    public BaseRepository<AssetAnalysisTypeStatistic, String> getRepository() {
        return this.assetAnalysisTypeStatisticRepository;
    }
}
