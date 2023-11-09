package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.CascadeStrategy;
import com.vrv.vap.base.BaseService;

public interface CascadeStrategyService extends BaseService<CascadeStrategy> {
/**
 *@author lilang
 *@date 2021/3/25
 *@description
 */
    /**
     * 修改策略并下发下级
     * @param cascadeStrategy
     * @return
     */
    Boolean updateStrategy(CascadeStrategy cascadeStrategy);

    /**
     * 删除策略并下发下级
     * @param ids
     * @return
     */
    Boolean deleteStrategy(String ids);
}
