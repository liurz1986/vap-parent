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
@LogDesc(value="基线合规",tableName="dw_baseline_conf_check",topicName="terminal-Intranet-baselineConfCheck-standard-v1")
public class DwBaselineConfCheck {
	@FieldDesc("id")
	private	String	id;
	@FieldDesc("设备唯一id")
	private	String	dev_only_id;
	@RelateField("src_Ip")
	@FieldDesc("id")
	private	String	ip;
	@FieldDesc("设备mac地址")
	private	String	mac;
	@FieldDesc("设备名")
	private	String	name;
	@FieldDesc("组织机构id")
	private	String	org_id;
	@FieldDesc("组织名称")
	private	String	org_name;
	@FieldDesc("注册组织机构唯一id")
	private	String	reg_user_only_id;
	@FieldDesc("注册组织机构账号")
	private	String	reg_user_account;
	@FieldDesc("当前使用人唯一id")
	private	String	user_only_id;
	@FieldDesc("策略名")
	private	String	policy_name;
	@FieldDesc("策略id")
	private	String	policy_id;
	@FieldDesc("模板名")
	private	String	template_name;
	@FieldDesc("状态")
	private	String	state;
	@FieldDesc("当前使用人账号")
	private	String	user_account;
	@FieldDesc("登录操作系统账号")
	private	String	os_login_account;
	@FieldDesc("客户端产生时间")
	private	String	client_time;
	@FieldDesc("上报时间")
	private	String	report_time;
	@FieldDesc("注册人用户名")
	private	String	reg_user_name;
	@FieldDesc("用户名")
	private	String	user_name;
	@FieldDesc("用户组织id")
	private	String	user_org_id;
	@FieldDesc("用户组织CodeUI")
	private	String	user_org_code_ui;
	@FieldDesc("设备组织CODEUI")
	private	String	dev_org_code_ui;
	@FieldDesc("设备组织机构路径")
	private	String	dev_org_path;
	@FieldDesc("设备IP类型")
	private	String	ip_type;
	@FieldDesc("持有人")
	private	String	hold_name;
	@FieldDesc("")
	private	String	bread;
	@FieldDesc("IP转换成long型数值")
	private	String ip_number;
	@FieldDesc("规则id")
	private	String	rule_id;
	@FieldDesc("结果类型")
	private	String	restype;
	@FieldDesc("错误号")
	private	String	error_no;
	@FieldDesc("系统当前值")
	private	String	ruleval;
	@FieldDesc("区域编码")
	private	String	area_code;
	@FieldDesc("区域名名称")
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
