package com.vrv.vap.alarmdeal.business.model.vo;

import lombok.Data;

/**
 * 模型发布VO
 */
@Data
public class ModelPublishVO {
    private String guid; //模型配置id

    private String dataCustomerModel; //数据消费模式: 持续消费、周期消费

    private String dataCustomerPeriod; // 数据消费周期表达式

    private String modelInputParams; // 模型入参集:json字符串

    private String modelStartParam; // 模型启动参数

    private String modelLogPath; // 模型日志记录路径

    private String modelLogLevel;// 模型日志级别

}
