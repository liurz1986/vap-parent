package com.vrv.logVO.action.networkBehaviorMonitoring;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;
@LogDesc(value="违规外联",tableName="dw_online_outreach",topicName="terminal-Intranet-violationOutreachAudit-standard-v1")
@Data
public class IllegalOutreach {
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
    @FieldDesc("代理类型")
    private	String 	proxy_type;
    @FieldDesc("代理IP")
    private	String 	proxy_ip;
    @FieldDesc("代理端口")
    private	String 	proxy_port;
    @FieldDesc("外联持续时间")
    private	String connect_time;
    @FieldDesc("违规处理方式")
    private	String 	result;
    @FieldDesc("1:一般性违规外联 2:违规拨号连接 3:通过代理违规外联")
    private	String ret;
    @FieldDesc("用户名字")
    private	String user_name;
    @FieldDesc("用户组织机构ID")
    private	String user_org_id;
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
