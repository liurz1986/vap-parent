package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.repository.TbConfRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
/**
 * TbConfServiceImpl
 * @author wd-pc
 *
 */
@Service
public class TbConfServiceImpl extends BaseServiceImpl<TbConf, String> implements TbConfService {

	@Autowired
	private TbConfRepository tbConfRepository;
	
	
	@Override
	public TbConfRepository getRepository() {
		return tbConfRepository;
	}

}
