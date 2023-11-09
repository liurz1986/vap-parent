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
@ApiModel(value="OltObjectanalysis对象", description="")
public class OltObjectanalysisQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String content;

    private Integer count;

    @ApiModelProperty(value = "是否是警员(1是,0否)")
    private Integer isPolice;

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
    public Integer getIsPolice() {
        return isPolice;
    }

    public void setIsPolice(Integer isPolice) {
        this.isPolice = isPolice;
    }

    @Override
    public String toString() {
        return "OltObjectanalysis{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", content=" + content +
            ", count=" + count +
            ", isPolice=" + isPolice +
        "}";
    }
}
