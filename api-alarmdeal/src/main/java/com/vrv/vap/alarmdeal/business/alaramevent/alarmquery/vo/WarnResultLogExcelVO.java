package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmModel.model.WorldAreaVO;
import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class WarnResultLogExcelVO {

    @ExcelField(title = "告警id", order = 1)
    private String id;  //ID
    @ExcelField(title = "事件分类ID", order = 1)
    private String riskEventId; //事件分类ID
    @ExcelField(title = "事件分类名称", order = 1)
    private String riskEventName; //事件分类名称
    @ExcelField(title = "规则ID", order = 1)
    private String ruleId; //规则ID
    @ExcelField(title = "规则名称", order = 1)
    private String ruleName; //规则名称
    @ExcelField(title = "告警产生时间", order = 1)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggerTime; //告警产生时间
    @ExcelField(title = "告警状态", order = 1)
    private Integer statusEnum; //告警状态
    @ExcelField(title = "告警等级", order = 1)
    private Integer weight; //告警等级
    @ExcelField(title = "关联IP", order = 1)
    private String relatedIps; //关联IP
    @ExcelField(title = "原始日志信息", order = 1)
    private String logsInfo; //原始日志信息
    @ExcelField(title = "目的IP", order = 1)
    private String dstIps; //目的IP
    @ExcelField(title = "告警规则编码", order = 1)
    private String riskEventCode; //告警规则编码
    @ExcelField(title = "事件类型等级", order = 1)
    private String eventtypelevel; //事件类型等级
    @ExcelField(title = "规则唯一标识", order = 1)
    private String resultGuid; //规则唯一标识
    @ExcelField(title = "规则编码", order = 1)
    private String ruleCode; //规则编码
    @ExcelField(title = "源IP", order = 1)
    private String src_ips; //源IP
    @ExcelField(title = "源端口", order = 1)
    private String src_ports; //源端口
    @ExcelField(title = "目的端口", order = 1)
    private String dst_ports;//目的端口
    @ExcelField(title = "合并个数", order = 1)
    private Integer repeatCount; //合并个数
    @ExcelField(title = "最后处置人", order = 1)
    private String deal_person; //最后处置人
    @ExcelField(title = "机构名称", order = 1)
    private String orgName; //机构名称
    @ExcelField(title = "机构编号", order = 1)
    private String orgCode; //机构编号
    @ExcelField(title = "区域名称（源ip安全域）", order = 1)
    private String areaName; //区域名称（源ip安全域）
    @ExcelField(title = "区域编码（源ip安全域）", order = 1)
    private String areaCode; //区域编码（源ip安全域）
    @ExcelField(title = "区域编码（目的ip安全域）", order = 1)
    private String dstAreaCode; //区域编码（目的ip安全域）
    @ExcelField(title = "区域名称（目的ip安全域）", order = 1)
    private String dstAreaName; //区域名称（目的ip安全域）
    @ExcelField(title = "告警描述", order = 1)
    private String alamDesc; //告警描述
    @ExcelField(title = "告警label", order = 1)
    private String tableLabel; //告警label
    @ExcelField(title = "源区域地址", order = 1)
    private String srcMapAreaName; //源区域地址（世界域源地址）
    @ExcelField(title = "目的区域地址", order = 1)
    private String dstMapAreaName; //目的区域地址（世界目的地址）
    @ExcelField(title = "源区域地址", order = 1)
    private WorldAreaVO srcWorldMapName; //源区域地址（世界域源地址）(实体格式)
    @ExcelField(title = "目的区域地址", order = 1)
    private WorldAreaVO dstWorldMapName; //目的区域地址（世界目的地址）(实体格式)
    @ExcelField(title = "包括资产数和资产对应的guide", order = 1)
    private Map<String,Object> assetInfo; //包括资产数和资产对应的guide
    @ExcelField(title = "应用系统", order = 1)
    private Map<String,Object> appSystemInfo; //应用系统
    @ExcelField(title = "id盒子", order = 1)
    private Map<String,String[]> idRoom; //id盒子
    @ExcelField(title = "额外的参数", order = 1)
    private Map<String,Object> extendParams; //额外的参数
    @ExcelField(title = "是否为攻击告警", order = 1)
    private String attackFlag; //是否为攻击告警
    @ExcelField(title = "标签", order = 1)
    private String tag; //标签
    @ExcelField(title = "综合版本", order = 1)
    private String multiVersions; //综合版本
    @ExcelField(title = "失陷状态", order = 1)
    private Integer  failedStatus;  //失陷状态
    @ExcelField(title = "处理意见", order = 1)
    private String dealAdvice; //处理意见
    @ExcelField(title = "危害", order = 1)
    private String harm;  //危害
    @ExcelField(title = "威胁来源", order = 1)
    private String  threatSource;   // 威胁来源
}
