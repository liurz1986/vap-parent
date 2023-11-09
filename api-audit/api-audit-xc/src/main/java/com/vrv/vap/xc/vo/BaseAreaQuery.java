package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 区域表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="BaseArea对象", description="区域表")
public class BaseAreaQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "区域编码")
    private String areaCode;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "ip范围")
    private String ipRange;

    @ApiModelProperty(value = "上级编号")
    private String parentCode;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "截取编码（确认地区）")
    private String areaCodeSub;

    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }
    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getAreaCodeSub() {
        return areaCodeSub;
    }

    public void setAreaCodeSub(String areaCodeSub) {
        this.areaCodeSub = areaCodeSub;
    }
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BaseArea{" +
            "id=" + id +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", ipRange=" + ipRange +
            ", parentCode=" + parentCode +
            ", description=" + description +
            ", areaCodeSub=" + areaCodeSub +
            ", sort=" + sort +
        "}";
    }
}
