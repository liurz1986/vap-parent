package com.vrv.vap.alarmdeal.business.evaluation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 事件详情
 */
@Data
public class EventBaseMsgVO {
    /**
     * 告警发生时间
     */
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date eventCreattime;
    /**
     * 事件类型
     */
    private String eventTypeName;
    /**
     * 事件详情
     */
    private String eventDetails;
}
