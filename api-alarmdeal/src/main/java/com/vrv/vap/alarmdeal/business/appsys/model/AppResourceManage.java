package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lps 2021/8/9
 */

@Data
@Entity
@Table(name = "app_resource_manage")
@ApiModel(value = "应用资源管理")
public class AppResourceManage {

    @Id
    private String guid;

    @ApiModelProperty(value = "资源编号")
    @Column(name="app_resource_no")
    private String appResourceNo;

    @ApiModelProperty(value = "URL")
    @Column(name="app_resource_url")
    private String appResourceUrl;

    /**
     * 1-业务，2-管理
     */
    @ApiModelProperty(value = "资源类别")
    @Column(name="resource_type")
    private Integer resourceType;

    @ApiModelProperty(value = "应用ID")
    @Column(name = "app_id")
    private Integer appId;

    @ApiModelProperty(value = "应用名称")
    @Column(name = "app_name")
    private String appName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

}
