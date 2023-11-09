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
@ApiModel(value="EmailThreaten对象", description="")
public class EmailThreatenQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "发送放区域")
    private String fromArea;

    @ApiModelProperty(value = "发送方ip")
    private String fromIp;

    @ApiModelProperty(value = "发送方地址")
    private String fromAddr;

    @ApiModelProperty(value = "威胁类型")
    private String type;

    @ApiModelProperty(value = "接收方区域")
    private String toArea;

    @ApiModelProperty(value = "接收方ip")
    private String toIp;

    @ApiModelProperty(value = "接收方地址")
    private String toAddr;

    @ApiModelProperty(value = "上报来源ip")
    private String repoerIp;

    @ApiModelProperty(value = "上报时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.TIME_RANGE)
    private Date reportTime;

    @ApiModelProperty(value = "意见")
    private String content;

    @ApiModelProperty(value = "插入时间")
    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getFromArea() {
        return fromArea;
    }

    public void setFromArea(String fromArea) {
        this.fromArea = fromArea;
    }
    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }
    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getToArea() {
        return toArea;
    }

    public void setToArea(String toArea) {
        this.toArea = toArea;
    }
    public String getToIp() {
        return toIp;
    }

    public void setToIp(String toIp) {
        this.toIp = toIp;
    }
    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }
    public String getRepoerIp() {
        return repoerIp;
    }

    public void setRepoerIp(String repoerIp) {
        this.repoerIp = repoerIp;
    }
    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "EmailThreaten{" +
            "id=" + id +
            ", fromArea=" + fromArea +
            ", fromIp=" + fromIp +
            ", fromAddr=" + fromAddr +
            ", type=" + type +
            ", toArea=" + toArea +
            ", toIp=" + toIp +
            ", toAddr=" + toAddr +
            ", repoerIp=" + repoerIp +
            ", reportTime=" + reportTime +
            ", content=" + content +
            ", insertTime=" + insertTime +
        "}";
    }
}
