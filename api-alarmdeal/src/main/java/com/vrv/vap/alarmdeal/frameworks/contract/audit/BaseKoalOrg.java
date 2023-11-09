package com.vrv.vap.alarmdeal.frameworks.contract.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
public class BaseKoalOrg {

    @ApiModelProperty("组织机构id，主键")
    @Id
    @Column(name = "uu_id")
    private Integer uuId;

    /**
     * 机构编码
     */
    @ApiModelProperty("机构编码")
    private String code;

    @ApiModelProperty("上级机构编码")
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
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    /**
     * 排序，默认为0
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 0： 部 1：省 2：市 3：区，默认为3
     */
    @ApiModelProperty("0： 部 1：省 2：市 3：区，默认为3")
    private Byte orghierarchy;

    

    /**
     * 层级关系维护码
     */
    @ApiModelProperty("层级关系维护码")
    private String subCode;

}