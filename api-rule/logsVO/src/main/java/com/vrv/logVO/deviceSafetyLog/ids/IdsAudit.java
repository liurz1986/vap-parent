package com.vrv.logVO.deviceSafetyLog.ids;

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
@LogDesc(value="ids操作日志（登录、操作）",tableName="ids_audit",topicName="ids-audit")
public class IdsAudit {
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
    @RelateField("src_Ip")
    private	String 	device_ip;
    @FieldDesc("设备名称")
    private String device_name;
    @FieldDesc("登录IP")
    @RelateField("src_Ip")
    private	String 	login_ip;
    @FieldDesc("用户名")
    private	String login_user;
    /*  @FieldDesc("发生时间")
  private	String 	event_time;*/
    @FieldDesc("日志级别")
    private	String event_level;
    @FieldDesc("操作类型")
    private	String opt_type;
    @FieldDesc("模块（进程）")
    private	String opt_process;
    @FieldDesc("操作命令")
    private	String opt_condition;
    @FieldDesc("命令结果")
    private	String opt_result;
    @FieldDesc("信息")
    private	String opt_msg;

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
    @FieldDesc("地区编码")
    private String areaCode;
    @FieldDesc("地区名称")
    private String areaName;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
