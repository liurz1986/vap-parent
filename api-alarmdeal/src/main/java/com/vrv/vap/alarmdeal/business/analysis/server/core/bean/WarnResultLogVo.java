package com.vrv.vap.alarmdeal.business.analysis.server.core.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmModel.model.WorldAreaVO;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common.ReformModel;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2023/4/11 11:38
 * @description:
 */
@Data
public class WarnResultLogVo extends ReformModel {
    private String id;
    private String riskEventId;
    private String riskEventName;
    private String ruleId;
    private String ruleName;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date triggerTime;
    private Integer statusEnum;
    private Integer weight;
    private String relatedIps;
    private String logsInfo;
    private String dstIps;
    private String riskEventCode;
    private String eventtypelevel;
    private String resultGuid;
    private String ruleCode;
    private String src_ips;
    private String src_ports;
    private String dst_ports;
    private Integer repeatCount;
    private String deal_person;
    private String orgName;
    private String orgCode;
    private String areaName;
    private String areaCode;
    private String dstAreaCode;
    private String dstAreaName;
    private String alamDesc;
    private String tableLabel;
    private String srcMapAreaName;
    private String dstMapAreaName;
    private WorldAreaVO srcWorldMapName;
    private WorldAreaVO dstWorldMapName;
    private Map<String, Object> assetInfo;
    private Map<String, Object> appSystemInfo;
    private Map<String, String[]> idRoom;
    private String timeRoom;
    private Map<String, Object> extendParams;
    private String attackFlag;
    private String tag;
    private String multiVersions;
    private Integer failedStatus;
    private String dealAdvice;
    private String harm;
    private String threatSource;
    private String attackLine;
    private String threatCredibility;
    private String principle;
    private String dataSource;
    private String parentCode;
    private Integer tryCount;
}
