package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.Base64Util;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.mapper.AuthLoginFieldMapper;
import com.vrv.vap.admin.model.AuthLoginField;
import com.vrv.vap.admin.service.AuthLoginFieldService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service(value = "authLoginFieldService")
public class AuthLoginFieldServiceImpl extends BaseServiceImpl<AuthLoginField> implements AuthLoginFieldService {

    private static final Logger log = LoggerFactory.getLogger(AuthLoginFieldServiceImpl.class);

    @Resource
    AuthLoginFieldMapper authLoginFieldMapper;

    private static final String IP_SPLIT = "~";

    public boolean validateLoginIp(Integer userId) {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = IPUtils.getIpAddress(request);
        Example example = new Example(AuthLoginField.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<AuthLoginField> authLoginFieldList = authLoginFieldMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(authLoginFieldList)) {
            for (AuthLoginField authLoginField : authLoginFieldList) {
                String authIp = authLoginField.getAuthFieldValue();
                if (authIp.indexOf(IP_SPLIT) > 0) {
                    String[] authIps = authIp.split(IP_SPLIT);
                    Long start = IPUtils.ip2int(authIps[0]);
                    Long end = IPUtils.ip2int(authIps[1]);
                    if (IPUtils.ip2int(ip) >= start && IPUtils.ip2int(ip) <= end) {
                        return true;
                    }
                } else {
                    if (ip.equals(authIp)) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean validateLoginMac(Integer userId,String userMac) {
        if (StringUtils.isEmpty(userMac)) {
            return true;
        }
        userMac = Base64Util.decoderByBase64(userMac,"UTF-8");
        log.info("mac is:" + userMac);
        Example example = new Example(AuthLoginField.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<AuthLoginField> authLoginFieldList = authLoginFieldMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(authLoginFieldList)) {
            for (AuthLoginField authLoginField : authLoginFieldList) {
                String authMac = authLoginField.getAuthFieldValue();
                if (StringUtils.isNotEmpty(userMac) && userMac.equals(authMac)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
