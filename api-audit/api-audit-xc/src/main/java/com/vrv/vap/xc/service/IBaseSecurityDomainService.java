package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.BaseSecurityDomain;

import java.util.List;

public interface IBaseSecurityDomainService extends IService<BaseSecurityDomain> {
    List<BaseSecurityDomain> findListByParentCode();

    List<BaseSecurityDomain> findAll();
}
