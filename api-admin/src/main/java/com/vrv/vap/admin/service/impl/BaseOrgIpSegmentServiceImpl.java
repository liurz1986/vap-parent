package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.BaseOrgIpSegmentMapper;
import com.vrv.vap.admin.model.BaseOrgIpSegment;
import com.vrv.vap.admin.service.BaseOrgIpSegmentService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/09/13.
 */
@Service
@Transactional
public class BaseOrgIpSegmentServiceImpl extends BaseServiceImpl<BaseOrgIpSegment> implements BaseOrgIpSegmentService {
    @Resource
    private BaseOrgIpSegmentMapper baseOrgIpSegmentMapper;

    @Override
    public void deleteAllIpSegment() {
        List<BaseOrgIpSegment> ipSegmentList = this.findAll();
        if (CollectionUtils.isNotEmpty(ipSegmentList)) {
            for (BaseOrgIpSegment ipSegment : ipSegmentList) {
                this.deleteById(ipSegment.getId());
            }
        }
    }
}
