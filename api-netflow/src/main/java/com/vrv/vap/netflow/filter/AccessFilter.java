package com.vrv.vap.netflow.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wh1107066
 * @date 2023/9/7
 */
@Configuration
public class AccessFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(AccessFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Object os = request.getAttribute(RequestAttributes.OS);
        Object version = request.getAttribute(RequestAttributes.VERSION);
        Object network = request.getAttribute(RequestAttributes.NETWORK);
        String ip = getIpAddress(request);

        logger.debug("api-netflow请求的ip={} method={} uri={} status={} <<<<<<<<<<\n  os= {}\n version= {}\n network= {}\n",
                ip,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                os,
                version,
                network);

        filterChain.doFilter(request, response);
    }

    private String getIpAddress(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String forward = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(forward) && !"unKnown".equalsIgnoreCase(forward)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = forward.indexOf(",");
            if (index != -1) {
                return forward.substring(0, index);
            } else {
                return forward;
            }
        }
        forward = xip;
        if (StringUtils.isNotEmpty(forward) && !"unKnown".equalsIgnoreCase(forward)) {
            return forward;
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getRemoteAddr();
        }
        return forward;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
