package com.vrv.vap.alarmdeal.business.asset.online.Repository;

import com.vrv.vap.alarmdeal.business.asset.online.model.AssetChange;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetChangeRepository extends BaseRepository<AssetChange, String> {
}
