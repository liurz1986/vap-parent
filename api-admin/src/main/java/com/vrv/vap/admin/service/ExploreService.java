package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.model.Edge;
import com.vrv.vap.admin.model.Entity;
import com.vrv.vap.admin.model.StatisticsModel;
import com.vrv.vap.admin.model.TrendModel;
import com.vrv.vap.admin.vo.ListQuery;
import com.vrv.vap.admin.vo.StatisticsQuery;
import com.vrv.vap.admin.vo.TrendQuery;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by lizj on 2018/07/11.
 */
public interface ExploreService extends BaseService<Entity> {

    /**
     * 查询各探索关系的统计信息
     * @param edges
     * @return
     */
    List<StatisticsModel> queryStatistics(List<Edge> edges, StatisticsQuery param);

    Map<String, Object> queryList(Edge edge, ListQuery param);

    Export.Progress exportList(Edge edge, ListQuery param, String[] fields, String[] fieldDesc, Map<String, Map<String, String>> dicMap) ;

    Map<String, Object> queryDetail(Edge edge, ListQuery param);

    List<TrendModel> queryTrend(Edge edge, TrendQuery param);
}
