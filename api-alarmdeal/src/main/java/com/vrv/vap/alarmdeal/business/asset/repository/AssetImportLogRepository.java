package com.vrv.vap.alarmdeal.business.asset.repository;


import com.vrv.vap.alarmdeal.business.asset.model.AssetImportLog;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetImportLogRepository extends BaseRepository<AssetImportLog, String> {
}
