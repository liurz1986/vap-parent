package com.vrv.vap.server.zuul.filter.condition;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class VAPConditional implements Condition{

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = context.getEnvironment().getProperty("auth.type");
        //不配置，或者配置为0时，表示采用默认的过滤器
        if (StringUtils.isEmpty(property) || "0".equals(property) || "3".equals(property)) {
            return true;
        }
        return false;
    }
}
