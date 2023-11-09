package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.TimeCount;
import com.vrv.vap.alarmdeal.business.analysis.repository.TimeCountRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 时间关联
 * @author wd-pc
 *
 */
@Service
public class TimeCountService extends BaseServiceImpl<TimeCount, String> {

	@Autowired
	private TimeCountRepository timeCountRepository;
	
	
	
	@Override
	public TimeCountRepository getRepository() {
		return timeCountRepository;
	}
	
	
	
	
	
	/**
	 * 保存对应的数据
	 * @param dateList
	 */
	public void saveTimeCountList(List<String> dateList){
		List<TimeCount> list = new ArrayList<>();
		for (String date : dateList){
			List<QueryCondition> conditions = new ArrayList<>();
			conditions.add(QueryCondition.eq("timeParam", date));
			long count = count(conditions);
			if(count==0){  //说明数据库当中没有对应的时间
				TimeCount timeCount = new TimeCount();
				timeCount.setGuid(UUIDUtils.get32UUID());
				int random = new SecureRandom().nextInt(10) + 1;
				timeCount.setTimeCount(random);
				timeCount.setTimeParam(date);
				list.add(timeCount);
			}
		}
		save(list);
	}
	
	
	/**
	 * 根据日期获得对应的timeCount
	 * @param date (yyyy-mm-dd)
	 */
	public Integer getTimeCountByDate(String date){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("timeParam", date));
		List<TimeCount> list = findAll(conditions);
		if(list.size()==1){ //说明存在
			TimeCount timeCount = list.get(0);
			Integer timeNum = timeCount.getTimeCount();
			return timeNum;
		}else{
			TimeCount timeCount = new TimeCount();
			int random = new SecureRandom().nextInt(10) + 1;
			timeCount.setGuid(UUIDUtils.get32UUID());
			timeCount.setTimeCount(random);
			timeCount.setTimeParam(date);
			save(timeCount);
			return random;
		}
	}
	
	
	

}
