package com.vrv.vap.admin.service;


import com.vrv.vap.admin.model.WorkbenchIndividuation;
import com.vrv.vap.base.BaseService;

public interface WorkbenchIndividuationService  extends BaseService<WorkbenchIndividuation> {

    /**
     * 同过userId查询用户工作台配置
     */
    public  WorkbenchIndividuation findByUserId(Integer userId);
}
