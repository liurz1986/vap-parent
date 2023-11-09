package com.vrv.vap.admin.service;


import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseSecurityDomain;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.BaseSecurityDomainInfo;
import com.vrv.vap.admin.vo.BaseSecurityDomainVO;
import com.vrv.vap.admin.vo.DomainQuery;
import com.vrv.vap.base.BaseService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface BaseSecurityDomainService extends BaseService<BaseSecurityDomain> {
    List<BaseSecurityDomainVO> findSubDomainByCode(String guid);

    List<BaseSecurityDomain> findSubAllDomainByCode(String guid);

    Integer deleteDomainByCode(String code);

    List<BaseSecurityDomain>  domainByTenant(Integer roleId);

    List<User> findTenantByCode(Integer roleId, String code);

    void  initSubCode();

    BaseSecurityDomain generateSubCode(BaseSecurityDomain baseKoalOrg);

    void deleteAllDomain();

    List<BaseSecurityDomainVO> fillChildren(List<BaseSecurityDomain> securityDomainList);

    void cacheDomain();

    void sendChangeMessage();

    BaseSecurityDomain getNetExtCode(Map<String, String> map);

    List<BaseSecurityDomainInfo> getDomainPage(DomainQuery domainQuery);

    Integer getDomainPageCount(DomainQuery domainQuery);

    Page<Map<String, Object>> getDomainBySecretLevel();

    List<Map<String,Object>> getDomainCountTop10();

    List<Map<String,Object>> getAllDomainIps();
}
