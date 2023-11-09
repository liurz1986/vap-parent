package com.vrv.logVO.access;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;
/**
 *  @author zhouwu
 *  @version 创建时间：2019年6月26日 下午2:55:20  
 */
@LogDesc(value="用户事件",tableName="VisitorIncident",topicName="terminal-Intranet-accessDetectionEven-standard-v1")
@Data
public class VisitorIncident {
    @FieldDesc("设备ID")
    private String  devid;
    @FieldDesc("日志产生时间戳")
    private String time;
    @FieldDesc("设备事件")
    private	String 	event_class_id;
    @FieldDesc("")
    private	String 	event_id;
    @FieldDesc("用户名")
    private	String 	auth_name;
    @FieldDesc("认证类型")
    private	String 	authtype;
    @FieldDesc("用户登录设备")
    @RelateField("src_Ip")
    private	String 	ipaddr;
    @FieldDesc("标识是否是告警事件")
    private	String 	alarm;
    @FieldDesc("告警级别")
    private	String level;
    @FieldDesc("事件信息")
    private	String 	details;

    @FieldDesc("源ip")
    @RelateField("src_Ip")
    private	String 	src_Ip;
    @FieldDesc("目标ip")
    private String dst_IP;
    @FieldDesc("源port")
    private	String 	src_port;
    @FieldDesc("目标port")
    private String dst_port;
    @FieldDesc("ip")
    private String relate_ip;
    @FieldDesc("处理时间")
    private Timestamp triggerTime;
    @FieldDesc("地区编码")
    private String areaCode;
    @FieldDesc("地区名称")
    private String areaName;
}
