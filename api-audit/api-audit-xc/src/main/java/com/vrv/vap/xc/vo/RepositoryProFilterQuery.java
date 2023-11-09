package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="RepositoryProFilter对象", description="")
public class RepositoryProFilterQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "游戏进程")
    private String process;

    @ApiModelProperty(value = "进程描述")
    private String description;

    @ApiModelProperty(value = "进程类型：5.游戏进程；7.危险进程")
    private Integer type;

    @ApiModelProperty(value = "状态：1启用，0停用")
    private Integer state;

    @ApiModelProperty(value = "最后更新时间")
    private String lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "RepositoryProFilterQuery{" +
            "id=" + id +
            ", process=" + process +
            ", description=" + description +
            ", type=" + type +
            ", state=" + state +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
