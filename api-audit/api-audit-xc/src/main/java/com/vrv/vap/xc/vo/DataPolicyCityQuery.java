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
@ApiModel(value="DataPolicyCity对象", description="")
public class DataPolicyCityQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String areaCode;

    private String areaName;

    @ApiModelProperty(value = "kafka用户名")
    private String kafkaUser;

    @ApiModelProperty(value = "kafka密码")
    private String kafkaPwd;

    @ApiModelProperty(value = "消费组")
    private String consumerGroup;

    @ApiModelProperty(value = "通讯校验码")
    private String checkCode;

    @ApiModelProperty(value = "多个ip逗号分隔")
    private String ip;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    @ApiModelProperty(value = "最后一次访问时间")
    private Date lastVisitTime;

    @ApiModelProperty(value = "是否同步：0离线1在线")
    private Integer online;

    @ApiModelProperty(value = "下级flume采集ip")
    private String flumeIp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getKafkaUser() {
        return kafkaUser;
    }

    public void setKafkaUser(String kafkaUser) {
        this.kafkaUser = kafkaUser;
    }
    public String getKafkaPwd() {
        return kafkaPwd;
    }

    public void setKafkaPwd(String kafkaPwd) {
        this.kafkaPwd = kafkaPwd;
    }
    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    public Date getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(Date lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }
    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }
    public String getFlumeIp() {
        return flumeIp;
    }

    public void setFlumeIp(String flumeIp) {
        this.flumeIp = flumeIp;
    }

    @Override
    public String toString() {
        return "DataPolicyCity{" +
            "id=" + id +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", kafkaUser=" + kafkaUser +
            ", kafkaPwd=" + kafkaPwd +
            ", consumerGroup=" + consumerGroup +
            ", checkCode=" + checkCode +
            ", ip=" + ip +
            ", modifyTime=" + modifyTime +
            ", lastVisitTime=" + lastVisitTime +
            ", online=" + online +
            ", flumeIp=" + flumeIp +
        "}";
    }
}
