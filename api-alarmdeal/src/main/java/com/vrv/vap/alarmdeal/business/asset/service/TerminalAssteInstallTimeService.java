package com.vrv.vap.alarmdeal.business.asset.service;


import com.vrv.vap.alarmdeal.business.asset.model.TerminalAssteInstallTime;
import com.vrv.vap.alarmdeal.business.asset.vo.TerminalAssteInstallTimeJobVO;
import com.vrv.vap.jpa.baseservice.BaseService;

public interface TerminalAssteInstallTimeService extends BaseService<TerminalAssteInstallTime, String> {
    public void excTerminalAssteInstallTime(TerminalAssteInstallTimeJobVO data);
}
