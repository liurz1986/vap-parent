package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.DictionaryMapper;
import com.vrv.vap.admin.model.Dictionary;
import com.vrv.vap.admin.service.DictionaryService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/03/23.
 */
@Service
@Transactional
public class DictionaryServiceImpl extends BaseServiceImpl<Dictionary> implements DictionaryService {
    @Resource
    private DictionaryMapper dictionaryMapper;

}
