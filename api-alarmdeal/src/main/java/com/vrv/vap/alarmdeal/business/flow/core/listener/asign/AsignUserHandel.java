package com.vrv.vap.alarmdeal.business.flow.core.listener.asign;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 流程中直接设置处理人处理
 *
 * 2022-11-29
 * lrz
 */
@Component("asignUserHandel")
public class AsignUserHandel {
    /**
     * 会签时，动态获取审批人
     * @param execution
     * @return
     */
    public List<String> resolve(DelegateExecution execution, String value) {
        String[] user = value.split(",");
        List<String> users = Arrays.asList(user);
        return users;
    }
}
