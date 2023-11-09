package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
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
 * @since 2021-05-25
 */
@ApiModel(value="FallIp对象", description="")
public class FallIpQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String taskId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "端口访问次数")
    private String portCount;

    @ApiModelProperty(value = "url访问次数")
    private String urlCount;

    @ApiModelProperty(value = "进程状态0运行1没运行")
    private String processCount;

    @ApiModelProperty(value = "满足条件个数")
    private String count;

    @ApiModelProperty(value = "开始时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.MORE_THAN)
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LESS_THAN)
    private Date endTime;

    @ApiModelProperty(value = "创建时间")
    private Date greatTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    public String getPortCount() {
        return portCount;
    }

    public void setPortCount(String portCount) {
        this.portCount = portCount;
    }
    public String getUrlCount() {
        return urlCount;
    }

    public void setUrlCount(String urlCount) {
        this.urlCount = urlCount;
    }
    public String getProcessCount() {
        return processCount;
    }

    public void setProcessCount(String processCount) {
        this.processCount = processCount;
    }
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getGreatTime() {
        return greatTime;
    }

    public void setGreatTime(Date greatTime) {
        this.greatTime = greatTime;
    }

    @Override
    public String toString() {
        return "FallIp{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", ip=" + ip +
            ", portCount=" + portCount +
            ", urlCount=" + urlCount +
            ", processCount=" + processCount +
            ", count=" + count +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", greatTime=" + greatTime +
        "}";
    }
}
