package com.vrv.vap.alarmdeal.business.asset.online.Repository;

import com.vrv.vap.alarmdeal.business.asset.online.model.AssetOnLine;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetOnLineRepository  extends BaseRepository<AssetOnLine, String> {
}
