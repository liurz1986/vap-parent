package com.vrv.rule.logVO;

import java.util.Date;
import java.util.Map;

import org.apache.flink.calcite.shaded.com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月20日 上午10:27:09 
* 类说明 流中间数据处理 
*/
@Data
public class StreamMidVO {
	private String areaCode;
	private String areaName;
	private String resultGuid;
	private String ruleCode;
	private String src_ips;
	private String src_ports;
	private String dstIps;
	private String dst_ports;
	private String logsInfo; //原始日志信息
	private String relatedIps; //关联IP
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date triggerTime;
	private Map<String,String[]> idRoom; //idRoom对应的id
	private String timeRoom; //对应的时间
	private Map<String,Object> extendParams; //告警额外属性
	
	
	public StreamMidVO(String resultGuid,String ruleCode,Date triggerTime,String src_ips,String dstIps,String relatedIps,String logsInfo,String src_ports,String dst_ports){
		this.resultGuid = resultGuid;
		this.ruleCode = ruleCode;
		this.triggerTime = triggerTime;
		this.src_ips = src_ips;
		this.dstIps = dstIps;
		this.relatedIps = relatedIps;
		this.logsInfo = logsInfo;
		this.src_ports = src_ports;
		this.dst_ports = dst_ports;
	}
	
	public StreamMidVO(String resultGuid,String ruleCode,Date triggerTime,String src_ips,String dstIps,String relatedIps,String logsInfo,String src_ports,String dst_ports,String areaName,String areaCode){
		this.resultGuid = resultGuid;
		this.ruleCode = ruleCode;
		this.triggerTime = triggerTime;
		this.src_ips = src_ips;
		this.dstIps = dstIps;
		this.relatedIps = relatedIps;
		this.logsInfo = logsInfo;
		this.src_ports = src_ports;
		this.dst_ports = dst_ports;
		this.areaName = areaName;
		this.areaCode = areaCode;
	}
	
	public StreamMidVO(String resultGuid,String ruleCode,Date triggerTime,String src_ips,String dstIps,String relatedIps,String src_ports,String dst_ports,Map<String,String[]> idRoom,String timeRoom,Map<String,Object> extendParams) {
		this.resultGuid = resultGuid;
		this.ruleCode = ruleCode;
		this.triggerTime = triggerTime;
		this.src_ips = src_ips;
		this.dstIps = dstIps;
		this.relatedIps = relatedIps;
		this.src_ports = src_ports;
		this.dst_ports = dst_ports;
		this.idRoom = idRoom;
		this.timeRoom = timeRoom;
		this.extendParams = extendParams; 
	}
	
	
	
	
	
	
	
	public StreamMidVO() {};
	
	
}
