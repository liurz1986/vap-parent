package com.vrv.vap.admin.common.enums;

import lombok.Getter;

/**
 * kafka监控数据枚举
 */
public enum KafkaMetricsEnum {
    MESSAGE_IN_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec"),
    BYTES_IN_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec"),
    BYTES_OUT_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec"),
    PRODUCE_REQUEST_PER_SEC("kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce"),
    CONSUMER_REQUEST_PER_SEC("kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer"),
    FLOWER_REQUEST_PER_SEC("kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchFollower"),
    ACTIVE_CONTROLLER_COUNT("kafka.controller:type=KafkaController,name=ActiveControllerCount"),
    PART_COUNT("kafka.server:type=ReplicaManager,name=PartitionCount");

    @Getter
    private String metric;

    KafkaMetricsEnum(String metric) {
        this.metric = metric;
    }
}
