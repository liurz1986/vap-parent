package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.UserDomain;
import com.vrv.vap.base.BaseService;


public interface UserDomainService extends BaseService<UserDomain> {
    void deleteByUserIds(String[] split);

    void saveUserDomains(String domainCode,Integer userId);

    void deleteAllUserDomain();
}
