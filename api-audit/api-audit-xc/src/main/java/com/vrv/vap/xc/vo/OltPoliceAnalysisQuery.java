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
@ApiModel(value="OltPoliceAnalysis对象", description="")
public class OltPoliceAnalysisQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String content;

    private Integer count;

    @ApiModelProperty(value = "警种")
    private String policeType;

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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }

    @Override
    public String toString() {
        return "OltPoliceAnalysis{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", content=" + content +
            ", count=" + count +
            ", policeType=" + policeType +
        "}";
    }
}
