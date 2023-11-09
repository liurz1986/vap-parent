package com.vrv.logVO.selfsafe;


import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月25日 下午5:57:06 
* 类说明  基线合规
*/
@Data
@LogDesc(value="补丁安装情况",tableName="dw_patch_install_info",topicName="terminal-Intranet-patchInstallation-standard-v1")
public class DwPatchInstallInfo {
	@FieldDesc("补丁ID")
	private	String	update_id;
	@FieldDesc("补丁名称")
	private	String	patch_name;
	@FieldDesc("地区编码")
	private	String	publish_time;
	@FieldDesc("补丁发布时间")
	private	String	distribute_time;
	@FieldDesc("补丁等级")
	private	String	patch_level;
	@FieldDesc("补丁编码")
	private	String	patch_code;
	@FieldDesc("设备唯一ID")
	private	String	dev_only_id;
	@FieldDesc("打补丁时间")
	private	String	patch_time;
	@FieldDesc("补丁状态")
	private	String	state;
	@FieldDesc("操作模式")
	private	String	mode;
	@FieldDesc("描述或备注")
	private	String	description;
	@FieldDesc("地区编码")
	private	String	area_code;
	@FieldDesc("地区编码")
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
