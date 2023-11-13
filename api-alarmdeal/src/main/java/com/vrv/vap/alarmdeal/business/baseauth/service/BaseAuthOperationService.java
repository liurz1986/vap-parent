package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthOperation;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthoOperationVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;


public interface BaseAuthOperationService extends BaseService<BaseAuthOperation, Integer> {


    Result<BaseAuthoOperationVo> addAuthOperation(BaseAuthoOperationVo baseAuthOperation);

    Result<BaseAuthoOperationVo> updateAuthOperation(BaseAuthoOperationVo baseAuthOperation);

    Result<String> delAuthOperation(BaseAuthOperation baseAuthOperation);

    PageRes<BaseAuthoOperationVo> operationPage(BaseAuthInternetQueryVo baseAuthInternetQueryVo);
}
