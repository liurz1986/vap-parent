package com.vrv.vap.alarmdeal.business.asset.repository;

import com.vrv.vap.alarmdeal.business.asset.model.AssetOperationLog;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetOperationLogRepository extends BaseRepository<AssetOperationLog, String>  {

}
