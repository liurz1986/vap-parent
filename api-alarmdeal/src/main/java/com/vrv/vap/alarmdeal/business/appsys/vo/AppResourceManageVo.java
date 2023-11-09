package com.vrv.vap.alarmdeal.business.appsys.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lps 2021/8/10
 */

@Data
@ApiModel(value = "应用资源管理Vo")
public class AppResourceManageVo extends PageReqVap {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("资源编号", "URL", "资源类别","应用系统id"));
    public static final String[] KEYS= new String[]{"appResourceNo","appResourceUrl","resourceType","appId"};
    public static final String APP_RESOURCE_MANAGE="应用资源";

    private String guid;

    @ApiModelProperty(value = "资源编号")
    private String appResourceNo;

    @ApiModelProperty(value = "URL")
    private String appResourceUrl;

    /**
     * 1-业务，2-管理
     */
    @ApiModelProperty(value = "资源类别")
    private Integer resourceType;

    @ApiModelProperty(value = "应用ID")
    private Integer appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
