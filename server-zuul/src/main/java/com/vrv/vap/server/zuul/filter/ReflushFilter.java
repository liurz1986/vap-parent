package com.vrv.vap.server.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.vrv.vap.server.zuul.filter.condition.VAPConditional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;


/**
 * 等待完成 ： 用于通过API修改资源等信息后，刷新缓存
 */
@Component
@Conditional(VAPConditional.class)
public class ReflushFilter extends ZuulFilter {

    static final Pattern PTN = Pattern.compile("\"roleId\":\"?(\\d+)\"?");

    private Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 管理员更新权限后，刷zuul的权限池
     */
    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        String uri = context.getRequest().getRequestURI();
        logger.info("uri: {}", uri);
        String method = context.getRequest().getMethod().toUpperCase();
        if ("PUT".equals(method.toUpperCase(Locale.ENGLISH)) && uri.startsWith("/api-common/role/resource/")) {
            logger.info("uri: {}, method: {}, return true;", uri, method);
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        try {
            logger.info("Enter Reflush  Role - Resource : ");
            RequestContext context = RequestContext.getCurrentContext();
            String uri = context.getRequest().getRequestURI();
            String[] ptns = uri.split("/");
            String key = ptns[ptns.length - 1];
            logger.info("Reflush Cache Role : " + key);
        } catch (Exception ex) {
            logger.error("ReflushFilter中捕获异常信息:{}", ex.getMessage());
        }
        return null;
    }
}
