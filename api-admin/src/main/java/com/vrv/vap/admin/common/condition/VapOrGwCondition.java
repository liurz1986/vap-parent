package com.vrv.vap.admin.common.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

public class VapOrGwCondition extends AnyNestedCondition {

    VapOrGwCondition(){
        super(ConfigurationPhase.REGISTER_BEAN);
    }
    @Conditional(VAPConditional.class)
    static class Vap {}

    @Conditional(GWConditional.class)
    static class Gw {}
}
