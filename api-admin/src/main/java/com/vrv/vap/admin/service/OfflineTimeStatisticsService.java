package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.OfflineTimeStatistics;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsQuery;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsVo;
import com.vrv.vap.base.BaseService;

import java.util.List;

public interface OfflineTimeStatisticsService extends BaseService<OfflineTimeStatistics> {

    List<OfflineTimeStatisticsVo> calcOfflineTime(OfflineTimeStatisticsQuery query);

}
