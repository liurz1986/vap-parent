package com.vrv.vap.server.zuul.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author huipei.x
 * @data 创建时间 2020/9/4
 * @description 类说明 :
 */
public class IpHttpServletRequest extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public IpHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if("x-forwarded-for".equals(name)){
            value="";
        }
        return value;
    }


}
