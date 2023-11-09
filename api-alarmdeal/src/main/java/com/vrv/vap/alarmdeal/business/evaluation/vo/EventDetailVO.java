package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

/**
 * 事件信息
 */
@Data
public class  EventDetailVO {
    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件详情
     */
    private EventBaseMsgVO eventBaseMsg;

    /**
     * 事件处置信息
     */
    private EventHandleMsgVO eventHandleMsg;

}
