package com.vrv.vap.server.zuul.filter;

import com.vrv.vap.server.zuul.config.IpHttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huipei.x
 * @data 创建时间 2020/9/4
 * @description 类说明 :
 */
@Component
@Order(Integer.MAX_VALUE-2)
public class IpHttpServletRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        IpHttpServletRequest ipHttpServletRequest=new IpHttpServletRequest(httpServletRequest);
        filterChain.doFilter(ipHttpServletRequest, httpServletResponse);
    }
}
