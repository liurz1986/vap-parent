package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model.EventDealBusiData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.IEventDealBusiDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository.EventDealBusiDataRepository;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventDealBusiDataServiceImpl extends BaseServiceImpl<EventDealBusiData,String> implements IEventDealBusiDataService {
    @Autowired
    private EventDealBusiDataRepository eventDealBusiDataRepository;
    @Override
    public BaseRepository<EventDealBusiData, String> getRepository() {
        return eventDealBusiDataRepository;
    }

    @Override
    public EventDealBusiData findOneByEventId(String eventId) {
        //查询条件集合
        List<QueryCondition> conditions=new ArrayList<>();
        conditions.add(QueryCondition.eq("eventId",eventId));
        //查询获取数据
        List<EventDealBusiData> list = findAll(conditions);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }
}
