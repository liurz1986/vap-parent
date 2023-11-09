package com.vrv.vap.xc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("组织机构信息")
@Data
public class BaseKoalOrg implements Serializable {

    private Integer uuId;

    /**
     * 机构编码
     */
    @ApiModelProperty("机构编码")
    private String code;

    @ApiModelProperty("上级机构")
    private String parentCode;

    /**
     * 机构类型
     */
    @ApiModelProperty("机构类型")
    private String type;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String name;

    @ApiModelProperty("机构缩写")
    private String shortName;

    @ApiModelProperty("其它机构名称")
    private String otherName;

    @ApiModelProperty("状态")
    private String status;

    /**
     * 原机构代码
     */
    @ApiModelProperty("原机构代码")
    private String oldCode;

    /**
     * 启用日期
     */
    @ApiModelProperty("启用日期")
    private String startDate;

    /**
     * 停用日期
     */
    @ApiModelProperty("停用日期")
    private String endDate;

    /**
     * 原机构代码停用日期
     */
    @ApiModelProperty("原机构代码停用日期")
    private String oldCodeEnd;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 排序，默认为0
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 0： 部 1：省 2：市 3：区，默认为3
     */
    @ApiModelProperty("0： 部 1：省 2：市 3：区，默认为3")
    private Byte orgHierarchy;

    /**
     * 业务线
     */
    @ApiModelProperty("机构所属业务线")
    private String businessLine;

    /**
     * 层级关系维护码
     */
    @ApiModelProperty("层级关系维护码")
    private String subCode;


    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级")
    private Integer secretLevel;

    /**
     * 保密资格-
     */
    @ApiModelProperty("保密资格")
    private Integer secretQualifications;

    /**
     *  单位类别 1-行政机关、2-事业单位；3-国有企业；4-中央企业
     */
    @ApiModelProperty("单位类别")
    private Integer orgType;


    /**
     *  防护等级
     */
    @ApiModelProperty("防护等级")
    private Integer protectionLevel;

    /**
     * 外部系统人员表主键
     */
    @ApiModelProperty("外部系统人员表主键")
    private String uid;

    /**
     * 数据来源
     */
    @ApiModelProperty("数据来源")
    private String source;

    /**
     * 数据来源类型
     */
    @ApiModelProperty("数据来源类型")
    private Integer dataSourceType;
}