package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.BaseSecurityDomainIpSegmentMapper;
import com.vrv.vap.xc.pojo.BaseSecurityDomainIpSegment;
import com.vrv.vap.xc.service.IBaseSecurityDomainIpSegmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BaseSecurityDomainIpSegmentServiceImpl extends ServiceImpl<BaseSecurityDomainIpSegmentMapper, BaseSecurityDomainIpSegment> implements IBaseSecurityDomainIpSegmentService {

    @Resource
    private BaseSecurityDomainIpSegmentMapper baseSecurityDomainIpSegmentMapper;
    @Override
    public List<BaseSecurityDomainIpSegment> findIpByCodes(List<String> codeList) {
        return baseSecurityDomainIpSegmentMapper.findIpByCodes(codeList);
    }
}
