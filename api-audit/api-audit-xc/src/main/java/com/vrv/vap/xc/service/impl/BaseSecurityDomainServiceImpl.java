package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.BaseSecurityDomainMapper;
import com.vrv.vap.xc.pojo.BaseSecurityDomain;
import com.vrv.vap.xc.service.IBaseSecurityDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseSecurityDomainServiceImpl extends ServiceImpl<BaseSecurityDomainMapper, BaseSecurityDomain> implements IBaseSecurityDomainService {

    public List<BaseSecurityDomain> findListByParentCode() {
        QueryWrapper<BaseSecurityDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_code", "f73f2234-8c10-4798-91cb-67dde736c767");
        return this.list(queryWrapper);
    }

    public List<BaseSecurityDomain> findAll(){
        return this.list();
    }
}
