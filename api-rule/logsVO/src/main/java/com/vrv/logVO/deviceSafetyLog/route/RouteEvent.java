package com.vrv.logVO.deviceSafetyLog.route;

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
@LogDesc(value="路由器系统日志",tableName="route_event",topicName="route-event")
public class RouteEvent {
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
    @FieldDesc("登录IP")
    @RelateField("src_Ip")
    private	String 	login_ip;
    @FieldDesc("用户名")
    private	String login_user;
    /*  @FieldDesc("安全级别")
      private	String 	event_time;*/
    @FieldDesc("日志级别")
    private	String event_level;
    @FieldDesc("操作类型")
    private	String opt_type;
    @FieldDesc("CPU使用率")
    private	String cpu_usage;
    @FieldDesc("内存使用率")
    private	String mem_usage;
    @FieldDesc("硬盘使用率")
    private	String disc_usage;
    @FieldDesc("信息")
    private	String event_detail;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;

}
