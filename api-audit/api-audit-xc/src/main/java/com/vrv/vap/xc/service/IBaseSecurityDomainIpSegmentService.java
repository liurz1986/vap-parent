package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.BaseSecurityDomainIpSegment;

import java.util.List;

public interface IBaseSecurityDomainIpSegmentService extends IService<BaseSecurityDomainIpSegment> {
    List<BaseSecurityDomainIpSegment> findIpByCodes(List<String> codeList);
}
