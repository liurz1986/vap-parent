package com.vrv.vap.alarmdeal.business.appsys.datasync.service;

import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;

import java.util.List;


public interface DataInfoManageSyncService {

    /**
     * 执行数据同步
     * @param dataInfoManages
     */
    public void excDataSync(List<DataInfoManage> dataInfoManages) ;
}
