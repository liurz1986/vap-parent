package com.vrv.vap.alarmdeal.business.asset.dao.query;

import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTotalStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTypeTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.SafeDeviceListVO;
import com.vrv.vap.jpa.web.NameValue;

import java.util.List;

public interface AssetQueryDao {
    /**
     * 资产总数及分类统计
     *
     * @return
     */
    AssetTotalStatisticsVO queryAssetTotalStatistics();
    /**
     * 资产数量按类型统计(二级资产类型)
     * @return
     */
    List<AssetStatisticsVO> queryAssetByAssetType();
    /**
     * 资产数量按部门
     * @return
     */
    List<AssetStatisticsVO> queryAssetByDepartment();
    /**
     * 资产数量按密级统计
     * @return
     */
    List<AssetStatisticsVO> queryAssetByLevel(String assetParentType);
    /**
     * 一级资产类型下资产数量按密级等级统计
     * @param type
     * @return
     */
    public List<AssetStatisticsVO> queryAssetTypeGroupByLevel(String type,String assetParentType);
    /**
     * 资产分类汇总统计：按资产类型统计 终端总数$，服务器总数$，网络设备总数$，安全设备总数$，其他设备数$。
     * @return
     */
    AssetTypeTotalVO queryAssetTypeTotal();
    /**
     * 一级资产类型下 ，按照二级资产类型、国产非国产进行分类统计
     *
     * @return Result
     */
    List<AssetStatisticsVO> queryAssetTypeTotalByTermType(String assetTypeGroupTreeCode);
    /**
     * 其他设备数量按类型统计
     * 其他设备  ：刻录机、打印机、涉密专用介质
     * @return
     */
    List<AssetStatisticsVO> queryOtherAssetNumber();
    /**
     * 安全设备信息列表
     *
     * @return
     */
    List<SafeDeviceListVO> querySafeDeviceAssetList();


    Integer countByWorth(Integer code);
    /**
     * 资产数量按安全域统计
     * @return
     */
    List<AssetStatisticsVO> queryAssetByDomain();
    /**
     * 其他设备类型统计（除终端、服务器、网络设备、安全设备一级资产类型外的设备）
     * @return
     */
    List<AssetStatisticsVO> queryAssetByOther();
    List<NameValue> queryAssetByArea();

    List<AssetStatisticsVO> queryAssetByDepartmentType(String type);

    List<AssetStatisticsVO> queryAssetNumByAssetType(String assetType);
}
