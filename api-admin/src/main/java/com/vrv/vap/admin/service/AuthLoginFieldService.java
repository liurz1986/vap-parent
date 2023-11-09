package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.AuthLoginField;
import com.vrv.vap.base.BaseService;

public interface AuthLoginFieldService extends BaseService<AuthLoginField> {

    public boolean validateLoginIp(Integer userId);

    public boolean validateLoginMac(Integer userId,String userMac);
}
