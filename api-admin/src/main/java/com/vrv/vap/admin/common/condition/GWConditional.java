package com.vrv.vap.admin.common.condition;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class GWConditional implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = context.getEnvironment().getProperty("auth.type");
        if (StringUtils.isNotEmpty(property) && "1".equals(property)) {
            return true;
        }
        return false;
    }
}
