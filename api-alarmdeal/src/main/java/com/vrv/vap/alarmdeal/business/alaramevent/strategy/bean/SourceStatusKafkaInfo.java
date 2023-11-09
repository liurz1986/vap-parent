package com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean;

import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2023/3/24 15:10
 * @description:
 */
@Data
public class SourceStatusKafkaInfo {
    private Long time;
    private Integer type;
    private List<FilterSourceStatusInfo> data;
}
