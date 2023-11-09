package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.model.VisualDatabaseConnection;
import com.vrv.vap.admin.vo.DatabaseQuery;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

public interface VisualDatabaseService extends BaseService<VisualDatabaseConnection> {

    /**
     * 根据拼装json生成查询sql
     * @param databaseQuery
     * @return
     */
    String generateQuerySql(DatabaseQuery databaseQuery);

    /**
     * 执行拼装sql
     * @param sql
     * @return
     */
    List<Map> queryData(String sql);

    /**
     * 生成查询数据详情sql
     * @param databaseQuery
     * @return
     */
    String generateDataDetailSql(DatabaseQuery databaseQuery);

    /**
     * 生成详情条数sql
     * @param queryJsonStr
     * @return
     */
    String generateDataCountSql(String queryJsonStr);

    /**
     * 查询数据条数
     * @param sql
     * @return
     */
    Integer queryDataCount(String sql);

    /**
     * 导出小组件数据
     */
    Export.Progress exportList(DatabaseQuery databaseQuery);

}
