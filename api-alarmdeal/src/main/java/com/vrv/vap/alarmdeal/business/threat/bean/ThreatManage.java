package com.vrv.vap.alarmdeal.business.threat.bean;

import com.vrv.vap.es.model.PrimaryKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: Administrator
 * @since: 2022/8/29 15:39
 * @description:
 */
@Entity
@Data
@PrimaryKey("id")
public class ThreatManage {
    @Id
    @Column(name = "id")
    @ApiModelProperty("id")
    private String id;

    @Column(name = "ip")
    @ApiModelProperty("ip")
    private String ip;

    @Column(name = "threat_guid")
    @ApiModelProperty("威胁ID")
    private String threatGuid;

    @Column(name = "threat_name")
    @ApiModelProperty("威胁名称")
    private String threatName;

    @Column(name = "threat_value")
    @ApiModelProperty("威胁值")
    private int threatValue;

    @Column(name = "threat_level")
    @ApiModelProperty("威胁值")
    private int threatLevel;

    @Column(name = "vul_guid")
    @ApiModelProperty("脆弱性ID")
    private String vulGuid;

    @Column(name = "vul_name")
    @ApiModelProperty("脆弱性名称")
    private String vulName;

    @Column(name = "vul_value")
    @ApiModelProperty("脆弱性值")
    private int vulValue;

    @Column(name = "insert_time")
    @ApiModelProperty("数据时间")
    private String insertTime;

    @Column(name = "org_name")
    private String orgName; // 组织机构名称(单位、部门) 2021-08-20

    @Column(name = "org_code")
    private String orgCode; // 组织机构code 2021-08-20

    @Column(name = "responsible_name")
    private String responsibleName; // 责任人名称:普通用户、管理员 2021-08-20 对应用户的userName

    @Column(name = "responsible_code")
    private String responsibleCode; // 责任人名称:普通用户、管理员 2021-08-20 对应用户的userNo
}
