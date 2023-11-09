package com.vrv.vap.server.zuul.filter;
import com.vrv.vap.server.zuul.wrapper.SqlInjectionRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huipei.x
 * @data 创建时间 2019/9/10
 * @description 类说明 :
 */
public class SqlInjectionFilter implements Filter {

    public List<String> excludes = new ArrayList<>(10);
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(handleExcludeURL((HttpServletRequest)servletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if(((HttpServletRequest)servletRequest).getMethod().equalsIgnoreCase("post") && ((HttpServletRequest)servletRequest).getHeader(HttpHeaders.CONTENT_TYPE).contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        SqlInjectionRequestWrapper sqlInjectionRequestWrapper = new SqlInjectionRequestWrapper((HttpServletRequest) servletRequest,handleExcludeURL((HttpServletRequest)servletRequest));
        
        filterChain.doFilter(sqlInjectionRequestWrapper, servletResponse);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        String temp = arg0.getInitParameter("excludes");
        if (temp != null) {
            String[] url = temp.split(",");
            for (int i = 0; url != null && i < url.length; i++) {
                excludes.add(url[i]);
            }
        }
    }

    @Override
    public void destroy() {

    }
    private boolean handleExcludeURL(HttpServletRequest request) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        String url = request.getRequestURI();
        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

}


