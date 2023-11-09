package com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl;

import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.analysis.server.core.service.DealCommand;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmCommandVO;

/**
 * 命令调用者
 * @author wd-pc
 *
 */
@Service
public class AlarmDealCommandInvoker {
	private static final ThreadLocal<DealCommand> dealCommandThreadLocal = new ThreadLocal<>();
       
    public void setDealCommand(DealCommand dealCommand){
		dealCommandThreadLocal.set(dealCommand);
    }

	public void executeCommand(AlarmCommandVO alarmCommandVO){
		dealCommandThreadLocal.get().executeCommand(alarmCommandVO);
		dealCommandThreadLocal.remove();
	}

}
