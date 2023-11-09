package com.vrv.logVO.deviceSafetyLog.app;

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
@LogDesc(value="应用审计日志（gab标准）",tableName="app_audit",topicName="app-audit")
public class AppAudit {
    @FieldDesc("上报设备IP")
    @RelateField("src_Ip")
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

    @FieldDesc("记录标识")
    private	String 	num_id;
    @FieldDesc("应用系统id")
    private String app_id;
    @FieldDesc("用户标识类型")
    private	String 	user_code_type;
    @FieldDesc("用户标识")
    private	String user_id;
    @FieldDesc("用户名")
    private	String username;
    @FieldDesc("单位名称")
    private	String organization;
    @FieldDesc("单位机构代码")
    private	String organization_id;
    @FieldDesc("设备类型")
    private	String terminal_type;
    @FieldDesc("设备标识")
    private	String terminal_id;
    @FieldDesc("操作时间")
    private	String operattion_time;
    @FieldDesc("操作类型")
    private	String operate_type;
    @FieldDesc("执行结果")
    private	String action_result;
    @FieldDesc("失败原因代码")
    private	String operate_result;
    @FieldDesc("操作所在模块")
    private	String operate_name;
    @FieldDesc("操作条件")
    private	String operate_condition;
    @FieldDesc("访问的URL")
    private	String url;
    @FieldDesc("请求方法")
    private	String http_method;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
