package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.ConfAttackMapping;
import com.vrv.vap.alarmdeal.business.analysis.repository.ConfAttackMappingRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.ConfAttackMappingService;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
@Service
public class ConfAttackMappingServiceImpl extends BaseServiceImpl<ConfAttackMapping, Integer> implements ConfAttackMappingService {

	@Autowired
	private ConfAttackMappingRespository confAttackMappingRespository;
	
	
	@Override
	public ConfAttackMappingRespository getRepository() {
		return confAttackMappingRespository;
	}

}
