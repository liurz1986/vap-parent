package com.vrv.vap.amonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.amonitor.entity.Monitor2AssetType;
import com.vrv.vap.amonitor.mapper.Monitor2AssetTypeMapper;
import com.vrv.vap.amonitor.service.Monitor2AssetTypeService;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Monitor2AssetTypeServiceImpl implements Monitor2AssetTypeService {
    @Autowired
    private Monitor2AssetTypeMapper mapper;

    @Override
    public int addItem(Monitor2AssetType monitor2AssetType) {
        int res = mapper.insert(monitor2AssetType);
        return res;
    }

    @Override
    public int updateItem(Monitor2AssetType monitor2AssetType) {
        int res = mapper.updateById(monitor2AssetType);
        return res;
    }

    @Override
    public int deleteItem(Monitor2AssetType monitor2AssetType) {
        int res = mapper.deleteById(monitor2AssetType.getGuid());
        return res;
    }

    @Override
    public Monitor2AssetType querySingle(Monitor2AssetType monitor2AssetType) {
        return mapper.selectById(monitor2AssetType.getGuid());
    }

    @Override
    public VList<Monitor2AssetType> queryByPage(Query t) {
        return null;
    }

    @Override
    public VData<List<Monitor2AssetType>> queryAll(Query t) {
        QueryWrapper<Monitor2AssetType> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, t);
        return VoBuilder.vd(mapper.selectList(queryWrapper));
    }

}
