package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.xc.model.AssetCountModel;
import com.vrv.vap.xc.model.AssetModel;
import com.vrv.vap.xc.model.AssetTypeModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.pojo.Asset;
import com.vrv.vap.xc.vo.AssetVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetMapper extends BaseMapper<Asset> {

    List<AssetCountModel> countByAssetType(@Param("params") ReportParam model);

    List<AssetModel> getOrgNameByIpList(List<String> ipList);

    Page<AssetVO> findAssetStealLeakValue(Page<AssetVO> page, @Param("model") AssetTypeModel model);
}
