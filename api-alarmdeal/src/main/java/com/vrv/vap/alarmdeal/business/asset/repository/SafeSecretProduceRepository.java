package com.vrv.vap.alarmdeal.business.asset.repository;


import com.vrv.vap.alarmdeal.business.asset.model.SafeSecretProduce;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 编排
 */
@Repository
public interface SafeSecretProduceRepository extends BaseRepository<SafeSecretProduce, String> {
}
