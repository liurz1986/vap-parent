package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.xc.mapper.core.DataCleanLogMapper;
import com.vrv.vap.xc.mapper.core.DataDumpLogMapper;
import com.vrv.vap.xc.mapper.core.DataDumpStrategyMapper;
import com.vrv.vap.xc.model.DeleteModel;
import com.vrv.vap.xc.pojo.*;
import com.vrv.vap.xc.service.DataDumpService;
import com.vrv.vap.xc.vo.DataCleanLogQuery;
import com.vrv.vap.xc.vo.DataDumpLogQuery;
import com.vrv.vap.xc.vo.DataDumpStrategyQuery;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 数据备份策略管理
 * Created by lizj on 2021/1/5
 */
@Service
public class DataDumpServiceImpl implements DataDumpService {
    @Autowired
    private DataDumpStrategyMapper dataDumpStrategyMapper;

    @Autowired
    private DataDumpLogMapper dataDumpLogMapper;

    @Autowired
    private DataCleanLogMapper dataCleanLogMapper;


    @Override
    public VList<DataDumpStrategy> selectStrategyListByPage(DataDumpStrategyQuery record) {
        /*DataDumpStrategyExample example = new DataDumpStrategyExample();
        PageTools.setAll(example, record);
        DataDumpStrategyExample.Criteria criteria = example.createCriteria();
        ExampleTools.buildByCriteria(record, criteria).andEqual("id").andLike("dataId")
            .andLike("dataDesc").andLike("dataId").andEqual("dataType").andEqual("type")
            .andEqual("dumpMode").andEqual("state");
        List<DataDumpStrategy> list = dataDumpStrategyMapper.selectByExample(example);
        long total = dataDumpStrategyMapper.countByExample(example);*/
        Page<DataDumpStrategy> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<DataDumpStrategy> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(dataDumpStrategyMapper.selectPage(page, queryWrapper));
    }

    @Override
    public void deleteStrategyById(DeleteModel record) {
        dataDumpStrategyMapper.deleteById(record.getIntegerId());
    }

    @Override
    public void addStrategy(DataDumpStrategy record) {
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        dataDumpStrategyMapper.insert(record);
    }

    @Override
    public void updateStrategyByKey(DataDumpStrategy record) {
        dataDumpStrategyMapper.updateById(record);
    }

    @Override
    public void addDataDumpLog(DataDumpLog record) {
        dataDumpLogMapper.insert(record);
    }

    @Override
    public void addDataCleanLog(DataCleanLog record) {
        dataCleanLogMapper.insert(record);
    }

    @Override
    public VList<DataDumpLog> selectDumpListByPage(DataDumpLogQuery record) {
        /*DataDumpLogExample example = new DataDumpLogExample();
        PageTools.setAll(example, record);
        DataDumpLogExample.Criteria criteria = example.createCriteria();
        ExampleTools.buildByCriteria(record, criteria).andEqual("id").andLike("dataId").andLike("dataDesc")
                .andLike("dataId").andEqual("dataType").andEqual("strategyId").andEqual("dumpFileState")
                .andBetween("dumpTime", TimeTools.format(record.getMyStartTime(), "yyyy-MM-dd HH:mm:ss"), TimeTools.format(record.getMyEndTime(), "yyyy-MM-dd HH:mm:ss"));
        List<DataDumpLog> list = dataDumpLogMapper.selectByExample(example);
        long total = dataDumpLogMapper.countByExample(example);*/
        Page<DataDumpLog> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<DataDumpLog> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        if (record.getMyStartTime() != null && record.getMyEndTime() != null) {
            queryWrapper.between("dump_time", record.getMyStartTime(), record.getMyEndTime());
        }
        return VoBuilder.vl(dataDumpLogMapper.selectPage(page, queryWrapper));
    }

    @Override
    public VList<DataCleanLog> selectCleanListByPage(DataCleanLogQuery record) {
        /*DataCleanLogExample example = new DataCleanLogExample();
        PageTools.setAll(example, record);
        DataCleanLogExample.Criteria criteria = example.createCriteria();
        ExampleTools.buildByCriteria(record, criteria).andEqual("id").andLike("dataId")
            .andLike("dataDesc").andEqual("dataType")
                .andBetween("cleanTime", TimeTools.format(record.getMyStartTime(), "yyyy-MM-dd HH:mm:ss"), TimeTools.format(record.getMyEndTime(), "yyyy-MM-dd HH:mm:ss"));
        List<DataCleanLog> list = dataCleanLogMapper.selectByExample(example);
        long total = dataCleanLogMapper.countByExample(example);*/
        Page<DataCleanLog> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<DataCleanLog> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        if (record.getMyStartTime() != null && record.getMyEndTime() != null) {
            queryWrapper.between("clean_time", record.getMyStartTime(), record.getMyEndTime());
        }
        return VoBuilder.vl(dataCleanLogMapper.selectPage(page, queryWrapper));
    }

    @Override
    public List<String> selectExistDataList(DataDumpStrategy record) {
        List<String> result = new ArrayList<>();
        List<DataDumpStrategy> list = dataDumpStrategyMapper.selectExistDataList(record);
        for (DataDumpStrategy strategy : list) {
            String dataStr = strategy.getDataId();
            String[] datas = dataStr.split(",");
            result.addAll(Arrays.asList(datas));
        }
        return result;
    }

    @Override
    public void updateDumpLogByKey(DataDumpLog record) {
        dataDumpLogMapper.updateById(record);
    }
}
