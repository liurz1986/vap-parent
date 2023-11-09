package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.vrv.vap.alarmdeal.business.flow.core.repository.BusinessTaskCandidateRepository;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskCandidate;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessTaskCandidateService extends BaseServiceImpl<BusinessTaskCandidate, String> {

	@Autowired
	private BusinessTaskCandidateRepository businessTaskCandidateRepository;

	@Override
	public BusinessTaskCandidateRepository getRepository() {
		return businessTaskCandidateRepository;
	}
	
}
