package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 热点库
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="ConfHotList对象", description="热点库")
public class ConfHotListQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "热点内容")
    private String content;

    @ApiModelProperty(value = "状态 0-禁用，1-启用")
    private Integer state;

    @ApiModelProperty(value = "有效开始日期")
    private Date startDate;

    @ApiModelProperty(value = "有效截止日期")
    private Date endDate;

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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
        return "ConfHotList{" +
            "id=" + id +
            ", content=" + content +
            ", state=" + state +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", description=" + description +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
