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
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;
import com.vrv.vap.alarmdeal.frameworks.feign.ServerSystemFegin;
import com.vrv.vap.jpa.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 邮件发送具体命令实现
 * 
 * @author wd-pc
 *
 */

@Service("mailCommand")
public class MailCommandImpl extends CommandAbs implements DealCommand {

	private static Logger logger = LoggerFactory.getLogger(MailCommandImpl.class);

	@Autowired
	private AlarmItemDealService alarmItemDealService;
	@Autowired
	private DealCommonLogService dealCommonLogService;

	@Autowired
	private ServerSystemFegin serverSysFeign;

	@Override
	public void executeCommand(AlarmCommandVO alarmCommandVO) {
		String alarmItemId = alarmCommandVO.getAlarmItemId();
		String userName = alarmCommandVO.getUserName();
		String userId = alarmCommandVO.getUserId();
		AlarmItemDeal alarmItemDeal = alarmItemDealService.getOne(alarmItemId);
		String jsonInfo = alarmItemDeal.getJsonInfo();
		new Thread(new ExecuteCommandThread(userId,userName, alarmItemId, jsonInfo)).start();
	}


	@Override
	protected void excuteCommand(CommandVO commandVO) throws JsonParseException, JsonMappingException, IOException {
		String jsonInfo = commandVO.getJsonInfo();
		String alarmItemId = commandVO.getAlarmItemId();
		List<MailVO> list = JsonMapper.fromJsonString2List(jsonInfo, MailVO.class);
		for (MailVO mailVO : list) {
			String id = mailVO.getId();
			ResultData<Boolean> resultData = serverSysFeign.sendSimpleEmail(mailVO.toMessage());
			updateAlarmDeal(resultData, TypeClass.mail, commandVO);
			dealCommonLogService.addDealLog(alarmItemId, id, jsonInfo, TypeClass.mail);
			logger.info("发送邮件：" + mailVO.getSendTo() + ",返回值："+ JSON.toJSONString(resultData));
		}
	}

}
