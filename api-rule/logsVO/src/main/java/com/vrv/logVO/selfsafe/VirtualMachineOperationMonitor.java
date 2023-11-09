package com.vrv.logVO.selfsafe;


import java.sql.Timestamp;
import java.util.Date;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月25日 下午5:22:40 
* 类说明 
*/
@Data
@LogDesc(value="多操作系统",tableName="virtual_machine_operation_monitor",topicName="terminal-Intranet-VirtualMachineOperationMonitoring-standard-v1")
public class VirtualMachineOperationMonitor{
	@FieldDesc("")
	private String bread;
	@FieldDesc("客户端产生时间")
	private String client_time;
	@FieldDesc("")
	private String cur_mac;
	@FieldDesc("当前客户端真实和通讯MAC")
	private String cyr_ip;
	@FieldDesc("")
	private String dev_only_id;
	@FieldDesc("设备唯一ID")
	private String dev_org_code_ui;
	@FieldDesc("设备组织路径")
	private String dev_org_path;
	@FieldDesc("分组ID")
	private String group_id;
	@FieldDesc("分组名称")
	private String group_name;
	@FieldDesc("持有人")
	private String hold_name;
	@FieldDesc("id")
	private String id;
	@FieldDesc("ip")
	private String ip;
	@FieldDesc("ip编号")
	private String ip_number;
	@FieldDesc("ip类型")
	private String ip_type;
	@FieldDesc("mac")
	private String mac;
	@FieldDesc("名称")
	private String name;
	@FieldDesc("网络状态")
	private String net_state;
	@FieldDesc("行为审计")
	private String opt_audit;
	@FieldDesc("组织id")
	private String org_id;
	@FieldDesc("组织名称")
	private String org_name;
	@FieldDesc("系统信息")
	private String os_info;
	@FieldDesc("系统登录账号")
	private String os_login_account;
	@FieldDesc("策略id")
	private String policy_id;
	@FieldDesc("策略名称")
	private String policy_name;
	@FieldDesc("注册账号")
	private String reg_user_account;
	@FieldDesc("注册用户名称")
	private String reg_user_name;
	@FieldDesc("终端注册人唯一ID")
	private String reg_user_only_id;
	@FieldDesc("上报时间")
	private String report_time;
	@FieldDesc("系统类型")
	private String system_type;
	@FieldDesc("类型")
	private String type;
	@FieldDesc("用户账号")
	private String user_account;
	@FieldDesc("用户名")
	private String user_name;
	@FieldDesc("用户唯一id")
	private String user_only_id;
	@FieldDesc("用户组织CodeUI")
	private String user_org_code_ui;
	@FieldDesc("用户组织ID")
	private String user_org_id;
	@FieldDesc("触发事件")
	private Timestamp triggerTime;
}
