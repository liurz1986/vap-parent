package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.EventAlarmSettingRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventAlarmSettingService;

import java.util.List;

@Service
public class EventAlarmSettingServiceImpl extends BaseServiceImpl<EventAlarmSetting, String> implements EventAlarmSettingService {

	@Autowired EventAlarmSettingRespository eventAlarmSettingRespository;
	@Override
	public BaseRepository<EventAlarmSetting, String> getRepository() {
		// TODO Auto-generated method stub
		return eventAlarmSettingRespository;
	}

	@Override
	public List<EventAlarmSetting> queryAllEventAlarmSetting() {
		return findAll();
	}
}
