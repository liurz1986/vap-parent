package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="EwCrossAreaNetflow对象", description="")
public class EwCrossAreaNetflowQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String srcIp;

    private String srcArea;

    private String dstIp;

    private String dstPort;

    private String dstArea;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.TIME_RANGE)
    private Date greatTime;

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
    public String getSrcArea() {
        return srcArea;
    }

    public void setSrcArea(String srcArea) {
        this.srcArea = srcArea;
    }
    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
    }
    public String getDstArea() {
        return dstArea;
    }

    public void setDstArea(String dstArea) {
        this.dstArea = dstArea;
    }
    public Date getGreatTime() {
        return greatTime;
    }

    public void setGreatTime(Date greatTime) {
        this.greatTime = greatTime;
    }

    @Override
    public String toString() {
        return "EwCrossAreaNetflow{" +
            "id=" + id +
            ", srcIp=" + srcIp +
            ", srcArea=" + srcArea +
            ", dstIp=" + dstIp +
            ", dstPort=" + dstPort +
            ", dstArea=" + dstArea +
            ", greatTime=" + greatTime +
        "}";
    }
}
