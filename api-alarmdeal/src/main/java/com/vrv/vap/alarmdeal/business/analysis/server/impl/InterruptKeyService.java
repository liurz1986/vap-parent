package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.InterruptKey;
import com.vrv.vap.alarmdeal.business.analysis.repository.InterruptKeyRespository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;

@Service
public class InterruptKeyService extends BaseServiceImpl<InterruptKey, String> {

	
	@Autowired
	private InterruptKeyRespository  interruputKeyRespository;
	
	@Override
	public InterruptKeyRespository getRepository() {
		return interruputKeyRespository;
	}

}
