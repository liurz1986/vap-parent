package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Storyboard;
import com.vrv.vap.base.BaseMapper;

public interface StoryboardMapper extends BaseMapper<Storyboard> {

	void cancelFirstPage();
}