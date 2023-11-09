package com.vrv.logVO.action.networkBehaviorMonitoring;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;

@LogDesc(value="异常流量",tableName="dw_net_monitor_audit",topicName="terminal-Intranet-trafficMonitorAudit-standard-v1")
@Data
public class AbnormalFlow {
    @FieldDesc("")
    private String  id;
    @FieldDesc("设备唯一ID")
    private String  dev_only_id;
    @FieldDesc("设备IP地址")
    @RelateField("src_Ip")
    private	String 	ip;
    @FieldDesc("设备MAC地址")
    private	String 	mac;
    @FieldDesc("设备名称")
    private	String 	name;
    @FieldDesc("组织机构ID")
    private	String 	org_id;
    @FieldDesc("终端所属组织机构名称")
    private	String 	org_name;
    @FieldDesc("终端注册人唯一ID")
    private String reg_user_only_id;
    @FieldDesc("终端注册人账号")
    private	String 	reg_user_account;
    @FieldDesc("终端当前使用人为一ID")
    private	String os_login_account;
    @FieldDesc("终端当前使用人账号")
    private	String 	user_only_id;
    @FieldDesc("终端当前登录操作系统帐号")
    private	String 	user_account;
    @FieldDesc("客户端产生时间")
    private	String client_time;
    @FieldDesc("上报到服务器时间")
    private	String 	report_time;
    @FieldDesc("对应执行策略ID")
    private	String 	policy_id;
    @FieldDesc("流量")
    private	String 	flow;
    @FieldDesc("域名")
    private	String 	operate_type;
    @FieldDesc("处理方式")
    private	String type;
    @FieldDesc("速度")
    private	String 	speed;
    @FieldDesc("开始时间")
    private	String begin_time;
    @FieldDesc("结束时间")
    private	String end_time;
    @FieldDesc("进程名")
    private	String process_name;
    @FieldDesc("上传流量")
    private	String up_flow;
    @FieldDesc("")
    private	String down_flow;
    @FieldDesc("审计类型")
    private	String audit_type;
    @FieldDesc("用户名字")
    private	String user_name;
    @FieldDesc("用户组织机构CodeUI")
    private	String user_org_code_ui;
    @FieldDesc("设备组织机构CodeUI")
    private	String dev_org_code_ui;
    @FieldDesc("设备组织机构路径")
    private	String 	dev_org_path;
    @FieldDesc("设备IP类型")
    private	String ip_type;
    @FieldDesc("持有人姓名")
    private	String 	hold_name;
    @FieldDesc("设备组织CODEUI")
    private	String 	ip_number;
    @FieldDesc("区域编码")
    private	String 	area_code;
    @FieldDesc("区域名名称")
    private	String area_name;
    @FieldDesc("分区字段")
    private	String dt;
    @FieldDesc("分区字段")
    private	String 	province;

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
