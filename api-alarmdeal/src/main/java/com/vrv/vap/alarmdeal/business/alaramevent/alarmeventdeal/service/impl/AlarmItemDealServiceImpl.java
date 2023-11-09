package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmItemDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository.AlarmItemDealRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmItemDealService;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
@Service
public class AlarmItemDealServiceImpl extends BaseServiceImpl<AlarmItemDeal, String> implements AlarmItemDealService {

   	@Autowired
   	private AlarmItemDealRespository alarmItemDealRespository;
	
	@Override
	public AlarmItemDealRespository getRepository() {
		return alarmItemDealRespository;
	}

}
