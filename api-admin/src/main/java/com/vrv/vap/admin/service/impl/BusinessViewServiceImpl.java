package com.vrv.vap.admin.service.impl;

import javax.annotation.Resource;

import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vrv.vap.admin.mapper.BusinessViewMapper;
import com.vrv.vap.admin.model.BusinessView;
import com.vrv.vap.admin.service.BusinessViewService;

/**
 * Created by 涂美政
 */
@Service
@Transactional
public class BusinessViewServiceImpl extends BaseServiceImpl<BusinessView> implements BusinessViewService {
	@Resource
	private BusinessViewMapper businessViewMapper;

}
