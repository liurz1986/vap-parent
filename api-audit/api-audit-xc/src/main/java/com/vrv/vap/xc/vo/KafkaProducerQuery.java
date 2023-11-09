package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-06-04
 */
@ApiModel(value = "KafkaProducer对象", description = "")
public class KafkaProducerQuery extends Query {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    @ApiModelProperty(value = "生产者系统名称")
    private String system;

    @ApiModelProperty(value = "生产者IP")
    private String ip;

    @ApiModelProperty(value = "主题")
    private String topic;

    @ApiModelProperty(value = "开启")
    private Integer avaiable;

    @ApiModelProperty(value = "添加时间")
    private LocalDateTime addTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getAvaiable() {
        return avaiable;
    }

    public void setAvaiable(Integer avaiable) {
        this.avaiable = avaiable;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "KafkaProducer{" +
                "id=" + id +
                ", system=" + system +
                ", ip=" + ip +
                ", topic=" + topic +
                ", avaiable=" + avaiable +
                ", addTime=" + addTime +
                ", updateTime=" + updateTime +
                ", remark=" + remark +
                "}";
    }
}
