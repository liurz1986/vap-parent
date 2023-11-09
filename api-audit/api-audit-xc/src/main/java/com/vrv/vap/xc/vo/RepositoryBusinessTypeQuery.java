package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="RepositoryBusinessType对象", description="")
public class RepositoryBusinessTypeQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "该类型对应的警种范围")
    private String policeTypeRange;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    public String getPoliceTypeRange() {
        return policeTypeRange;
    }

    public void setPoliceTypeRange(String policeTypeRange) {
        this.policeTypeRange = policeTypeRange;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "RepositoryBusinessType{" +
            "id=" + id +
            ", businessType=" + businessType +
            ", policeTypeRange=" + policeTypeRange +
            ", description=" + description +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
