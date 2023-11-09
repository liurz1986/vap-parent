package com.vrv.logVO.access;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;

/**
 *  @author zhouwu
 *  @version 创建时间：2019年6月25日 下午3:55:20  
 */
@LogDesc(value="设备事件",tableName="DeviceAccess",topicName="TERMINAL-INTRANET-ACCESSDETECTIONEVEN-STANDARD-V1")
@Data
public class DeviceAccess {
    @FieldDesc("设备ID")
    private String  devid;
    @FieldDesc("日志产生时间戳")
    private	String time;
    @FieldDesc("设备事件")
    private	String 	event_class_id;
    @FieldDesc("")
    private	String 	event_id;
    @FieldDesc("设备名称")
    private	String 	name;
    @FieldDesc("终端IP地址")
    @RelateField("src_Ip")
    private	String 	ipaddr;
    @FieldDesc("端口名")
    private	String 	port_name;
    @FieldDesc("标识是否是告警事件")
    private	String 	alarm;
    @FieldDesc("告警级别")
    private	String level;
    @FieldDesc("事件信息")
    private	String 	details;

    @FieldDesc("源ip")
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
