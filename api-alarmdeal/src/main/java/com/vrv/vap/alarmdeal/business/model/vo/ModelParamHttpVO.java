package com.vrv.vap.alarmdeal.business.model.vo;

import lombok.Data;

/**
 * 调用模型方执行模型运行、模型测试传参实体
 */
@Data
public class ModelParamHttpVO {
    private String name;  // 参数名称

    private String paramDesc; // 参数描述

    private String paramValueType; // 参数值类型：数值类型-int，字符串-String，日期时间-Date

    private String paramType; // 参数类型(业务参数/技术参数)

    private String paramValue; // 当前值

    private String paramDefaultValue; // 参数默认值
}
