package com.vrv.vap.common.utils;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wh1107066
 */
public class ContextHolderUtil {

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;

    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return response;

    }

    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;
    }

    /**
     *  传入false操作
     * @param autoCreateFalse 传入false
     * @return HttpSession
     */
    public static HttpSession getSession(boolean autoCreateFalse) {
        HttpSession session = getRequest().getSession(autoCreateFalse);
        return session;
    }
}
