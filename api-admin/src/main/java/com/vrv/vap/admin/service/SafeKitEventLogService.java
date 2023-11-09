package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SyncBaseData;

/**
 * @author lilang
 * @date 2023/4/17
 * @description
 */
public interface SafeKitEventLogService {

    public void produce(SyncBaseData syncBaseData);
}
