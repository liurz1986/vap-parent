package com.vrv.vap.data.service;

import com.vrv.vap.data.model.SourceMonitor;
import com.vrv.vap.base.BaseService;

public interface SourceMonitorService extends BaseService<SourceMonitor>{

    SourceMonitor findLastBySourceId(Integer sourceId);
}
