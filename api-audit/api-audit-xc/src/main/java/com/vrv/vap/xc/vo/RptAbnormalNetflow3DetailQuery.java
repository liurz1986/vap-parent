package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 外连流量异常分析详情
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="RptAbnormalNetflow3Detail对象", description="外连流量异常分析详情")
public class RptAbnormalNetflow3DetailQuery extends Query {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "应用ID(源)")
    private String systemId;

    @ApiModelProperty(value = "访问次数")
    private Integer visitCount;

    @ApiModelProperty(value = "访问总次数")
    private Integer totalCount;

    @ApiModelProperty(value = "目标IP")
    private String dstIp;

    @ApiModelProperty(value = "目标地区")
    private String dstArea;

    @ApiModelProperty(value = "访问量阈值")
    private Integer threshold;

    @ApiModelProperty(value = "数据时间")
    private String dataTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }
    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    public String getDstArea() {
        return dstArea;
    }

    public void setDstArea(String dstArea) {
        this.dstArea = dstArea;
    }
    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "RptAbnormalNetflow3DetailQuery{" +
            "id=" + id +
            ", systemId=" + systemId +
            ", visitCount=" + visitCount +
            ", totalCount=" + totalCount +
            ", dstIp=" + dstIp +
            ", dstArea=" + dstArea +
            ", threshold=" + threshold +
            ", dataTime=" + dataTime +
        "}";
    }
}
