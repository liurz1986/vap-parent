package com.vrv.vap.xc.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.UUID;

public class FeignFlumeConfig {

    /**
     * fegin拦截器:服务调用时带上cookie等信息
     *
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if (headerNames != null) {
                        while (headerNames.hasMoreElements()) {
                            String name = headerNames.nextElement();
                            String values = request.getHeader(name);
                            if ("cookie".equalsIgnoreCase(name) && values.indexOf("JSESSIONID") < 0) {
                                if (template.headers().containsKey("cookie")) {
                                    String cookie = template.headers().get("cookie").stream().findFirst().orElse("");
                                    values = values + "; " + cookie;
                                    template.header("cookie", values);
                                    continue;
                                }
                                values = values + "; JSESSIONID=" + UUID.randomUUID();
                            }
                            template.header(name, values);
                        }
                    }
                }
                /* //如果写了body会导致请求强制转换为post,慎用
                Enumeration<String> bodyNames = request.getParameterNames();
                StringBuffer body = new StringBuffer();
                if (bodyNames != null) {
                    while (bodyNames.hasMoreElements()) {
                        String name = bodyNames.nextElement();
                        String values = request.getParameter(name);
                        body.append(name).append("=").append(values).append("&");
                    }
                }
                if (body.length() != 0) {
                    body.deleteCharAt(body.length() - 1);
                    template.body(body.toString());
                }*/
            }
        };
    }
}
