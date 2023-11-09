package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.StoryboardMapper;
import com.vrv.vap.admin.model.Storyboard;
import com.vrv.vap.admin.service.StoryboardService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/10/24.
 */
@Service
@Transactional
public class StoryboardServiceImpl extends BaseServiceImpl<Storyboard> implements StoryboardService {
    @Resource
    private StoryboardMapper storyboardMapper;

	@Override
	public void cancelFirstPage() {
	   storyboardMapper.cancelFirstPage();	
	}
}
