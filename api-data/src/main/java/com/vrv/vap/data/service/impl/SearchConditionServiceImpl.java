package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.SearchConditionMapper;
import com.vrv.vap.data.model.SearchCondition;
import com.vrv.vap.data.service.SearchConditionService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class SearchConditionServiceImpl extends BaseServiceImpl<SearchCondition> implements SearchConditionService {
    @Resource
    private SearchConditionMapper searchConditionMapper;

}
