package com.vrv.vap.alarmdeal.business.analysis.server.core.command;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmItemDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmItemDealService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmCommandVO;
import com.vrv.vap.alarmdeal.business.analysis.enums.TypeClass;
import com.vrv.vap.alarmdeal.business.analysis.server.DealCommonLogService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.DealCommand;
import com.vrv.vap.alarmdeal.business.analysis.vo.CommandVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.jpa.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service("smsCommand")
public class SmsCommandImpl extends CommandAbs implements DealCommand {
	private static Logger logger = LoggerFactory.getLogger(MailCommandImpl.class);
	@Autowired
	private AlarmItemDealService alarmItemDealService;

	@Autowired
	private DealCommonLogService dealCommonLogService;

	@Autowired
	private AdminFeign authFeign;
	
	@Override
	public void executeCommand(AlarmCommandVO alarmCommandVO) {
		String alarmItemId = alarmCommandVO.getAlarmItemId();
		String userName = alarmCommandVO.getUserName();
		String userId = alarmCommandVO.getUserId();
		AlarmItemDeal alarmItemDeal = alarmItemDealService.getOne(alarmItemId);
		alarmItemDeal.setItemPeople(alarmCommandVO.getUserId());
		String jsonInfo = alarmItemDeal.getJsonInfo();
		new Thread(new ExecuteCommandThread(userId,userName, alarmItemId, jsonInfo)).start();
	}

	@Override
	protected void excuteCommand(CommandVO commandVO) throws JsonParseException, JsonMappingException, IOException {
		String jsonInfo = commandVO.getJsonInfo();
		String alarmItemId = commandVO.getAlarmItemId();
		List<SmsVO> list = JsonMapper.fromJsonString2List(jsonInfo, SmsVO.class);
		for (SmsVO smsVO : list){
			Map<String,Object> result= authFeign.shortMessage(smsVO.toMessage());
			ResultData<Boolean> resultData = new ResultData<>();
			resultData.setData(false);
			if(result!=null&& result.get("code").toString().equals("0")){
				resultData.setData(true);
			}else{
				resultData.setData(false);
			}
			updateAlarmDeal(resultData, TypeClass.sms, commandVO);
			dealCommonLogService.addDealLog(alarmItemId, smsVO.getId(), jsonInfo, TypeClass.sms);
			logger.info("发送短信："+smsVO.getRecipient()+",返回值："+ JSON.toJSONString(result));
		}
	
		
	}
}
