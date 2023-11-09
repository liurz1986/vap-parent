package com.vrv.logVO.selfsafe;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;

@Data
@LogDesc(value="首次运行进程",tableName="dw_act_process",topicName="v1-DwActProcess")
public class DwActProcess {
    @FieldDesc("设备唯一ID")
    private String  dev_only_id;
    @FieldDesc("系统会话号")
    private	String 	session_id;
    @FieldDesc("当前操作系统IP地址")
    @RelateField("src_Ip")
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
    private	String remote_ip;
    @FieldDesc("数据来源")
    private	String 	data_src;
    @FieldDesc("当前主机名称")
    private	String 	clt_host;
    @FieldDesc("激活时间")
    private	String 	active_time;
    @FieldDesc("创建时间")
    private	String 	create_time;
    @FieldDesc("禁用时间")
    private	String disable_time;
    @FieldDesc("结束时间")
    private	String end_time;
    @FieldDesc("进程PID")
    private	String p_id;
    @FieldDesc("版本")
    private	String version;
    @FieldDesc("进程描述")
    private	String describ;
    @FieldDesc("MD5")
    private	String md5;
    @FieldDesc("进程名称")
    private	String name;
    @FieldDesc("进程完整路径")
    private	String path;
    @FieldDesc("数字签名机构名称")
    private	String signed_name;
    @FieldDesc("进程置顶且活跃时间")
    private	String top_active_time;
    @FieldDesc("窗口标题")
    private	String 	win_title;
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
