package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.alarmdeal.business.analysis.repository.SelfConcernAssetRepository;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
public class SelfConcernAssetServiceImpl extends BaseServiceImpl<SelfConcernAsset, String> implements SelfConcernAssetService {

	@Autowired
	SelfConcernAssetRepository  selfConcernAssetRepository;
	
	@Override
	public BaseRepository<SelfConcernAsset, String> getRepository() {
		// TODO Auto-generated method stub
		return selfConcernAssetRepository;
	}


	@Override
	public List<SelfConcernAsset> getSelfConcernAssets(SelfConcernAsset selfConcernAsset) {
		List<QueryCondition> queryConditions=new ArrayList<>();
		queryConditions.add(QueryCondition.eq("userId",selfConcernAsset.getUserId()));
		queryConditions.add(QueryCondition.eq("type",selfConcernAsset.getType()));
		List<SelfConcernAsset> all = this.findAll(queryConditions);
		return all;
	}

	@Override
	public Long getCountByUserIP(String userId, Integer type,String ip) {
		List<QueryCondition> queryConditions=new ArrayList<>();
		queryConditions.add(QueryCondition.eq("userId",userId));
		queryConditions.add(QueryCondition.eq("type",type));
		queryConditions.add(QueryCondition.eq("ip",ip));
		long count = this.count(queryConditions);
		return count;
	}
}
