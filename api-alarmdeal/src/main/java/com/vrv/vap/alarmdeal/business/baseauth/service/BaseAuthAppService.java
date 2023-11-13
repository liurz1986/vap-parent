package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthApp;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthAppVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthAppQueryVo;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;


public interface BaseAuthAppService extends BaseService<BaseAuthApp, Integer> {

    PageRes<BaseAuthAppVo> getPager(BaseAuthAppQueryVo baseAuthAppQueryVo);


    Result<BaseAuthAppVo> addAuthApp(BaseAuthAppVo baseAuthAppVo);


    Result<BaseAuthAppVo> updateAuthApp(BaseAuthAppVo baseAuthAppVo);

    Result<String> delAuthApp(BaseAuthAppVo baseAuthAppVo);

    void addAuthAppByName(BaseAuthAppVo baseAuthApp);
}
