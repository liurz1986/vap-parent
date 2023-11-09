package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="CtyIpCustomize对象", description="")
public class CtyIpCustomizeQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String srcIp;

    private String dstIp;

    private Date startTime;

    private Date endTime;

    private Date recordTime;

    private Date updataTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }
    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
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
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
    public Date getUpdataTime() {
        return updataTime;
    }

    public void setUpdataTime(Date updataTime) {
        this.updataTime = updataTime;
    }

    @Override
    public String toString() {
        return "CtyIpCustomize{" +
            "id=" + id +
            ", srcIp=" + srcIp +
            ", dstIp=" + dstIp +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", recordTime=" + recordTime +
            ", updataTime=" + updataTime +
        "}";
    }
}
