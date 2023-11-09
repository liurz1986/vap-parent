package com.vrv.vap.alarmdeal.business.analysis.server.core.command;

import java.io.IOException;

import com.vrv.vap.alarmdeal.business.analysis.vo.CommandVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.UpdateAlarmDealVO;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmDealServer;

/**
 * 邮件短信抽象类
 * @author vrv
 *
 */
public abstract class CommandAbs {

	@Autowired
	private AlarmDealServer alarmDealServer;
	
	
	/**
	 * 执行对应的命令
	 * @param commandVO
	 */
	protected  abstract void excuteCommand(CommandVO commandVO) throws JsonParseException, JsonMappingException, IOException;
   
	
	/**
	 * 更新updateAlarmdeal
	 * @param result
	 * @param itemType
	 * @param commandVO
	 */
	protected void updateAlarmDeal(ResultData<Boolean> result,String itemType,CommandVO commandVO) {
		UpdateAlarmDealVO updateAlarmDealVO = new UpdateAlarmDealVO();
		updateAlarmDealVO.setAlarmItemId(commandVO.getAlarmItemId());
		updateAlarmDealVO.setItemType(itemType);
		updateAlarmDealVO.setResultData(result);
		updateAlarmDealVO.setUserId(commandVO.getUserId());
		updateAlarmDealVO.setUserName(commandVO.getUserName());
		alarmDealServer.updateAlarmDeal(updateAlarmDealVO);
	}
	
	/**
	 * 执行命令
	 * @author vrv
	 *
	 */
	public class ExecuteCommandThread implements Runnable {
		private String userName;
		private String userId;
		private String alarmItemId;
		private String jsonInfo;

		public ExecuteCommandThread(String userId,String userName, String alarmItemId, String jsonInfo) {
			this.userId = userId;
			this.userName = userName;
			this.alarmItemId = alarmItemId;
			this.jsonInfo = jsonInfo;
		}

		@Override
		public void run() {
			try {
				CommandVO commandVO = new CommandVO();
				commandVO.setAlarmItemId(alarmItemId);
				commandVO.setJsonInfo(jsonInfo);
				commandVO.setUserId(userId);
				commandVO.setUserName(userName);
				excuteCommand(commandVO);;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
