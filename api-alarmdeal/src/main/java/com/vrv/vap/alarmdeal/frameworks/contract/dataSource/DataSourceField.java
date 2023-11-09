package com.vrv.vap.alarmdeal.frameworks.contract.dataSource;

import lombok.Data;
import scala.Int;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年05月26日 14:37
 */
@Data
public class DataSourceField {
    // KEY
    private Integer id;

    // 链接类型 person / device / app (单机版不支持)
    private String link;

    // 字段标题
    private String name;

    // 字段原始类型
    private String origin;

    // 是否显示
    private Boolean show;

    // 排序
    private Integer sort;

    // 是否可以按此字段排序
    private Boolean sorter;

    // 数据源ID
    private Integer sourceId;

    // 是否标签
    private Boolean tag;

    // 类型，支持：keyword text long double date object json
    private String type;

    // 数量格式
    private String unit;

    // 字段别名
    private String alias;

    // 分析事件排序
    private Integer analysisSort;

    // 分析事件字段类型
    private String analysisType;

    // 分析时间字段是否展示
    private int analysisShow;

    // 分析事件字段类型长度
    private Integer analysisTypeLength;

    // 字典
    private String dict;

    // 拓展字段json
    private String extendConf;

    // 字段名
    private String field;

    // 是否过滤
    private Boolean filter;

}
