package com.vrv.rule.logVO.venustech.ids;

import java.io.Serializable;
import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月29日 上午9:48:36 
* 类说明   IDS日志-V1FeatureDetec
*/
@LogDesc("IDS日志-V1FeatureDetec")
@Data
public class V1FeatureDetec implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@FieldDesc("DT版本")
	private String dt_version;
	@FieldDesc("等级")
	private Long level;
	@FieldDesc("ID")
	private String id; 
	@FieldDesc("类型")
	private String type;
	@FieldDesc("TM")
	private String tm;
	@FieldDesc("目的IP")
	private String src_ip;
	@FieldDesc("目的端口")
	private Long src_port;
	@FieldDesc("目的MAC")
	private String src_mac;
	@FieldDesc("目的IP")
	private String dst_ip;
	@FieldDesc("目的端口")
	private Long dst_port;
	@FieldDesc("目的MAC")
	private String dst_mac;
	@FieldDesc("数量")
	private Long counts;
	@FieldDesc("协议")
	private String protocol;
	@FieldDesc("主题")
	private String subject;
	@FieldDesc("安全ID")
	private String security_id;
	@FieldDesc("攻击ID")
	private String attack_id;
	@FieldDesc("信息")
	private String message;
	@FieldDesc("AJB生产者")
	private String ajb_producer;
	@FieldDesc("AJB主机")
	private String ajb_host;
	@FieldDesc("VRV接收时间")
	private String vrv_receive_time;
	@FieldDesc("DT")
	private String dt;  
	@FieldDesc("省份")
	private String province;
	@FieldDesc("处理时间")
	private Timestamp triggerTime;

}
