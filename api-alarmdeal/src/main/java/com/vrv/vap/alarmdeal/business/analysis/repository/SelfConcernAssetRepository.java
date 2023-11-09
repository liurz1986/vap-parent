package com.vrv.vap.alarmdeal.business.analysis.repository;

import org.springframework.stereotype.Repository;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.jpa.basedao.BaseRepository;

@Repository
public interface SelfConcernAssetRepository  extends BaseRepository<SelfConcernAsset, String>  {

}
