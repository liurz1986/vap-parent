package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.IndexTopic;
import com.vrv.vap.base.BaseService;

public interface IndexTopicService extends BaseService<IndexTopic> {

    void  setDefaultTopic();
}
