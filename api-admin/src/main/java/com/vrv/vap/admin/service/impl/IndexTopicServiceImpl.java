package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.IndexTopicMapper;
import com.vrv.vap.admin.model.IndexTopic;
import com.vrv.vap.admin.service.IndexTopicService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class IndexTopicServiceImpl extends BaseServiceImpl<IndexTopic> implements IndexTopicService {

    @Resource
    private IndexTopicMapper indexTopicMapper;

    @Override
    public void setDefaultTopic() {
        IndexTopic queryIndexTopic = new IndexTopic();
        queryIndexTopic.setGroupDefault(1);
        queryIndexTopic.setStatus("01");
        List<IndexTopic> topicList = indexTopicMapper.select(queryIndexTopic);
        if(topicList==null || topicList.size() == 0){
            queryIndexTopic = new IndexTopic();
            queryIndexTopic.setStatus("01");
            queryIndexTopic.setType("01");
            List<IndexTopic> availList = indexTopicMapper.select(queryIndexTopic);
            if(availList == null || availList.size() == 0)
                return;
            IndexTopic indexTopic = availList.get(0);
            indexTopicMapper.updateByPrimaryKey(indexTopic);
        }

    }
}
