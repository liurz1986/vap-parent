package com.vrv.vap.alarmdeal.business.analysis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "event_alarm_setting")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventAlarmSetting {
    @Id
    @Column(name = "guid", length = 50)
    private String guid;

    @Column(name = "link_guid")
    @ApiModelProperty(value = "关联的id")
    private String linkGuid;

    @Column(name = "link_type")
    @ApiModelProperty(value = "关联的类型：category（分类）、rule（规则）")
    private String linkType;

    //
    @ApiModelProperty(value = "规则path：category填写code_level，rule填写warmType/id")
    @Column(name = "rule_path")
    private String rulePath;

    @Column(name = "is_urge")
    @ApiModelProperty(value = "是否督促")
    private Boolean isUrge;

    @Column(name = "time_limit_num")
    @ApiModelProperty(value = "限期时间")
    private Integer timeLimitNum;

    @Column(name = "urge_reason")
    @ApiModelProperty(value = "督促理由")
    private String urgeReason;

    @Column(name = "to_role")
    @ApiModelProperty(value = "通知哪个角色")
    private String toRole;

    @Column(name = "to_user")
    @ApiModelProperty(value = "通知谁")
    private String toUser;

    @Column(name = "to_asset_user")
    @ApiModelProperty(value = "是否关联资产责任人")
    private Boolean toAssetUser;

    @Column(name = "rule_code")
    @ApiModelProperty(value = "策略ID")
    private String ruleCode;

}
