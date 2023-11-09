package com.vrv.vap.alarmdeal.business.asset.datasync.repository;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetBookDetailRepository extends BaseRepository<AssetBookDetail, String> {
}
