package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.QueryModel;
import com.vrv.vap.xc.model.SingleTableModel;

import java.util.Map;
import java.util.Optional;

public interface SingleTableService {

    /**
     * 查询指定表,返回1000条数据
     *
     * @param singleTableModel 通过SingleTableBuilder获取
     * @return
     */
    Optional<VList<Map<String, Object>>> queryAll(SingleTableModel singleTableModel);

    /**
     * 根据查询条件查询指定表
     *
     * @param singleTableModel 通过SingleTableBuilder获取
     * @param queryModel       查询条件
     * @return
     */
    Optional<VList<Map<String, Object>>> query(SingleTableModel singleTableModel, QueryModel queryModel);

    /**
     * 根据查询条件查询指定表
     *
     * @param table      表名
     * @param select     字段名
     * @param requestMap 查询参数
     * @return
     */
    Optional<VList<Map<String, Object>>> query(String table, String select, Map<String, Object> requestMap);

    /**
     * 删除数据
     *
     * @param singleTableModel 通过SingleTableBuilder获取
     * @param pk               主键值
     * @return
     */
    RetMsgEnum delete(SingleTableModel singleTableModel, String[] pk);

    /**
     * 添加数据,返回新增的数据主键
     *
     * @param singleTableModel 通过SingleTableBuilder获取
     * @param kv               数据键值对
     * @return
     */
    Optional<String> add(SingleTableModel singleTableModel, Map<String, Object> kv);

    /**
     * 修改数据
     *
     * @param singleTableModel 通过SingleTableBuilder获取
     * @param kv               数据键值对
     * @return
     */
    RetMsgEnum update(SingleTableModel singleTableModel, Map<String, Object> kv);

}
