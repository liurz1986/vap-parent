package com.vrv.vap.alarmdeal.business.analysis.server.core.mq;

/**
 * 告警发送信息
 * @author wd-pc
 *
 */

public class AlarmDealPubRabbitConfiguration {

    public static final String DEFAULT_DIRECT_EXCHANGE = "alarmdeal.direct";
    public static final String POINT_KEY = "auto_flow_key";
    public static final String DEAD_POINT_QUEUE = "d.flow";
    public static final String DEAD_POINT_KEY = "a0b1d08b-4ccd-11e7-9226-1242ac14";
    public static final String AutoFlowEvent = "auto-flow-event";  //自动发送流程事件
    

}
