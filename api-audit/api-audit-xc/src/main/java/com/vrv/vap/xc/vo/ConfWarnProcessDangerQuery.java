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
@ApiModel(value="ConfWarnProcessDanger对象", description="")
public class ConfWarnProcessDangerQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "进程名")
    private String process;

    @ApiModelProperty(value = "软件名")
    private String name;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "ConfWarnProcessDanger{" +
            "id=" + id +
            ", process=" + process +
            ", name=" + name +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
