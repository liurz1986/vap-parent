package com.vrv.vap.alarmdeal.business.asset.analysis.service.impl;

import com.vrv.vap.alarmdeal.business.asset.analysis.Repository.AssetAnalysisTotalStatisticRepository;
import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisTotalStatistic;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisTotalStatisticService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetAnalysisTotalStatisticServiceImpl  extends BaseServiceImpl<AssetAnalysisTotalStatistic, String> implements AssetAnalysisTotalStatisticService {
    @Autowired
    private AssetAnalysisTotalStatisticRepository assetAnalysisTotalStatisticRepository;
    @Override
    public BaseRepository<AssetAnalysisTotalStatistic, String> getRepository() {
        return assetAnalysisTotalStatisticRepository;
    }
}
