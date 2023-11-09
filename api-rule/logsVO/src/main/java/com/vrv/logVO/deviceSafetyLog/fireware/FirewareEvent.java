package com.vrv.logVO.deviceSafetyLog.fireware;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;
import lombok.Data;

import java.sql.Timestamp;

/**
 *  @author zhouwu
 *  @version 创建时间：2019年8月12日 下午10:13:20  
 */
@Data
@LogDesc(value="防火墙防护事件日志",tableName="fireware_event",topicName="fireware-event")
public class FirewareEvent {
    @FieldDesc("上报设备IP")
    private String  report_ip;
    @FieldDesc("上报设备IP转换")
    private	String 	report_ip_num;
    @FieldDesc("入库时间")
    private	String 	indate;
    @FieldDesc("发生时间")
    private	String 	event_time;
    @FieldDesc("来源")
    private String msg_src;
    @FieldDesc("原始字段")
    private	String 	report_msg;
    @FieldDesc("安全域关联IP")
    private	String safety_margin_ip;
    @FieldDesc("安全级别")
    private	String 	security_level;
    @FieldDesc("日志类别")
    private	String log_type;
    @FieldDesc("安全域")
    private	String safety_margin;

    @FieldDesc("设备IP")
    private	String 	device_ip;
    @FieldDesc("设备名称")
    private String device_name;
    @FieldDesc("源IP")
    @RelateField("src_Ip")
    private	String 	src_ip;
    @FieldDesc("源区域")
    private	String src_area;
    @FieldDesc("目的IP")
    @RelateField("dst_Ip")
    private	String dst_ip;
    @FieldDesc("目标区域")
    private	String dst_area;
    @FieldDesc("源端口")
    @RelateField("src_port")
    private	String src_port;
    @FieldDesc("目的端口")
    @RelateField("dst_port")
    private	String dst_port;
    @FieldDesc("传输层协议")
    private	String transport_protocol;
    @FieldDesc("应用层协议")
    private	String app_protocol;
    @FieldDesc("事件等级")
    private	String event_level;
    @FieldDesc("事件类型")
    private	String event_type;
    @FieldDesc("事件摘要")
    private	String event_abstract;
    @FieldDesc("策略规则id")
    private	String strategy_id;
    @FieldDesc("策略名称")
    private	String strategy_name;
    @FieldDesc("执行动作")
    private	String action_content;
    @FieldDesc("执行结果")
    private	String action_result;
    @FieldDesc("命中次数")
    private	String action_time;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
