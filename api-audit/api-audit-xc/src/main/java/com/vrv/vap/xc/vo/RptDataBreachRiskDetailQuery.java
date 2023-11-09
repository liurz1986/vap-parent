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
 * @since 2021-05-28
 */
@ApiModel(value="RptDataBreachRiskDetail对象", description="")
public class RptDataBreachRiskDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "地区编码")
    private String areaCode;

    @ApiModelProperty(value = "操作时间")
    private Date opTime;

    @ApiModelProperty(value = "数据类型")
    private String dataType;

    @ApiModelProperty(value = "其他")
    private String other;

    @ApiModelProperty(value = "时间类型，1-上班,2-下班")
    private Integer timeType;

    @ApiModelProperty(value = "记录时间")
    private Date recordTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "RptDataBreachRiskDetail{" +
            "id=" + id +
            ", ip=" + ip +
            ", areaCode=" + areaCode +
            ", opTime=" + opTime +
            ", dataType=" + dataType +
            ", other=" + other +
            ", timeType=" + timeType +
            ", recordTime=" + recordTime +
        "}";
    }
}
