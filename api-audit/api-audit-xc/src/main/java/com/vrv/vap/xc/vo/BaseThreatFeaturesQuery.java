package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="BaseThreatFeatures对象", description="")
public class BaseThreatFeaturesQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long keyId;

    @ApiModelProperty(value = "哪一类特征")
    private Integer type;

    @ApiModelProperty(value = "威胁情报的特征值")
    private String indicator;

    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getKeyId() {
        return keyId;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "BaseThreatFeaturesQuery{" +
            "id=" + id +
            ", keyId=" + keyId +
            ", type=" + type +
            ", indicator=" + indicator +
            ", remarks=" + remarks +
        "}";
    }
}
