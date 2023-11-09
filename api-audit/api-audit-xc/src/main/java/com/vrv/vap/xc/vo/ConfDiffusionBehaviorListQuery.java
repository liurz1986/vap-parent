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
@ApiModel(value="ConfDiffusionBehaviorList对象", description="")
public class ConfDiffusionBehaviorListQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "数据源类型：1-进程，2-端口，3-url")
    private String sourceType;

    @ApiModelProperty(value = "数据类型：1-黑名单，2-白名单")
    private String dataType;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "ConfDiffusionBehaviorList{" +
            "id=" + id +
            ", sourceType=" + sourceType +
            ", dataType=" + dataType +
            ", content=" + content +
            ", remark=" + remark +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
