package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.AssetExtend;
import com.vrv.vap.xc.pojo.AssetType;

import java.util.List;

public interface AssetTypeMapper extends BaseMapper<AssetType> {

    List<AssetType> findAssetTypeByGuidIn(List<String> guidList);
    List<AssetExtend> findAssetExtendByAssetGuidIn(List<String> guidList);
}
