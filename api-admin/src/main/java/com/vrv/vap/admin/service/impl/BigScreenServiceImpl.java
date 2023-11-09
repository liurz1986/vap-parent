package com.vrv.vap.admin.service.impl;

import javax.annotation.Resource;

import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vrv.vap.admin.mapper.BigScreenMapper;
import com.vrv.vap.admin.model.BigScreen;
import com.vrv.vap.admin.service.BigScreenService;

/**
 * Created by 涂美政
 */
@Service
@Transactional
public class BigScreenServiceImpl extends BaseServiceImpl<BigScreen> implements BigScreenService {
	@Resource
	private BigScreenMapper bigScreenMapper;

}
