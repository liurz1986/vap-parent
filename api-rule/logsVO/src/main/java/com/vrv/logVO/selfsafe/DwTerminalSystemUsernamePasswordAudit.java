package com.vrv.logVO.selfsafe;


import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月25日 下午5:57:06 
* 类说明  基线合规
*/
@Data
@LogDesc(value="终端系统用户名密码审计",tableName="dw_terminal_system_username_password_audit",topicName="terminal-Intranet-userNameAndPasswordAudit-standard-v1")
public class DwTerminalSystemUsernamePasswordAudit {
	@FieldDesc("设备唯一ID")
	private	String	dev_only_id;
	@RelateField("src_Ip")
	@FieldDesc("设备IP地址")
	private	String	ip;
	@FieldDesc("设备MAC地址")
	private	String	mac;
	@FieldDesc("设备名称")
	private	String	name;
	@FieldDesc("组织机构ID")
	private	String	org_id;
	@FieldDesc("终端所属组织机构名称")
	private	String	org_name;
	@FieldDesc("终端注册人唯一ID")
	private	String	reg_user_only_id;
	@FieldDesc("终端注册人账号")
	private	String	reg_user_account;
	@FieldDesc("终端当前使用人唯一ID")
	private	String	user_only_id;
	@FieldDesc("终端注册人账号")
	private	String	user_account;
	@FieldDesc("终端当前登录操作系统帐号")
	private	String	os_login_account;
	@FieldDesc("客户端产生时间")
	private	String	client_time;
	@FieldDesc("上报时间")
	private	String	report_time;
	@FieldDesc("策略ID")
	private	String	policy_id;
	@FieldDesc("是否存在空密码用户名")
	private	String	is_empty;
	@FieldDesc("空密码用户名")
	private	String	empty_user;
	@FieldDesc("是否存在弱口令用户名")
	private	String	is_weak;
	@FieldDesc("弱口令用户名")
	private	String	weak_user;
	@FieldDesc("用户名")
	private	String	user_name;
	@FieldDesc("用户组织ID")
	private	String	user_org_id;
	@FieldDesc("用户组织CodeUI")
	private	String	user_org_code_ui;
	@FieldDesc("设备组织CODEUI")
	private	String	dev_org_code_ui;
	@FieldDesc("设备组织路径")
	private	String	dev_org_path;
	@FieldDesc("ip类型")
	private	String	ip_type;
	@FieldDesc("持有人")
	private	String	hold_name;
	@FieldDesc("")
	private	String	bread;
	@FieldDesc("当前客户端真实和通讯IP")
	private	String	cyr_ip;
	@FieldDesc("id")
	private	String	cur_mac;
	@FieldDesc("当前客户端真实和通讯MAC")
	private	String	ip_number;
	@FieldDesc("区域编码")
	private	String	area_code;
	@FieldDesc("区域名称")
	private	String	area_name;
	@FieldDesc("分区字段")
	private	String	dt;
	@FieldDesc("分区字段")
	private	String	province;
	@FieldDesc("源ip")
	private	String	src_Ip;
	@FieldDesc("目的ip")
	private	String	dst_Ip;
	@FieldDesc("源端口")
	private	String	src_port;
	@FieldDesc("目的端口")
	private	String	dst_port;
	@FieldDesc("关联IP")
	private	String	relate_ip;
	@FieldDesc("触发时间")
	private Timestamp triggerTime;
    @FieldDesc("地区编码")
    private String areaCode;
    @FieldDesc("地区名称")
    private String areaName;

}
