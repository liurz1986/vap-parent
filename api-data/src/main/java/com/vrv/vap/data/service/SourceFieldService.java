package com.vrv.vap.data.service;

import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.base.BaseService;

import java.util.List;


public interface SourceFieldService extends BaseService<SourceField>{

    List<SourceField> findAllBySourceId(Integer sourceId);


}
