package com.vrv.vap.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.monitor.entity.Monitor2AssetIndicatorViewHistory;
import com.vrv.vap.monitor.mapper.Monitor2AssetIndicatorViewHistoryMapper;
import com.vrv.vap.monitor.service.MonitorV2AssetIndicatorViewHistoryService;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorV2AssetIndicatorViewHistoryServiceImpl implements MonitorV2AssetIndicatorViewHistoryService {

    @Autowired
    private Monitor2AssetIndicatorViewHistoryMapper mapper;

    @Override
    public int addItem(Monitor2AssetIndicatorViewHistory monitor2IndicatorView) {
        return mapper.insert(monitor2IndicatorView);
    }

    @Override
    public int updateItem(Monitor2AssetIndicatorViewHistory monitor2IndicatorView) {
        return mapper.updateById(monitor2IndicatorView);
    }

    @Override
    public int deleteItem(Monitor2AssetIndicatorViewHistory monitor2IndicatorView) {
        return mapper.deleteById(monitor2IndicatorView.getId());
    }

    @Override
    public Monitor2AssetIndicatorViewHistory querySingle(Monitor2AssetIndicatorViewHistory monitor2IndicatorView) {
        return mapper.selectById(monitor2IndicatorView.getId());
    }

    @Override
    public VList<Monitor2AssetIndicatorViewHistory> queryByPage(Query record) {
        Page<Monitor2AssetIndicatorViewHistory> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<Monitor2AssetIndicatorViewHistory> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(mapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData<List<Monitor2AssetIndicatorViewHistory>> queryAll(Query query) {
        QueryWrapper<Monitor2AssetIndicatorViewHistory> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vd(mapper.selectList(queryWrapper));
    }
}
