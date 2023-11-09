package com.vrv.vap.admin.service;


import com.vrv.vap.admin.model.License;
import com.vrv.vap.base.BaseService;

public interface LicenseService  extends BaseService<License> {
    void updateResourceByLicense();

    String getHttpEncriptLicense();

    void saveAuthenticationConfig();

    void  forbiddenResource();
}