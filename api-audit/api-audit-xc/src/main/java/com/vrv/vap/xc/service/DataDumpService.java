package com.vrv.vap.xc.service;

import com.vrv.vap.xc.model.DeleteModel;
import com.vrv.vap.xc.pojo.DataCleanLog;
import com.vrv.vap.xc.pojo.DataDumpLog;
import com.vrv.vap.xc.pojo.DataDumpStrategy;
import com.vrv.vap.xc.vo.DataCleanLogQuery;
import com.vrv.vap.xc.vo.DataDumpLogQuery;
import com.vrv.vap.xc.vo.DataDumpStrategyQuery;
import com.vrv.vap.toolkit.vo.VList;

import java.util.List;

/**
 * 数据备份策略管理
 * Created by lizj on 2021/1/5
 */
public interface DataDumpService {
    VList<DataDumpStrategy> selectStrategyListByPage(DataDumpStrategyQuery record);

    void deleteStrategyById(DeleteModel record);

    void addStrategy(DataDumpStrategy record);

    void updateStrategyByKey(DataDumpStrategy record);

    void addDataDumpLog(DataDumpLog record);

    void addDataCleanLog(DataCleanLog record);

    VList<DataDumpLog> selectDumpListByPage(DataDumpLogQuery record);

    VList<DataCleanLog> selectCleanListByPage(DataCleanLogQuery record);

    List<String> selectExistDataList(DataDumpStrategy record);

    void updateDumpLogByKey(DataDumpLog record);
}
