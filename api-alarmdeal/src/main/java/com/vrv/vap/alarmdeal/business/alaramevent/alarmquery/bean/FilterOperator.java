package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 过滤器组件
 *
 * @author wd-pc
 */
@Entity
@Data
@Table(name = "filter_operator")
public class FilterOperator implements Serializable {


    public static final String FILTER = "filter";
    public static final String ANALYSIS = "analysis";
    public static final String PERMIT = "permit";
    public static final String FORBID = "forbid";

    public static final String INSTANCE = "instance";
    public static final String RULE = "rule";
    public static final String MODEL = "model";

    private static final long serialVersionUID = 1L;
    @Id
    @Column
    private String guid; //guid
    @Column(name = "name")
    private String name; //过滤器组件名称
    @Column(name = "config")
    private String filterConfig; //过滤器组件配置
    @Column(name = "source")
    private String sourceIds; //数组类型，只能由eventtable
    @Column(name = "output_fields")
    private String outFieldInfos; //最终输出结果
    @Column(name = "version")
    private Integer version;  //过滤器版本号
    @Column(name = "delete_flag")
    private Boolean deleteFlag; //删除标识
    @Column(name = "dependencies")
    private String dependencies; //依赖
    @Column(name = "status") //过滤器状态
    private boolean status;
    @Column(name = "outputs") //输出配置
    private String outputs;
    @Column(name = "operator_type") //操作类型
    private String operatorType; //filter or analysis （过滤器 or 分析器）
    @Column(name = "multi_version")
    private String multiVersion;   //综合版本
    @Column(name = "code")
    private String code;   //唯一编码
    @Column(name = "label")
    private String label;   //中文标签
    @Column(name = "desc_")
    private String desc;   // 过滤器/分析器描述
    @Column(name = "create_time")
    private Date createTime;   // 创建时间
    @Column(name = "update_time")
    private Date updateTime;   // 创建时间
    @Column(name = "room_type")
    private String roomType;   // 盒子类型
    @Column(name = "config_template")
    private String filterConfigTemplate;   // 过滤器组件配置模板（带占位符）
    @Column(name = "param_config")
    private String paramConfig;   // 参数项
    @Column(name = "param_value")
    private String paramValue;   // 参数值
    @Column(name = "tag")
    private String tag;   // 接入数据源类型 ：online（实时规则） or offline（离线规则）
    @Column(name = "model_id")
    private String modelId;   // 分析器模板code值
    @Column(name = "rule_type")
    private String ruleType;  //规则类型

    @Column(name = "allow_start")
    private Boolean allowStart;  //规则是否有误
    @Column(name = "newline_flag")
    private String newlineFlag;  //换行标识

    @Column(name = "filter_type")
    @ApiModelProperty("过滤规则类型")
    private String filterType;

    @Column(name = "rule_filter_type")
    @ApiModelProperty("过滤规则类型")// inside(内部)    warning（预警）    alarmdeal（告警）
    private String ruleFilterType;

    @Column(name = "init_status")
    @ApiModelProperty("是否预置")// 0 预置，1 自定义
    private String initStatus;

    @Column(name = "attack_line") //攻击链阶段
    private String attackLine;

    @Column(name = "threat_credibility") //威胁可信度
    private String threatCredibility;

    @Column(name = "deal_advcie") //处置建议
    private String dealAdvcie;

    @Column(name = "harm")
    private String harm;    //危害

    @Column(name = "principle")
    private String principle;    //原理

    @Column(name = "violation_scenario")
    private String violationScenario;// 违规场景

    @Column(name = "filter_desc")
    private String filterDesc;// 规则描述

    @Column(name = "start_config")
    private String startConfig;//启动参数配置

}
