package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Asset;
import com.vrv.vap.admin.vo.AssetVo;
import com.vrv.vap.base.BaseMapper;

import java.util.List;

public interface AssetMapper extends BaseMapper<Asset> {
    List<AssetVo> findAllAsset();
}