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
 * @since 2021-05-24
 */
@ApiModel(value="KafkaSubscriber对象", description="")
public class KafkaSubscriberQuery extends Query {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订阅者名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String system;

    @ApiModelProperty(value = "订阅者IP")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String ip;

    @ApiModelProperty(value = "订阅主题")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String topic;

    @ApiModelProperty(value = "开启")
    private Integer avaiable;

    @ApiModelProperty(value = "消费组")
    private String groupId;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

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
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
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
        return "KafkaSubscriberQuery{" +
            "id=" + id +
            ", system=" + system +
            ", ip=" + ip +
            ", topic=" + topic +
            ", avaiable=" + avaiable +
            ", groupId=" + groupId +
            ", addTime=" + addTime +
            ", updateTime=" + updateTime +
            ", remark=" + remark +
        "}";
    }
}
