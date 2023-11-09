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
 * @since 2021-05-26
 */
@ApiModel(value="DataPolicy对象", description="")
public class DataPolicyQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String areaCode;

    @ApiModelProperty(value = "类别: 1省级下发 2市级上报")
    private Integer levelType;

    @ApiModelProperty(value = "数据策略: 0未开启 1开启  [{index:netflow,status:1}] ")
    private String dataPolicy;

    @ApiModelProperty(value = "不下发应用 多个应用id 逗号分隔")
    private String notDownPolicy;

    @ApiModelProperty(value = "不上报应用 多个应用id 逗号分隔")
    private String uploadPolicy;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    @ApiModelProperty(value = "是否同步：0未同步1已同步")
    private Integer synchronize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public Integer getLevelType() {
        return levelType;
    }

    public void setLevelType(Integer levelType) {
        this.levelType = levelType;
    }
    public String getDataPolicy() {
        return dataPolicy;
    }

    public void setDataPolicy(String dataPolicy) {
        this.dataPolicy = dataPolicy;
    }
    public String getNotDownPolicy() {
        return notDownPolicy;
    }

    public void setNotDownPolicy(String notDownPolicy) {
        this.notDownPolicy = notDownPolicy;
    }
    public String getUploadPolicy() {
        return uploadPolicy;
    }

    public void setUploadPolicy(String uploadPolicy) {
        this.uploadPolicy = uploadPolicy;
    }
    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    public Integer getSynchronize() {
        return synchronize;
    }

    public void setSynchronize(Integer synchronize) {
        this.synchronize = synchronize;
    }

    @Override
    public String toString() {
        return "DataPolicy{" +
            "id=" + id +
            ", areaCode=" + areaCode +
            ", levelType=" + levelType +
            ", dataPolicy=" + dataPolicy +
            ", notDownPolicy=" + notDownPolicy +
            ", uploadPolicy=" + uploadPolicy +
            ", modifyTime=" + modifyTime +
            ", synchronize=" + synchronize +
        "}";
    }
}
