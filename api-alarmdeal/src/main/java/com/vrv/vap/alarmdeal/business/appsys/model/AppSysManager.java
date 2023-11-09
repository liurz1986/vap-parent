package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lps 2021/8/9
 */
@Data
@Entity
@Table(name = "app_sys_manager")
@ApiModel("应用系统管理")
public class AppSysManager {

    @Id
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty(value = "应用编号")
    @Column(name = "app_no")
    private String appNo;

    @ApiModelProperty(value = "应用系统名称")
    @Column(name = "app_name")
    private String appName;

    @ApiModelProperty(value = "单位名称")
    @Column(name = "department_name")
    private String departmentName;

    @ApiModelProperty(value = "单位GUID")
    @Column(name = "department_guid")
    private String departmentGuid;

    @ApiModelProperty(value = "域名")
    @Column(name = "domain_name")
    private String domainName;


    @ApiModelProperty(value = "涉密等级")
    @Column(name = "secret_level")
    private String secretLevel;

    @ApiModelProperty(value = "涉密厂商")
    @Column(name = "secret_company")
    private String secretCompany;


    @ApiModelProperty(value = "服务器ID")
    @Column(name = "service_id")
    private String serviceId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name="data_source_type")
    private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现
    @Column(name="sync_source")
    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
    @Column(name="sync_uid")
    private String syncUid;   //外部来源主键ID

    @Column(name="app_url")
    private String appUrl;   //业务入口
    @Column(name="operation_url")
    private String operationUrl;   //管理入口





}
