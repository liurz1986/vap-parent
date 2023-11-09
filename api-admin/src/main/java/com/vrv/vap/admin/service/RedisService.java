package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.*;
import com.vrv.vap.common.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {


    boolean hasRoleResource(String roleId);

    void setRoleResource(String roleId, Set<String> resourcesSet );

    Set<String>  getRoleResource(String roleId );

    void clearRoleResource(int roleId);

    String genToken(User user,Integer appId);

    Object validateToken(String token);


    void updateOrAddSecurityDomainResource(BaseSecurityDomain baseSecurityDomain);
    void deleteSecurityDomainResource(String guid);
    BaseSecurityDomain getSecurityDomainResource(String guid);


    void updateOrAddSecurityDomainIpSegmentResource(BaseSecurityDomainIpSegment baseSecurityDomainIpSegment);
    void deleteSecurityDomainIpSegmentResource(String id);
    BaseSecurityDomainIpSegment getSecurityDomainSegmentIpResource(Long ipNumber);



}
