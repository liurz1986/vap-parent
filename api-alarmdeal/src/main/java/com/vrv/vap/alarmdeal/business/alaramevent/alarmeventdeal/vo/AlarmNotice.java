package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.analysis.vo.SoarVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsSendVO;

import com.vrv.vap.alarmdeal.frameworks.contract.syslog.SysLogVO;
import lombok.Data;

/**
 * 规则处置方式
 * @author wd-pc
 *
 */
@Data
public class AlarmNotice {
	public   static final String OPEN="1";
	public   static final String CLOSE="0";
	private String ckEmail; //邮件发送
	private String ckSms;  //短信发送
	private String assetGuids;
	private String name;
	private String ckSyslog; //syslog
	private String ckSoarInfo; //Soar打开阻断信息
	private String ckBlockInfo; //设备联动阻断信息
	private BlockVO blockVO; //设备阻断信息
	public List<MailSendVO> mailSendVO;
	public List<SmsSendVO> smsSendVO;
	public List<SysLogVO> sysLogVOList;
	public SoarVO soarVO;   //SOAR
	

	//todo  snmpTrap对应VO----snmpTrapVO
	
}
