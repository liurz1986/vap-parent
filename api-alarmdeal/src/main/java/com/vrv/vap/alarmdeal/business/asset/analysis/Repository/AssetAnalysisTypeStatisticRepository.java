package com.vrv.vap.alarmdeal.business.asset.analysis.Repository;

import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisTypeStatistic;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetAnalysisTypeStatisticRepository extends BaseRepository<AssetAnalysisTypeStatistic, String> {
}
