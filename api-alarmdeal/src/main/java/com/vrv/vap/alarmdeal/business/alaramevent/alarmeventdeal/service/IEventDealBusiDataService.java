package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model.EventDealBusiData;
import com.vrv.vap.jpa.baseservice.BaseService;

/**
 * 事件处置业务数据服务接口
 */
public interface IEventDealBusiDataService extends BaseService<EventDealBusiData,String> {
    EventDealBusiData findOneByEventId(String eventId);
}
