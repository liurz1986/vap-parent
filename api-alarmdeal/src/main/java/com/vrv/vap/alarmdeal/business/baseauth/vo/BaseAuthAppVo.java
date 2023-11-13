package com.vrv.vap.alarmdeal.business.baseauth.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 审批类型基础配置表
 *
 * @author liurz
 * @date 202308
 */
@Data
@ApiModel(value = "打印刻录审批表")
public class BaseAuthAppVo implements Serializable {
    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("应用系统名称", "内部授权ip", "外部授权ip"));
    public static final String[] KEYS= new String[]{"appName","insideIp","outIp"};
    public static final String INFO_MANAGE="应用访问权限审批信息";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer appId;
    private String ip ;
    private String insideIp;
    private String outIp;
    private Integer type ;
    private String appName;
    private Date createTime; //创建时间
}
