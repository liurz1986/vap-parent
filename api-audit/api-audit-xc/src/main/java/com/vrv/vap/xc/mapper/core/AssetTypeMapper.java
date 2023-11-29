package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.AssetExtend;
import com.vrv.vap.xc.pojo.AssetType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetTypeMapper extends BaseMapper<AssetType> {

    List<AssetType> findAssetTypeByGuidIn(@Param("guidList") List<String> guidList);

    List<AssetExtend> findAssetExtendByAssetGuidIn(@Param("guidList") List<String> guidList);
}
