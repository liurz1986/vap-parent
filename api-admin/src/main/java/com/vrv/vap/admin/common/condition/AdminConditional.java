package com.vrv.vap.admin.common.condition;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AdminConditional implements Condition{

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = context.getEnvironment().getProperty("spring.application.name");
        //不配置，或者配置为0时，表示采用默认的过滤器
        if (StringUtils.isNotEmpty(property) && "api-admin".equals(property)) {
            return true;
        }
        return false;
    }
}
