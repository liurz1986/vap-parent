package com.vrv.vap.common.config;

import com.vrv.vap.common.utils.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


/**
 * 请求头部信息加入到拦截器中进行向下传递
 *
 * @author wh1107066
 */
public class HeaderInterceptor implements RequestInterceptor {
    private Logger logger = LoggerFactory.getLogger(HeaderInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            if (request != null) {
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);
                        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(values)) {
                            template.header(name, values);
                        }
                    }
                }
            }else {
                logger.warn("获取不到HttpServletRequest容器不存在!");
            }
        } else {
            logger.warn("获取不到ServletRequestAttributes， 容器中不存在!");
        }
    }

}
