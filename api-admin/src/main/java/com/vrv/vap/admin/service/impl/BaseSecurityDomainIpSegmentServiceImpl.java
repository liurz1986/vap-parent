package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.mapper.BaseSecurityDomainIpSegmentMapper;
import com.vrv.vap.admin.model.BaseSecurityDomainIpSegment;
import com.vrv.vap.admin.service.BaseSecurityDomainIpSegmentService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class BaseSecurityDomainIpSegmentServiceImpl extends BaseServiceImpl<BaseSecurityDomainIpSegment> implements BaseSecurityDomainIpSegmentService {

    @Resource
    private BaseSecurityDomainIpSegmentMapper baseSecurityDomainIpSegmentMapper;

    @Override
    public Integer delete(BaseSecurityDomainIpSegment baseSecurityDomainIpSegment) {
        return this.baseMapper.delete(baseSecurityDomainIpSegment);
    }

        @Override
        public BaseSecurityDomainIpSegment findByIp (String ip){
            long unit =IPUtils.ip2int(ip);
            Example example = new Example(BaseSecurityDomainIpSegment.class);
            example.setOrderByClause(" start_ip_num DESC, end_ip_num ASC");
            example.createCriteria()
                   .andLessThanOrEqualTo("startIpNum",unit)
                   .andGreaterThanOrEqualTo("endIpNum",unit);
           List<BaseSecurityDomainIpSegment> result =  baseSecurityDomainIpSegmentMapper.selectByExample(example);
              if(result==null||result.size()==0){
                 return null;
              }
            return result.get(0);
           }

        /**
         * 删除所有安全域ip
         */
        public void deleteAllDomainIp() {
            List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegmentList = baseSecurityDomainIpSegmentMapper.selectAll();
            if (CollectionUtils.isNotEmpty(baseSecurityDomainIpSegmentList)) {
                for (BaseSecurityDomainIpSegment baseSecurityDomainIpSegment : baseSecurityDomainIpSegmentList) {
                    baseSecurityDomainIpSegmentMapper.deleteByPrimaryKey(baseSecurityDomainIpSegment.getId());
                }
            }
        }
    }
