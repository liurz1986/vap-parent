package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.Label;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.ThreeinOneInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import com.vrv.vap.es.model.PrimaryKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@PrimaryKey(value = "eventId")
public class AlarmEventAttribute extends EventBaseAttribute {

    //标签
    @ApiModelProperty(value = "标签")
    List<Label> labels;

    //有效期
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期 处置时限")
    Date validityDate;

    @ApiModelProperty(value = "处理状态")
    Integer alarmDealState;

    //是否督促
    @ApiModelProperty(value = "是否督促")
    Boolean isUrge;
    //是否协办
    @ApiModelProperty(value = "是否协办")
    Boolean isAssist = false;

    //是否督办
    @ApiModelProperty(value = "是否督办")
    Boolean isSupervise;

    @ApiModelProperty(value = "主体Ip,攻击事件中和源ip关联，被攻击事件种和目的ip关联")
    String principalIp;
    @ApiModelProperty(value = "principalIp计算值")
    @EsField("principalIpNum")
    private Long principalIpNum;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "督办时间")
    Date superviseTime;

    //已读
    @ApiModelProperty(value = "是否已读")
    Boolean isRead;

    @ApiModelProperty(value = "事件编码")
    String eventCode;

    @ApiModelProperty(value = "事件分类id")
    String categoryId;

    @ApiModelProperty(value = "事件规则id")
    String ruleId;

    @ApiModelProperty(value = "事件规则Name")
    String ruleName;

    @ApiModelProperty(value = "风险等级")
    Integer alarmRiskLevel;

    @ApiModelProperty(value = "数据来源")
    String dataSource;


    String dstIps;
    String srcIps;

    @ApiModelProperty(value = "督促详情")
    List<AlarmEventUrge> urgeInfos;

    @ApiModelProperty(value = "权限控制")
    AuthorizationControl authorization;

    @ApiModelProperty(value = "事件原始日志相关信息")
    List<LogIdVO> logs;

    @EsField("notice_desc")
    @ApiModelProperty(value = "级联描述")
    String noticeDesc;

    @ApiModelProperty(value = "成因分析")
    String causeAnalysis;

    @ApiModelProperty(value = "聚合字段")
    String aggField;
    @ApiModelProperty(value = "工单拼接时间戳，用于创建工单不重复性")
    String ticketJoinStamp;

    @ApiModelProperty(value = "规则ID")
    String filterCode;
    //todo 20230714增加的字段  扩展信息 每个事件对应不同的扩展信息，如违规外联，需对应三合一版本号，三合一厂商编号
    @EsField("extention")
    List<ThreeinOneInfo> extention = new ArrayList<>();


}


