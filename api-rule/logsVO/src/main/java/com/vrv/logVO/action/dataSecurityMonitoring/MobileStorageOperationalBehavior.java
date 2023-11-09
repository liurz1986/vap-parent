package com.vrv.logVO.action.dataSecurityMonitoring;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;

@LogDesc(value="移动存储操作行为",tableName="dw_file_operation",topicName="v1-MobileStorageDeviceAccess")
@Data
public class MobileStorageOperationalBehavior {
    @FieldDesc("设备唯一ID")
    private String  dev_only_id;
    @FieldDesc("系统会话号")
    private	String 	session_id;
    @FieldDesc("当前操作系统IP地址")
    private	String 	clt_ip;
    @FieldDesc("当前操作系统MAC地址")
    private	String 	clt_mac;
    @FieldDesc("当前操作账户")
    private	String 	logon_user;
    @FieldDesc("当前标准时间")
    private	String 	std_time;
    @FieldDesc("当前客户端时间")
    private String time;
    @FieldDesc("pki ID")
    private	String 	pki_id;
    @FieldDesc("pki 用户身份证号")
    private	String user_id;
    @FieldDesc("pki 用户姓名")
    private	String 	user_name;
    @FieldDesc("单位")
    private	String 	unit;
    @FieldDesc("远程登录ip")
    @RelateField("src_Ip")
    private	String remote_ip;
    @FieldDesc("数据来源")
    private	String 	data_src;
    @FieldDesc("当前主机名称")
    private	String 	clt_host;
    @FieldDesc("进行操作的文件名称")
    private	String 	file_name;
    @FieldDesc("进行操作的文件大小")
    private	String 	file_size;
    @FieldDesc("操作类型")
    private	String mode;
    @FieldDesc("拷贝文件的原始路径")
    private	String src_path;
    @FieldDesc("拷贝文件的目标路径")
    private	String dst_path;
    @FieldDesc("u盘序列号")
    private	String usb_serial_no;
    @FieldDesc("区域编码")
    private	String area_code;
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
