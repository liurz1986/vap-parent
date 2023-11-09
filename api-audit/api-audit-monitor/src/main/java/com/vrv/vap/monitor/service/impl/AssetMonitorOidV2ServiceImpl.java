package com.vrv.vap.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.monitor.command.MonitorRunnerV2;
import com.vrv.vap.monitor.entity.Monitor2AssetOidAlg;
import com.vrv.vap.monitor.entity.Monitor2Indicator;
import com.vrv.vap.monitor.mapper.Monitor2AssetOidAlgMapper;
import com.vrv.vap.monitor.service.AssetMonitorOidV2Service;
import com.vrv.vap.monitor.service.MonitorV2IndicatorService;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetMonitorOidV2ServiceImpl implements AssetMonitorOidV2Service {

    @Autowired
    private Monitor2AssetOidAlgMapper mapper;

    @Lazy
    @Autowired
    private MonitorRunnerV2 monitorRunnerV2;

    @Autowired
    private MonitorV2IndicatorService monitorV2IndicatorService;

    @Override
    public int addItem(Monitor2AssetOidAlg assetOidAlg) {
        Optional<Monitor2Indicator> indicatorOptional = monitorV2IndicatorService.getMonitor2Indicators().stream().filter(f -> f.getIndicatorField().equals(assetOidAlg.getIndicatorField())).findAny();
        assetOidAlg.setRealQuery(indicatorOptional.get().getRealQuery());
        int res = mapper.insert(assetOidAlg);
        markChanged(assetOidAlg, res);
        return res;
    }

    private void markChanged(Monitor2AssetOidAlg assetOidAlg, int res) {
        if (res == 1) {
            monitorRunnerV2.settingChanged(assetOidAlg.getSnoUnicode());
        }
    }

    @Override
    public int updateItem(Monitor2AssetOidAlg assetOidAlg) {
        int res = mapper.updateById(assetOidAlg);
        markChanged(assetOidAlg, res);
        return res;
    }

    @Override
    public int deleteItem(Monitor2AssetOidAlg assetOidAlg) {
        int res = mapper.deleteById(assetOidAlg.getId());
        markChanged(assetOidAlg, res);
        return res;
    }

    @Override
    public Monitor2AssetOidAlg querySingle(Monitor2AssetOidAlg assetOidAlg) {
        return mapper.selectById(assetOidAlg.getId());
    }

    @Override
    public VList<Monitor2AssetOidAlg> queryByPage(Query record) {

        if (record.getOrder() == null) {
            record.setOrder("indicator_field,id");
            record.setBy("asc");
        }

        Page<Monitor2AssetOidAlg> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<Monitor2AssetOidAlg> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(mapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData<List<Monitor2AssetOidAlg>> queryAll(Query query) {
        QueryWrapper<Monitor2AssetOidAlg> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vd(mapper.selectList(queryWrapper));
    }
}
