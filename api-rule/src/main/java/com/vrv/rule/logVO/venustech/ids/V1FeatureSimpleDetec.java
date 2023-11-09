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
* 类说明   IDS日志-V1FeatureDetec(精简版)
*/
@LogDesc("IDS日志-V1FeatureSimpleDetec")
@Data
public class V1FeatureSimpleDetec implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@FieldDesc("TM")
	private Timestamp tm;
	@FieldDesc("目的IP")
	private String src_ip;
	@FieldDesc("目的端口")
	private String src_port;
	@FieldDesc("目的IP")
	private String dst_ip;
	@FieldDesc("目的端口")
	private String dst_port;
	@FieldDesc("协议")
	private String protocol;
	@FieldDesc("主题")
	private String subject;
	@FieldDesc("攻击ID")
	private String attack_id;

}
