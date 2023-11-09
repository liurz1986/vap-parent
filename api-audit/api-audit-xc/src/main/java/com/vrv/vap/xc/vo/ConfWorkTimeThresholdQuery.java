package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 上下班时间阈值配置
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="ConfWorkTimeThreshold对象", description="上下班时间阈值配置")
public class ConfWorkTimeThresholdQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "警种id")
    private String policeTypeId;

    @ApiModelProperty(value = "警种名称")
    private String policeTypeName;

    @ApiModelProperty(value = "上班时间")
    private String workTime;

    @ApiModelProperty(value = "上班阈值")
    private Integer onWorkThreshold;

    @ApiModelProperty(value = "下班阈值")
    private Integer offWorkThreshold;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getPoliceTypeId() {
        return policeTypeId;
    }

    public void setPoliceTypeId(String policeTypeId) {
        this.policeTypeId = policeTypeId;
    }
    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
    }
    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }
    public Integer getOnWorkThreshold() {
        return onWorkThreshold;
    }

    public void setOnWorkThreshold(Integer onWorkThreshold) {
        this.onWorkThreshold = onWorkThreshold;
    }
    public Integer getOffWorkThreshold() {
        return offWorkThreshold;
    }

    public void setOffWorkThreshold(Integer offWorkThreshold) {
        this.offWorkThreshold = offWorkThreshold;
    }

    @Override
    public String toString() {
        return "ConfWorkTimeThreshold{" +
            "id=" + id +
            ", policeTypeId=" + policeTypeId +
            ", policeTypeName=" + policeTypeName +
            ", workTime=" + workTime +
            ", onWorkThreshold=" + onWorkThreshold +
            ", offWorkThreshold=" + offWorkThreshold +
        "}";
    }
}
