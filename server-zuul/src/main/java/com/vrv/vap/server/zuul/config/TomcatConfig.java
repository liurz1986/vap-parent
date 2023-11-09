package com.vrv.vap.server.zuul.config;


import org.apache.catalina.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huipei.x
 * @data 创建时间 2019/12/11
 * @description 类说明 :
 */

@Configuration
public class TomcatConfig {

    /**
     * 绑定的host
     */
    @Value("${server.host}")
    private String host;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcatServletContainerFactory = new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();

                collection.addPattern("/*");
                collection.addMethod("HEAD");
                collection.addMethod("OPTIONS");
                collection.addMethod("TRACE");
                collection.addMethod("COPY");
                collection.addMethod("SEARCH");
                collection.addMethod("PROPFIND");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcatServletContainerFactory.addConnectorCustomizers(connector -> {
            if(StringUtils.isNotEmpty(host)) {
                connector.setProxyName(host);
            }
            connector.setUseIPVHosts(true);
            connector.setXpoweredBy(false);
        });
        return tomcatServletContainerFactory;
    }

}
