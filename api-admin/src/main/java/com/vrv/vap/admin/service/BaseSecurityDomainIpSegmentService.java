package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BaseSecurityDomainIpSegment;
import com.vrv.vap.base.BaseService;

public interface BaseSecurityDomainIpSegmentService extends BaseService<BaseSecurityDomainIpSegment> {

    Integer delete(BaseSecurityDomainIpSegment baseSecurityDomainIpSegment);

    BaseSecurityDomainIpSegment findByIp(String ip);

    void deleteAllDomainIp();
}
