package com.vrv.vap.alarmdeal.frameworks.contract.dataSource;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年05月26日 11:39
 */
@Data
public class DataSource {
    // key
    private Integer id;

    // 索引/数据表/视图名
    private String name;

    // 数据源标题
    private String title;

    // 数据源Icon标识
    private String icon;

    // 1： 本地ES ,2: 本地Mysql ,3: 远程Mysql, 4远程mysql
    private Integer type;

    // 时间字段 ES 必选，MySql 可选
    private String timeField;

    // 描述
    private String description;

    // 时间字段格式
    private String timeFormat;

    // 数据类型，1：原始日志，2：基线数据
    private Integer dataType;

    // 对应kafka主题
    private String topicName;

    // 安全域字段
    private String domainField;

    // topic别名
    private String topicAlias;

}
