package com.vrv.vap.admin.common.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

public class VapOrZhyCondition  extends AnyNestedCondition {
    VapOrZhyCondition(){
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @Conditional(VAPConditional.class)
    static class Vap {}

    @Conditional(ZHYConditional.class)
    static class Zhy {}

}
