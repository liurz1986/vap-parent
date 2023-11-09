package com.vrv.vap.syslog.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author wh1107066
 * @date 2022/1/24 14:44
 */
public class SessionUtils {

    private static Logger logger = LoggerFactory.getLogger(SessionUtils.class);

    public static HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            logger.info("非Servlet容器， ServletRequestAttributes为空，user获取为空");
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        if (request == null) {
            logger.info("非Servlet容器,request 为空， user获取为空");
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.error("session为空，未登录!");
        }
        return session;
    }
}