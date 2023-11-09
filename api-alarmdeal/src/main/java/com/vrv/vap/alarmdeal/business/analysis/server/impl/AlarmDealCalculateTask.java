package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.impl.SrcIpScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;



@Configuration
@EnableScheduling
public class AlarmDealCalculateTask {
	
	private static Logger logger = LoggerFactory.getLogger(AlarmDealCalculateTask.class);

	
	@Autowired 
	private SrcIpScoreService srcIpScoreService;

	
	//@Scheduled(cron = "0 */1 * * * ?")
	@Scheduled(cron = "0 0 0 * * ? ")//每天0点开始计算
	public void check()
	{
		try {
			logger.warn("定时开始告警评分");
			srcIpScoreService.calculateSrcIpScore();
			logger.warn("结束告警评分");
		}
		catch(Exception ex) {
			logger.error(""+ex.getMessage());
		}
		
	}
}
