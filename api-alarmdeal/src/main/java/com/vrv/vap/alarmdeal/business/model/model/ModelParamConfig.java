package com.vrv.vap.alarmdeal.business.model.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 模型参数配置
 */
@Data
@Table(name="model_param_config")
@Entity
@ApiModel(value = "模型参数配置表")
public class ModelParamConfig {
    @Id
    private String guid;
    @Column(name="name")
    private String name;  // 参数名称
    @Column(name="param_desc")
    private String paramDesc; // 参数描述
    @Column(name="param_value_type")
    private String paramValueType; // 参数值类型：数值类型-int，字符串-String，日期时间-Date
    @Column(name="param_type")
    private String paramType; // 参数类型(业务参数/技术参数)
    @Column(name="param_value")
    private String paramValue; // 当前值
    @Column(name="param_default_value")
    private String paramDefaultValue; // 参数默认值
    @Column(name="model_manage_id")
    private String modelManageId; // 模型配置管理id

}
