package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2023-03-29
 */
@ApiModel(value="BehaviorAnalysisModel对象", description="")
public class BehaviorAnalysisModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "基线id")
    private Integer baseLineId;

    @ApiModelProperty(value = "配置信息")
    private String config;

    @ApiModelProperty(value = "事件id")
    private String ruleId;

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getBaseLineId() {
        return baseLineId;
    }

    public void setBaseLineId(Integer baseLineId) {
        this.baseLineId = baseLineId;
    }
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BehaviorAnalysisModel{" +
            "id=" + id +
            ", baseLineId=" + baseLineId +
            ", config=" + config +
            ", ruleId=" + ruleId +
            ", param=" + param +
            ", createTime=" + createTime +
        "}";
    }
}
