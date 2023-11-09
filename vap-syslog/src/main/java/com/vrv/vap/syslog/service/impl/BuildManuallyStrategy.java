package com.vrv.vap.syslog.service.impl;

import com.vrv.vap.common.utils.ip.IpUtils;
import com.vrv.vap.syslog.common.utils.UserUtil;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.model.UserdDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 手动构建策略模式
 *
 * @author wh1107066
 * @date 2021/7/2 14:49
 */
public class BuildManuallyStrategy extends AbstractBuildStrategy {
    private static Logger logger = LoggerFactory.getLogger(BuildManuallyStrategy.class);

    @Override
    public Map<String, Object> buildSyslogVO(SystemLog systemLog, String resResult) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            logger.info("非Servlet容器， ServletRequestAttributes为空，user获取为空");
        }
        String uri = "";
        HttpServletRequest request = attributes.getRequest();
        if (request == null) {
            logger.info("非Servlet容器,request 为空， user获取为空");
        } else {
            uri = request.getRequestURI();
        }
        Object user = UserUtil.getUser();
        Map<String, Object> logMap = new HashMap<>(1000);
        if (user != null) {
            UserdDTO userDto = new UserdDTO();
            BeanUtils.copyProperties(user, userDto);
            if (StringUtils.isNotEmpty(String.valueOf(userDto.getId()))) {
                logMap.put("user_id", String.valueOf(userDto.getIdcard()));
            }
            if (StringUtils.isNotBlank(userDto.getName())) {
                logMap.put("user_name", userDto.getName());
            }
            if (StringUtils.isNotBlank(userDto.getOrgName())) {
                logMap.put("organization_name", userDto.getOrgName());
            }
            if (userDto.getRoleName() != null && !userDto.getRoleName().isEmpty()) {
                logMap.put("role_name", userDto.getRoleName().get(0));
            }
            logMap.put("login_type", userDto.getLoginType());
        }
        logMap.put("request_ip", IpUtils.getIpAddr(request));
        logMap.put("request_url", uri);
        logMap.put("request_method", request.getMethod());
        logMap.put("response_result", resResult);
        // 手动构建id可能保存多次，由系统自动生成
        logMap.put("id", UUID.randomUUID().toString());
        extraProperties(systemLog, logMap);
        return logMap;
    }

}
