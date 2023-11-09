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
@ApiModel(value="OltUserAbnormalVisitedDetail对象", description="")
public class OltUserAbnormalVisitedDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "身份证号")
    private String userId;

    @ApiModelProperty(value = "日期")
    private String dataTime;

    @ApiModelProperty(value = "查询次数")
    private Integer visitTotal;

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
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public Integer getVisitTotal() {
        return visitTotal;
    }

    public void setVisitTotal(Integer visitTotal) {
        this.visitTotal = visitTotal;
    }

    @Override
    public String toString() {
        return "OltUserAbnormalVisitedDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", userId=" + userId +
            ", dataTime=" + dataTime +
            ", visitTotal=" + visitTotal +
        "}";
    }
}
