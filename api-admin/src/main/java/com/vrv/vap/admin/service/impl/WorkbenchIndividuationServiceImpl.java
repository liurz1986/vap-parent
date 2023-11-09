package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.mapper.WorkbenchIndividuationMapper;
import com.vrv.vap.admin.model.WorkbenchIndividuation;
import com.vrv.vap.admin.service.WorkbenchIndividuationService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class WorkbenchIndividuationServiceImpl extends BaseServiceImpl<WorkbenchIndividuation> implements WorkbenchIndividuationService {

    @Autowired
    WorkbenchIndividuationMapper workbenchIndividuationMapper;

    /**
     * 同过userId查询用户工作台配置
     */
    public  WorkbenchIndividuation findByUserId(Integer userId){
        Example example = new Example(WorkbenchIndividuation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<WorkbenchIndividuation> individuationList = workbenchIndividuationMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(individuationList)) {
            return individuationList.get(0);
        }
        return null;
    }



}
