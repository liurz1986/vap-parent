package com.vrv.vap.admin.model;

import lombok.Data;

/**
 * kafka监控数据model
 */
@Data
public class KafkaDataInfo {
    // 每秒消息入站数
    private long messagesInPerSec;
    // 每秒消息入站流量
    private long bytesInPerSec;
    // 每秒消息出站流量
    private long bytesOutPerSec;
    // 每秒请求数
    private long produceRequestCountPerSec;
    // Consumer每秒拉取数
    private long consumerRequestCountPerSec;
    // Follower每秒拉取数
    private long flowerRequestCountPerSec;
    // Controller存活数量
    private int activeControllerCount;
    // 分区数
    private int partCount;
}
