package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.AssetType;

import java.util.List;

public interface IAssetTypeService extends IService<AssetType> {

    List<AssetType> findAssetTypeByCode(String assetType);
}
