package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.AssetTypeMapper;
import com.vrv.vap.xc.pojo.AssetType;
import com.vrv.vap.xc.service.IAssetTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetTypeServiceImpl extends ServiceImpl<AssetTypeMapper, AssetType> implements IAssetTypeService {

    public List<AssetType> findAssetTypeByCode(String assetType){
        QueryWrapper<AssetType> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("TreeCode",assetType + "-");
        return this.list(queryWrapper);
    }
}
