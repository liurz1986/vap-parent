package com.vrv.vap.alarmdeal.business.flow.processdef.service;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.FlowConfig;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketInnerForm;
import com.vrv.vap.alarmdeal.business.flow.processdef.repository.FlowConfigRepository;
import com.vrv.vap.alarmdeal.business.flow.processdef.repository.FlowInnerFormRepository;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowConfigService extends BaseServiceImpl<FlowConfig, String> {

	@Autowired
	FlowConfigRepository flowConfigRepository;
	
	@Override
	public BaseRepository<FlowConfig, String> getRepository() {
		return flowConfigRepository;
	}

}
