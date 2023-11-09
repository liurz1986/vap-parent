package com.vrv.vap.alarmdeal.business.asset.repository;

import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetStealLeakValue;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 资产额外属性
 * @author wd-pc
 *
 */
@Repository
public interface AssetStealLeakValueRepository extends 	BaseRepository<AssetStealLeakValue, String> {

}
