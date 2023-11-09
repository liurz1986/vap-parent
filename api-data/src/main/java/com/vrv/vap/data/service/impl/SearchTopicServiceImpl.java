package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.SearchTopicMapper;
import com.vrv.vap.data.model.SearchTopic;
import com.vrv.vap.data.service.SearchTopicService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class SearchTopicServiceImpl extends BaseServiceImpl<SearchTopic> implements SearchTopicService {

    @Resource
    private SearchTopicMapper searchTopicMapper;

    @Override
    public List<SearchTopic> findAll() {
        Example example = new Example(SearchTopic.class);
        example.createCriteria().andEqualTo("status", 0);
        return super.findByExample(example);
    }
}
