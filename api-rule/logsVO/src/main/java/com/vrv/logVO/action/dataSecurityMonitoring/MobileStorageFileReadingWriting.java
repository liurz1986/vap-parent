package com.vrv.logVO.action.dataSecurityMonitoring;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;

@LogDesc(value="移动存储文件读写",tableName="dw_mobile_storage_file_data_audit",topicName="v1-MobileStorageDeviceAccess")
@Data
public class MobileStorageFileReadingWriting {
    @FieldDesc("")
    private String  id;
    @FieldDesc("设备唯一ID")
    private String  dev_only_id;
    @FieldDesc("策略ID")
    private	String 	policy_id;
    @FieldDesc("设备MAC地址")
    private	String 	operate_id;
    @FieldDesc("设备名称")
    private	String 	org_id;
    @FieldDesc("U盘类型")
    private	String u_type;
    @FieldDesc("操作类型")
    private	String action;
    @FieldDesc("文件名")
    private String file_name;
    @FieldDesc("终端注册人账号")
    private	String 	file_path;
    @FieldDesc("文件大小")
    private	String file_size;
    @FieldDesc("目的路径")
    private	String 	des_path;
    @FieldDesc("操作进程名")
    private	String 	process;
    @FieldDesc("执行结果")
    private	String result;
    @FieldDesc("客户端产生时间")
    private	String 	client_time;
    @FieldDesc("客户端产生时间")
    private	String 	time;
    @FieldDesc("上报服务器时间")
    private	String 	report_time;
    @FieldDesc("设备IP")
    @RelateField("src_Ip")
    private	String 	ip;
    @FieldDesc("设备mac")
    private	String mac;
    @FieldDesc("设备名称")
    private	String 	name;
    @FieldDesc("终端组织名称")
    private	String org_name;
    @FieldDesc("终端注册人唯一ID")
    private	String reg_user_only_id;
    @FieldDesc("终端注册人帐号")
    private	String reg_user_account;
    @FieldDesc("终端当前使用人唯一ID")
    private	String user_only_id;
    @FieldDesc("终端当前使用人帐号")
    private	String user_account;
    @FieldDesc("注册人用户名")
    private	String reg_user_name;
    @FieldDesc("当前设备登录操作系统帐号")
    private	String os_login_account;
    @FieldDesc("用户组织机构CodeUI")
    private	String dev_org_code_ui;
    @FieldDesc("设备组织机构路径")
    private	String 	dev_org_path;
    @FieldDesc("设备IP类型")
    private	String ip_type;
    @FieldDesc("持有人姓名")
    private	String 	hold_name;
    @FieldDesc("设备组织CODEUI")
    private	String 	ip_number;
    @FieldDesc("用户名字")
    private	String 	user_name;
    @FieldDesc("是否已读")
    private	String bread;
    @FieldDesc("当前客户端真实和通讯IP")
    private	String cyr_ip;
    @FieldDesc("当前客户端真实和通讯MAC")
    private	String 	cur_mac;
    @FieldDesc("当前客户端真实和通讯IP")
    private	String 	clt_ip;
    @FieldDesc("当前客户端真实和通讯MAC")
    private	String 	clt_mac;
    @FieldDesc("用户组织机构CodeUI")
    private	String user_org_code_ui;
    @FieldDesc("")
    private	String source_disk_type;
    @FieldDesc("")
    private	String des_disk_type;
    @FieldDesc("")
    private	String 	source_disk_info;
    @FieldDesc("")
    private	String 	source_tag_info;
    @FieldDesc("源U盘")
    private	String source_u_sn;
    @FieldDesc("")
    private	String 	des_disk_info;
    @FieldDesc("")
    private	String 	des_tag_info;
    @FieldDesc("目标U盘")
    private	String 	des_u_sn;
    @FieldDesc("用户组织机构CodeUI")
    private	String source_path;
    @FieldDesc("区域编码")
    private	String area_code;
    @FieldDesc("区域名名称")
    private	String area_name;
    @FieldDesc("拷入拷出")
    private	String 	copy;
    @FieldDesc("拷入拷出")
    private	String 	operate_type;
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
