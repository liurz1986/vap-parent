package com.vrv.vap.amonitor.service.canvas.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.amonitor.entity.AssetCanvasInfo;
import com.vrv.vap.amonitor.mapper.AssetCanvasInfoMapper;
import com.vrv.vap.amonitor.service.canvas.CanvasInfoService;
import com.vrv.vap.amonitor.vo.AssetCanvasInfoQuery;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CanvasInfoServiceImpl implements CanvasInfoService {

    @Autowired
    private AssetCanvasInfoMapper mapper;

    @Override
    public int addItem(AssetCanvasInfo assetCanvasInfo) {
        return mapper.insert(assetCanvasInfo);
    }

    @Override
    public int updateItem(AssetCanvasInfo assetCanvasInfo) {
        return mapper.updateById(assetCanvasInfo);
    }

    @Override
    public int setDefaultItem(AssetCanvasInfo assetCanvasInfo) {
        Integer id = assetCanvasInfo.getId();
        assetCanvasInfo.setId(null);
        QueryWrapper<AssetCanvasInfo> queryWrapper = new QueryWrapper<>();
        AssetCanvasInfoQuery query = new AssetCanvasInfoQuery();

        assetCanvasInfo.setCanvasTop(0);
        mapper.update(assetCanvasInfo, queryWrapper);
        query.setId(id);
        assetCanvasInfo.setCanvasTop(1);
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return mapper.update(assetCanvasInfo, queryWrapper);
    }

    @Override
    public int deleteItem(AssetCanvasInfo assetCanvasInfo) {
        return mapper.deleteById(assetCanvasInfo.getId());
    }

    @Override
    public AssetCanvasInfo querySingle(AssetCanvasInfo assetCanvasInfo) {
        return mapper.selectById(assetCanvasInfo.getId());
    }

    @Override
    public VList<AssetCanvasInfo> queryByPage(Query record) {
        Page<AssetCanvasInfo> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<AssetCanvasInfo> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(mapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData<List<AssetCanvasInfo>> queryAll(Query record) {
        record.setMyCount(9999);
        Page<AssetCanvasInfo> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<AssetCanvasInfo> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vd(mapper.selectPage(page, queryWrapper).getRecords());
    }
}
