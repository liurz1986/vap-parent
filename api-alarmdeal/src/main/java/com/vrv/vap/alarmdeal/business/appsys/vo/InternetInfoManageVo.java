package com.vrv.vap.alarmdeal.business.appsys.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lps 2021/8/10
 */

@Data
@ApiModel(value = "互联网信息管理VO")
public class InternetInfoManageVo extends PageReqVap {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("互联单位", "接入方式", "涉密等级", "防护等级"));
    public static final String[] KEYS= new String[]{"internetName","internetType","secretLevel","protectLevel"};
    public static final String INTERNET_INFO_MANAGE="互联网信息";

    private Integer id;

    @ApiModelProperty(value = "互联网单位名称")
    private String internetName;

    /**
     * 远程登录  网络接入
     */
    @ApiModelProperty(value = "接入方式")
    private String internetType;


    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;

    /**
     * 1-一级，2-二级，3-三级
     */
    @ApiModelProperty(value = "防护等级")
    private String protectLevel;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
