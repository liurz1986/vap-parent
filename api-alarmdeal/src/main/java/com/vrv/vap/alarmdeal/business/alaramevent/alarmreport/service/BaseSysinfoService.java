package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.common.model.User;

import java.util.List;
import java.util.Set;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月20日 10:09
 */
public interface BaseSysinfoService {
    public void addUserAssetPermissions(AnalysisVO analysisVO);
    public void addUserPermissions(AnalysisVO analysisVO);
    public User getCurrentUser();
    public void addUserAppPermissions(AnalysisVO analysisVO);
    public void setTimes(String timeType, AnalysisVO analysisVO);
    public Set<String> getIpList(String[] systemIds);
    public List<String> getIpList(String systemId);
    public List<BaseSysinfo> getAllApplication();
}
