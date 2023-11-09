package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.BaseSecurityDomainIpSegment;

import java.util.List;

public interface BaseSecurityDomainIpSegmentMapper extends BaseMapper<BaseSecurityDomainIpSegment> {

    List<BaseSecurityDomainIpSegment> findIpByCodes(List<String> list);
}
