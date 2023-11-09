package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-21
 */
@ApiModel(value="OltNetvisited对象", description="")
public class OltNetvisitedQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "ip地址")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String ip;

    @ApiModelProperty(value = "平均查询次数")
    private Integer avgCount;

    @ApiModelProperty(value = "查询次数")
    private Integer count;

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
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getAvgCount() {
        return avgCount;
    }

    public void setAvgCount(Integer avgCount) {
        this.avgCount = avgCount;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "OltNetvisited{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", ip=" + ip +
            ", avgCount=" + avgCount +
            ", count=" + count +
        "}";
    }
}
