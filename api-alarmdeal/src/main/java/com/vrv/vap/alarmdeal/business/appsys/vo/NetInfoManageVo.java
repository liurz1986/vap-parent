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
@ApiModel(value = "网络信息管理VO")
public class NetInfoManageVo extends PageReqVap {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("网络名称", "网络类型", "涉密等级","安全域", "防护等级"));
    public static final String[] KEYS= new String[]{"netName","netType","secretLevel","domain","protectLevel"};
    public static final String  NET_INFO_MANAGE="网络信息";


    private Integer id;

    @ApiModelProperty(value = "网络名称")
    private String netName;

    /**
     * 局域网，广域网
     */
    @ApiModelProperty(value = "网络类型")
    private String netType;

    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;


    /**
     * 1-一级，2-二级，3-三级
     */
    @ApiModelProperty(value = "防护等级")
    private String protectLevel;

    @ApiModelProperty(value = "安全域")
    private String domain;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
