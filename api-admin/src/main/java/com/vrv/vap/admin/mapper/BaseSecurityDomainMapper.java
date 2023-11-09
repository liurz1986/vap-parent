package com.vrv.vap.admin.mapper;


import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseSecurityDomain;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.BaseSecurityDomainInfo;
import com.vrv.vap.admin.vo.DomainQuery;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BaseSecurityDomainMapper extends BaseMapper<BaseSecurityDomain> {
    List<BaseSecurityDomain> findSubDomainByCode(@Param("code") String code);
    List<BaseSecurityDomain> domainByTenant(@Param("roleId")Integer roleId);
    List<User> findTenantByCode(@Param("roleId")Integer roleId , @Param("code")String code);
    List<Map<String, Object>> getDomainIpSegmentTop();

    List<BaseSecurityDomainInfo> getDomainPage(@Param("domainQuery") DomainQuery domainQuery);

    Integer getDomainPageCount(@Param("domainQuery") DomainQuery domainQuery);

    Page<Map<String, Object>> getDomainBySecretLevel();
}
