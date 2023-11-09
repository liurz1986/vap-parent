package com.vrv.vap.alarmdeal.business.appsys.datasync.service;

import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;

import java.util.List;

public interface AppSynchService {

    /**
     * kafka数据处理
     * @param appSysDatas
     */
    public void excDataSync(List<AppSysManagerSynchVo> appSysDatas);
}
