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
 * @since 2021-05-28
 */
@ApiModel(value="OltUserAbnormalVisited对象", description="")
public class OltUserAbnormalVisitedQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "身份证号")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "查询总次数")
    private Integer visitTotal;

    @ApiModelProperty(value = "查询次数方差")
    private Double variance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Integer getVisitTotal() {
        return visitTotal;
    }

    public void setVisitTotal(Integer visitTotal) {
        this.visitTotal = visitTotal;
    }
    public Double getVariance() {
        return variance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }

    @Override
    public String toString() {
        return "OltUserAbnormalVisited{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", userId=" + userId +
            ", userName=" + userName +
            ", visitTotal=" + visitTotal +
            ", variance=" + variance +
        "}";
    }
}
