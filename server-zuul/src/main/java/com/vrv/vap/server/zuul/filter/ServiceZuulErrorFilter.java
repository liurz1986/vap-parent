package com.vrv.vap.server.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.vrv.vap.server.zuul.filter.condition.VAPConditional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * @Author: liujinhui
 * @Date: 2019年8月10日00:56:02
 * @Desc 1. 当过滤器在执行过程中发生异常，若没有捕获到，就会进入error过滤器中
 * 2. 处理请求时发生错误时，被调用。在执行过程中发送错误进入error过滤器，可以用来统计记录错误信息
 */
@Component
@Conditional(VAPConditional.class)
public class ServiceZuulErrorFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(ServiceZuulErrorFilter.class);

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            Throwable throwable = ctx.getThrowable();
            logger.error("ErrorFilter拦截Zuul中未捕获的异常信息,{}", throwable.getCause().getMessage());
        } catch (Exception ex) {
            logger.error("ErrorFilter中捕获异常信息:{}", ex.getMessage());
        }
        return null;
    }

}