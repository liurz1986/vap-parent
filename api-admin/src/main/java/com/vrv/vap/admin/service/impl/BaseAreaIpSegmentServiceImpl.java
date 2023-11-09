package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.mapper.BaseAreaIpSegmentMapper;
import com.vrv.vap.admin.model.BaseAreaIpSegment;
import com.vrv.vap.admin.service.BaseAreaIpSegmentService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by Main on 2019/07/24.
 */
@Service
@Transactional
public class BaseAreaIpSegmentServiceImpl extends BaseServiceImpl<BaseAreaIpSegment> implements BaseAreaIpSegmentService {
    @Resource
    private BaseAreaIpSegmentMapper baseAreaIpSegmentMapper;

    @Override
    public BaseAreaIpSegment findByIp(String ip) {
        long unit =IPUtils.ip2int(ip);
        Example example = new Example(BaseAreaIpSegment.class);
        example.setOrderByClause(" start_ip_num DESC, end_ip_num ASC");
        example.createCriteria()
                .andLessThanOrEqualTo("startIpNum",unit)
                .andGreaterThanOrEqualTo("endIpNum",unit);
        List<BaseAreaIpSegment> result =  baseAreaIpSegmentMapper.selectByExample(example);
        if(result==null||result.size()==0){
            return null;
        }
        return result.get(0);
    }
}
