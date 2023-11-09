package com.vrv.logVO.selfsafe;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

import java.sql.Timestamp;

@Data
@LogDesc(value="开放端口",tableName="dw_port_change",topicName="terminal-Intranet-portChange-standard-v1")
public class DwPortChange {
    @FieldDesc("")
    private String  id;
    @FieldDesc("设备唯一ID")
    private String  dev_only_id;
    @FieldDesc("设备IP地址")
    @RelateField("src_Ip")
    private	String 	ip;
    @FieldDesc("设备MAC地址")
    private	String 	mac;
    @FieldDesc("当前设备登陆操作系统账号")
    private	String os_account;
    @FieldDesc("客户端产生时间")
    private	String client_time;
    @FieldDesc("对应执行策略ID")
    private	String 	policy_id;
    @FieldDesc("本地端口")
    private	String local_port;
    @FieldDesc("远程IP")
    private	String remote_ip;
    @FieldDesc("远程端口")
    private	String remote_port;
    @FieldDesc("端口绑定的协议")
    private	String protocol;
    @FieldDesc("端口状态")
    private	String status;
    @FieldDesc("进程名称")
    private	String proc_name;
    @FieldDesc("源文件名")
    private	String source_name;
    @FieldDesc("进程ID号")
    private	String p_id;
    @FieldDesc("文件路径")
    private	String path;
    @FieldDesc("用户组织id")
    private	String 	md5;
    @FieldDesc("文件版本")
    private	String file_version;
    @FieldDesc("产品版本")
    private	String 	product_version;
    @FieldDesc("公司名称")
    private	String company;
    @FieldDesc("数字签名")
    private	String 	sign;
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
    @FieldDesc("病毒名称")
    private String fallName;
    @FieldDesc("等级")
    private String fallLevel;
    @FieldDesc("端口特征")
    private String port_feature;
    @FieldDesc("网络特征")
    private String web_feature;
    @FieldDesc("进程特征")
    private String process_feature;
    @FieldDesc("补丁名称")
    private String patch;
    @FieldDesc("端口个数")
    private String port_count;
    @FieldDesc("url个数")
    private String url_count;
    @FieldDesc("进程状态")
    private String process_status;
    @FieldDesc("补丁状态")
    private String patch_status;

    
    
    

}
