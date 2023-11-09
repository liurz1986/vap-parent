package com.vrv.vap.data.service;

import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.data.common.excel.out.Export;
import com.vrv.vap.data.vo.*;
import org.elasticsearch.client.Response;

import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface ContentService {

    /**
     * 通过 通用查询结构  生成 查询语句 (Elastic Search版本)
     */
    CommonResponse generateElastic(CommonRequest query) throws ApiException;

    /**
     * 通过 通用查询结构  生成 查询语句 （MySql 版本）
     */
    CommonResponse generateSQL(CommonRequest query) throws ApiException;

    /**
     * 通过时间段获取索引片段
     */
    LinkedHashSet<String> querySegmentsByTime(List<String> sources, Date startTime, Date endTime);

    /**
     * 通过查询语句进行搜索 (Elastic Search版本)
     */
    String elasticSearch(ElasticParam query);

    /**
     * es 滚动查询
     * @param query
     * @return
     */
    Response scrollSearch(ElasticParam query);

    /**
     * 通过查询语句计算总数量  (Elastic Search版本)
     */
    CommonResponse elasticTotal(CommonResponse response) throws ApiException;

    /**
     * sql 查询 仅支持 select
     */
    List<Map<String, Object>> execQuery(String sql) throws ApiException;

    /**
     * 通过查询语句进行搜索 （MySql 版本）
     */
    VList sqlList(SqlQuery query) throws ApiException;

    /**
     * sql 查询 Count
     */
    int sqlCount(String tableName, WhereCondition condition);

    /**
     * SQL Group 查询
     */
    VData sqlGroup(SqlGroup param) throws ApiException;

    /**
     * 导出数据 SQL
     */
    void sqlExport(OutputStream outputStream, int sourceId, SqlQuery query) throws ApiException;

    /**
     * 导出数据 ES
     */
    void esExport(OutputStream outputStream, ElasticParam query) throws ApiException;

    /**
     * 导出数据 ES
     */
    Export.Progress esExport(ElasticParam query) throws ApiException;
}
