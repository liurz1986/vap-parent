package com.vrv.vap.netflow.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.netflow.model.Asset;
import com.vrv.vap.netflow.vo.AssetVo;

import java.util.List;

public interface AssetMapper extends BaseMapper<Asset> {
    List<AssetVo> findAllAsset();
}