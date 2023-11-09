package com.vrv.logVO.alarmdeal;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月13日 下午6:11:02 
* 类说明     
*/
@LogDesc(value="告警信息",tableName="warn_result",topicName="alarmdeal-returnflow")
@Data
public class WarnResultLogVO {
   
	private String id;  //ID
	private String riskEventId; //事件分类ID
	private String riskEventName; //事件分类名称
	private String ruleId; //规则ID
	private String ruleName; //规则名称
	//private Date triggerTime; //告警产生时间
    private Integer statusEnum; //告警状态
    private Integer weight; //告警等级
    private String relatedIps; //关联IP
    private String logsInfo; //原始日志信息
    private String dstIps; //目的IP
	private String riskEventCode; //告警规则编码
	private String eventtypelevel; //事件类型等级
    private String resultGuid; //规则唯一标识
    private String ruleCode; //规则编码
    private String src_ips; //源IP
    private String src_ports; //源端口
    private String dst_ports;//目的端口
    private Integer repeatCount; //合并个数
    private String deal_person; //最后处置人

    private String orgName; //机构名称
    private String orgCode; //机构编号

    private String areaName; //区域名称（源ip安全域）
    private String areaCode; //区域编码（源ip安全域）
    
    private String dstAreaCode; //区域编码（目的ip安全域）
    private String dstAreaName; //区域名称（目的ip安全域）
    
    private String alamDesc; //告警描述
    private String tableLabel; //告警label
    
    private String srcMapAreaName; //源区域地址（世界域源地址）
    private String dstMapAreaName; //目的区域地址（世界目的地址）
    private WorldAreaVO srcWorldMapName; //源区域地址（世界域源地址）(实体格式)
    private WorldAreaVO dstWorldMapName; //目的区域地址（世界目的地址）(实体格式)
    private Map<String,Object> assetInfo; //包括资产数和资产对应的guide
    private Map<String,Object> appSystemInfo; //应用系统
    private String attackFlag; //是否为攻击告警
    private String tag; //标签
    private Timestamp eventTime;//处理时间
    
}
