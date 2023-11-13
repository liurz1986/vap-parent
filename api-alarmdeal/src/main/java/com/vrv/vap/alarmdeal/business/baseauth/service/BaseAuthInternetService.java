package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthInternet;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthInternetVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;


public interface BaseAuthInternetService extends BaseService<BaseAuthInternet, Integer> {


    Result<BaseAuthInternetVo> addAuthInt(BaseAuthInternetQueryVo baseAuthInternet);

    Result<String> delAuthInt(BaseAuthInternetQueryVo baseAuthInternet);

    Result<BaseAuthInternetVo> updateAuthInt(BaseAuthInternetQueryVo baseAuthInternetQueryVo);

    PageRes<BaseAuthInternetVo> intPage(BaseAuthInternetQueryVo baseAuthInternetQueryVo);

    void addAuthInterneByName(BaseAuthInternetVo baseAuthInternet);
}
