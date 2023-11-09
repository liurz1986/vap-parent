package com.vrv.logVO.deviceSafetyLog.ips;

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
@LogDesc(value="ips入侵防护事件",tableName="ips_event",topicName="ips-event")
public class ipsEvent {
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

    @FieldDesc("源IP（客户端IP）")
    @RelateField("src_Ip")
    private	String 	src_ip;
    @FieldDesc("源区域")
    private String src_area;
    @FieldDesc("目的IP（服务器IP）")
    @RelateField("dst_Ip")
    private	String 	dst_ip;
    @FieldDesc("目标区域")
    private	String dst_area;
    /*  @FieldDesc("发生时间")
  private	String 	event_time;*/
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
    @FieldDesc("威胁等级")
    private	String event_level;
    @FieldDesc("事件（类型）统一编码")
    private	String event_code;
    @FieldDesc("事件名称")
    private	String event_name;
    @FieldDesc("设备IP")
    private	String device_ip;
    @FieldDesc("设备名称")
    private	String device_name;
    @FieldDesc("事件摘要")
    private	String event_abstract;
    @FieldDesc("执行策略")
    private	String action_strategy;
    @FieldDesc("执行结果")
    private	String action_result;


    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
