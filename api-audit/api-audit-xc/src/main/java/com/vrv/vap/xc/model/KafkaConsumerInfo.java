package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * kafak消费信息model
 */
@ApiModel("kafak消费信息模型")
public class KafkaConsumerInfo extends BaseModel {

    @ApiModelProperty("消费组")
    private String group;
    @ApiModelProperty("主题")
    private String topic;
    @ApiModelProperty("分区")
    private String partition;
    @ApiModelProperty("当前消费偏移量")
    private String currentOffset;
    @ApiModelProperty("最新数据偏移量")
    private String logEndOffset;
    @ApiModelProperty("数据堆积量")
    private String lag;
    @ApiModelProperty("消费者ID")
    private String consumerId;
    @ApiModelProperty("消费者IP")
    private String host;
    @ApiModelProperty("客户端ID")
    private String clientId;
    @ApiModelProperty("当前状态")
    private String status;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(String currentOffset) {
        this.currentOffset = currentOffset;
    }

    public String getLogEndOffset() {
        return logEndOffset;
    }

    public void setLogEndOffset(String logEndOffset) {
        this.logEndOffset = logEndOffset;
    }

    public String getLag() {
        return lag;
    }

    public void setLag(String lag) {
        this.lag = lag;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status != null ? status : (consumerId == null ? "0" : "1");
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
