package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.DiscoverRecord;
import com.vrv.vap.base.BaseService;

/**
 * Created by CodeGenerator on 2018/07/11.
 */
public interface DiscoverRecordService extends BaseService<DiscoverRecord> {

    /**
     * 保存记录
     * @param record
     * @return
     */
    boolean saveRecord(DiscoverRecord record);
}
