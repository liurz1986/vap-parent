package com.vrv.vap.server.zuul.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huipei.x
 * @data 创建时间 2020/8/20
 * @description 类说明 :
 */
@Component
public class ResponseHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//        httpServletResponse.addHeader("X-Frame-Options", "DENY");
        // 防止被嵌到其他网站中,避免点击劫持 (clickJacking)攻击
        httpServletResponse.addHeader("X-Frame-Options", "SAMEORIGIN");
        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        httpServletResponse.addHeader("Cache-Control", "no-cache='set-cookie'");
        httpServletResponse.addHeader("Cache-Control", "private");
        httpServletResponse.addHeader("Pragma", "no-cache");
        httpServletResponse.addHeader("X-Content-Type-Options", "nosniff");
        httpServletResponse.addHeader("X-Xss-Protection", "1; mode=block");
        httpServletResponse.setHeader( "Set-Cookie", "name=value; HttpOnly");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}