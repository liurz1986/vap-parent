package com.vrv.vap.alarmdeal.business.kafkadeal;

/**
 * @author: 梁国露
 * @since: 2023/3/31 15:24
 * @description:
 */
public interface AlarmDealKafkaService {
    /**
     * 数据源变动
     * @param message
     */
    void sourceChange(String message);

    /**
     * 基础数据变更监听
     * @param message
     */
    void baseDataChannel(String message);

    /**
     * 告警生成监听
     * @param message
     */
    void comsumerFlinkAlarmData(String message);

    void comsumerEventTypeData(String message);
}
