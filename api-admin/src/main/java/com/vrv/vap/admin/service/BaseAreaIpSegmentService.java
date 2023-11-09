package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BaseAreaIpSegment;
import com.vrv.vap.base.BaseService;

/**
 * Created by Main on 2019/07/24.
 */
public interface BaseAreaIpSegmentService extends BaseService<BaseAreaIpSegment> {

    BaseAreaIpSegment findByIp(String ip);

}
