package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.AreaLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.analysis.repository.AreaLocationRespository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;

@Service
public class AreaLocationService extends BaseServiceImpl<AreaLocation, String> {

	
	@Autowired
	private AreaLocationRespository  areaLocationRespository;
	
	@Override
	public AreaLocationRespository getRepository() {
		return areaLocationRespository;
	}

}
