package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.DiscoverSummarise;
import com.vrv.vap.admin.vo.SummariseSearchQuery;
import com.vrv.vap.base.BaseService;

import java.util.Map;

/**
 *@author lilang
 *@date 2020/7/21
 *@description 搜索概要接口
 */
public interface SummariseService extends BaseService<DiscoverSummarise> {

    /**
     * 保存概要
     * @param discoverSummarise
     */
    void saveSummarise(DiscoverSummarise discoverSummarise);

    /**
     * 搜索概要内容
     * @return
     */
    Map<String,Object> searchContent(SummariseSearchQuery summariseSearchQuery);
}
