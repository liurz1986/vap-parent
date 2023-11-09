package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.CascadeStrategyReceive;
import com.vrv.vap.admin.vo.ReportStrategyVO;
import com.vrv.vap.base.BaseService;

public interface CascadeStrategyReceiveService extends BaseService<CascadeStrategyReceive> {
/**
 *@author lilang
 *@date 2021/3/26
 *@description
 */

    Boolean saveStrategy(ReportStrategyVO strategyVO);
}
