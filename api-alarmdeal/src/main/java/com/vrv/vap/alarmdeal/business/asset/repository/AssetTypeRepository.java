package com.vrv.vap.alarmdeal.business.asset.repository;

import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AssetTypeRepository extends BaseRepository<AssetType, String> {

}
