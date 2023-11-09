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
@ApiModel(value = "数据信息管理VO")
public class DataInfoManageVo extends PageReqVap {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("数据标识", "业务类型", "涉密等级","文件名称","文件类型","文件大小(MB)","文件管理状态"));
    public static final String[] KEYS= new String[]{"dataFlag","businessType","secretLevel","fileName","fileType","fileSize","fileStatus"};
    public static final String DATA_INFO_MANAGE="数据属性";

    private Integer id;

    @ApiModelProperty(value = "数据标识")
    private String dataFlag;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String fileName; // 文件名称
    private String fileType; // 文件类型
    private String fileSize; // 文件大小 (单位MB)
    private String fileStatus; //文件管理状态 字典：b61a841f-3f90-39c6-eb54-a65f8c5261f9
    private String draftUser; // 文件起草人
    private String determineUser; // 文件定密人
    private String saleUser; // 文件签发人
    private String awareScope; // 知悉范围
    private String secretPeriod; // 保密期限
    private String determineReason; // 定密依据
    private String fileAuth;  // 文件授权
    private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现
    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
    private String syncUid;   //外部来源主键ID
}
