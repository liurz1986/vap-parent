package com.vrv.logVO.action.dataSecurityMonitoring;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;
@LogDesc(value="移动存储设备接入",tableName="dw_mobile_storage_file_operation_audit",topicName="terminal-Intranet-RemoveStorageFileOperationAudit-v1")
@Data
public class MobileStorageDeviceAccess {
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
    @FieldDesc("U盘厂商名")
    private	String 	umanufacturer;
    @FieldDesc("U盘序列号")
    private	String 	u_sn;
    @FieldDesc("U盘类型")
    private	String u_type;
    @FieldDesc("速度")
    private	String action;
    @FieldDesc("标签信息")
    private	String tage_info;
    @FieldDesc("U盘备注信息")
    private	String remark;
    @FieldDesc("操作ID")
    private	String operate_id;
    @FieldDesc("注册人用户名")
    private	String reg_user_name;
    @FieldDesc("用户名")
    private	String user_name;
    @FieldDesc("用户组织id")
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
    @FieldDesc("是否已读")
    private	String bread;
    @FieldDesc("当前客户端真实和通讯IP")
    private	String 	cyr_ip;
    @FieldDesc("当前客户端真实和通讯MAC")
    private	String cur_mac;
    @FieldDesc("IP转换成String型数值")
    private	String 	ip_number;
    @FieldDesc("当前客户端真实和通讯MAC")
    private	String udisk_info;
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