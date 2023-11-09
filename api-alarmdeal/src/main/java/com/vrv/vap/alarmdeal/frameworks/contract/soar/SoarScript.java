package com.vrv.vap.alarmdeal.frameworks.contract.soar;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "soar_script")
public class SoarScript {


    public static int  priorityLower=1;  //低
    public static int  priorityMiddle=3; //中
    public static int  priorityHigh=5;   //高

    public static int statusDraft=0; //草稿
    public static int statusStart=1; //启用
    public static int statusStop=2; //禁用
    public static int statusDelete=-1; //删除

    public static int cooperationAuto=0;    //自动
    public static int cooperationHand=1;    //手动


    @Id
    @Column
    private String guid;

    @Column
    private String name;

    @Column
    private Integer priority;  //优先级

    @Column(name="use_status")
    private Integer useStatus;  //状态（是否可用）

    @Column(name="built_in")
    private boolean builtIn;    //是否内置

    @Column(name="cooperation_mode")
    private Integer cooperationMode;   //联动模式

    @Column(name="deploy_id")
    private String deployId;    //流程部署外键

    @Column(name = "model_id")
    private String modelId;   //流程模型id

    @Column(name="event_types")
    private String eventTypes;  //事件类型

    @Column(name="create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column(name="update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column(name="update_version")
    private Integer updateVersion;  //版本

    @Column
    private String note;    //描述

    @Column(name="input_value")
    private String inputValue;  //入参配置


    @Column(name="output_value")
    private String outputValue;  //出参配置
    @Column(name="jbpm_path")
    private String jbpmPath; //jbpm文件路径
    @Column(name="line_info")
    private String lineInfo; //连线信息
    
    @Column(name="code")
    private String code; //编号
    
}
