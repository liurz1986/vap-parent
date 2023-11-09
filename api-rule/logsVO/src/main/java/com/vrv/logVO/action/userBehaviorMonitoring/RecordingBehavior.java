package com.vrv.logVO.action.userBehaviorMonitoring;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;
@LogDesc(value="刻录行为",tableName="dw_device_prStringprotectedlog",topicName="terminal-bigData-IllegalBurn-standard-v1")
@Data
public class RecordingBehavior {
    @FieldDesc("ID")
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
    @FieldDesc("刻录序号")
    private	String 	burn_id;
    @FieldDesc("光盘类型")
    private	String 	cd_type;
    @FieldDesc("光盘名称")
    private	String cd_name;
    @FieldDesc("光驱名称")
    private	String 	cd_driver_name;
    @FieldDesc("注册人用户名")
    private	String reg_user_name;
    @FieldDesc("用户名")
    private	String 	user_name;
    @FieldDesc("用户组织id")
    private	String user_org_id;
    @FieldDesc("用户组织CodeUI")
    private	String 	user_org_code_ui;
    @FieldDesc("设备组织CODEUI")
    private	String 	dev_org_code_ui;
    @FieldDesc("设备组织机构路径")
    private	String 	dev_org_path;
    @FieldDesc("设备IP类型")
    private	String ip_type;
    @FieldDesc("持有人")
    private	String 	hold_name;
    @FieldDesc("是否已读")
    private	String bread;
    @FieldDesc("当前客户端真实和通讯IP")
    private	String 	cyr_ip;
    @FieldDesc("当前客户端真实和通讯MAC")
    private	String 	cur_mac;
    @FieldDesc("IP转换成long型数值")
    private	String ip_number;
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
