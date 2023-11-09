package com.vrv.vap.alarmdeal.business.flow.core.model;

import lombok.Data;
import org.activiti.engine.impl.el.FixedValue;

@Data
public class FixModel {


    private FixedValue candidateType;
    private FixedValue candidate; // TODO 根据逗号分隔。进一步可以考虑使用流程变量占位符。
    private FixedValue secParam; //TODO 安全域附属参数
    private FixedValue roleParam; //TODO 角色附属参数

    private FixedValue roleValue; // 角色code，根据逗号分隔 2021-09-10

    private FixedValue userValue; // 用户id，根据逗号分隔 2021-09-10

    private FixedValue node;  //任务节点name;

    private  String executionId; //执行流id


    private  String taskId;  //节点任务id

    private  String intanceId; //流程实例id

    private  Object busiArgs;  //表单业务参数





}
