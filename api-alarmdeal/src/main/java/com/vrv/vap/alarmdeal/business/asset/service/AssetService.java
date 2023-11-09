package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetCacheVo;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetIpByAssetGroupVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetSearchNewVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetTypeByIpVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetWorthVO;
import com.vrv.vap.alarmdeal.business.asset.vo.*;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AssetService extends BaseService<Asset, String> {

	public List<AssetCacheVo> queryAllAsset();

	public List<AssetCacheVo> queryAssetCacheVoByIp(String ip);

	public Asset queryAssetByIp(String ip);
    
	/**
	 * 获得资产分页管理列表
	 * @param assetVO
	 * @param pageable
	 * @return
	 */
	public PageRes<AssetVO> getAssetInfoPager(AssetSearchVO assetSearchVO, Pageable pageable);
	
	public  PageRes<AssetVO> getAssetInfoPager(String treeCode,  Pageable pageable);
	/**
	 * 获得资产标签
	 * @return
	 */
	public List<String> getAllTopTagsType();
	public Map<String,Integer> getAllTopTagsCount();
	/**
	 * 同类型资产IP验证
	 * @param assetSearchVO
	 * @return
	 */
	public Result<Boolean> validateAssetIp(AssetSearchVO assetSearchVO);
	public Result<Boolean> validateAssetIp(String ip, String guid, String assetTypeGuid, String assetTypeSnoGuid);
	/**
	 * 保存资产
	 * @param assetVO
	 * @return
	 */
	public Result<String> saveAddAsset(AssetVO assetVO);
	
	/**
	 * 更新资产
	 * @param assetVO
	 * @return
	 */
	public Result<String> saveEditAsset(AssetVO assetVO);
	

	public Result<Boolean> deleteAsset(String guid);

	/**
	 * 批量资产删除
	 * @param guids
	 * @return
	 */
	public Result<Boolean> batchDeleteAsset(List<String> guids);
	/**
	 * asset装成AssetVO
	 * @param asset
	 * @return
	 */
	public AssetVO mapperVO(Asset asset,List<BaseSecurityDomain> allDomains);

	/**
	 * 查询资产条件
	 * @param assetSearchVO
	 * @return
	 */
	public List<QueryCondition> searchAssetCondition(AssetSearchVO assetSearchVO);

	/**
	 *  按照资产品牌型号进行分类处理（自带权限）
	 * @param assetSearchVO
	 * @return
	 */
	public Map<String, List<AssetExportDataVO>> getAssetBySno(AssetSearchVO assetSearchVO);

	public Map<String, List<AssetExportDataVO>> getAssetByType(AssetSearchVO assetSearchVO);

	public Map<String, List<AssetExportDataVO>> getAssetByTypeGroup(AssetSearchVO assetSearchVO, Map<String,List<AssetType>> assetTypesMap, Map<String,String> uniqueCodeAndTreeCodeMap );
	/**
	 * 获得单个资产的信息
	 * @param guid
	 * @return
	 */
	public Result<AssetVO> getSingleAsset(String guid);

	/**
	 * 查询某个类型的设备数量(不带权限)
	 * @param typeGuid
	 * @return
	 */
	public int getAssetCountNotWithAuth(String typeGuid);

	/**
	 * 按照任意列分组统计数值
	 * @param columnName
	 * @return
	 */
	public List<Map<String, Object>> getAssetCountByAnyColumn(String columnName);
	
	/**
	 * 获取资产数据统计 按照资产类型（大类分组）
	 * @return
	 */
	public List<Map<String, Object>> getAssetCountByAssetType();
	
	public List<Map<String, Object>> getAssetCreateCountByTime(String timeType);


	public AssetOrgTreeVO organizationByCode();

	// 校验序列号是否存在
	public boolean validateAssetSerialNumber(String serialNumber,String guid);

	// 统计不同资产类型下的资产数量
	public List<Map<String, Object>> queryAssetTypeNumber();

	// 统计不同部门下的资产数量"
	public List<Map<String, Object>> queryDepartmentNumber();

	// 基础数据查询终端接口信息
	public PageRes<Map<String,Object>> queryAssetHostsPager(AssetSearchVO assetSearchVOe);

	// usb设备存储介质（支持分页）
	public PageRes<Map<String, Object>> queryUSBMemorysPager(AssetSearchVO assetSearchVO);
    // 查询USB外设（支持分页）
	public PageRes<Map<String, Object>> queryUSBPeripheralsPager(AssetSearchVO assetSearchVO);
    // 终端上安装安全保密产品数量
	public Long queryAssetHostSafeNums(String responsibleCode);
    // 获取安全保密产品安装情况
	public List<Map<String, Object>>  querySafeProductInfo(String responsibleCode);
     // 终端类型安装与未安装统计
	public List<Map<String, Object>> terminalAssetInstallCount();
    // 当前设备是不是终端类型
    boolean isTerminalAsset(String typeUniqueCode);
    // mac地址校验
	Result<Boolean> validateAssetMac(String mac, String guid, String assetTypeGuid, String assetTypeSnoGuid);
    // 工作台：统计终端、服务器、网络设备、安全产品资产的总数量
	public long queryWorkplatformnNum(String type);
	// 通过ip获取资产详情   2022-1-24 方法改造
	public AssetDetailVO getOneAssetDetailByIp(String ip);
    // 通过资产guid获取资产详情  2022-1-24 方法改造
	public AssetDetailVO getAssetDetail(String guid);
    // 获取资产数量（自带权限）
	public long getAssetCount();

	/**
	 * 根据NTDS数据更新资产表
	 * @param ntds
	 * @return
	 */
	public Result<String> updateAssetByNTDS(List<AuditAssetVO> ntds);


	//-----------------关保相关-----------------------//
	/**
	 * 通过ip获取资产的资产类型
	 * @param ips
	 * @return
	 */
	public Result<List<AssetTypeByIpVO>> getAssetTypeByIps(List<String> ips);

	/**
	 * 查询全部资产
	 * @return
	 */
	public Result<List<AssetWorthVO>> getAllAsset();

	/**
	 * 资产权重
	 * @return
	 */
	public Long getAssetWeight();

	/**
	 * 通过ip资产权重
	 * @return
	 */
	public Long getAssetWeightByIp(List<String> ips);

	/**
	 * 服务器、终端、网络设备、安全保密产品对应的资产ip
	 * @return
	 */
	public List<AssetIpByAssetGroupVO> getIpByGroupType();

	/**
	 * 通过ip获取资产中对应责任人
	 * @param ip
	 * @return
	 */
	public Result<String> getEmpNameByIp(String ip);

	public Result<AssetType> getAssetTypeByIp(String ip);

	public List<String> getAllAssetIps();

	public List<String> getAssetIpByOrg(String orgCode);

	public List<Asset> queryAssets(AssetSearchNewVO assetSearchNewVO);

	public long queryAssetTotalFilter(Date startTime, Date endTime);

	List<String> getAssetIpsByTypeGroup(String type);

    void culStealLeakValue(List<Map<String, Map<String, Long>>> list,Date date);

	PageRes<AssetVO> getAssetImagePager(AssetSearchVO assetSearchVO, Pageable pageable);

	Result<String> exportNewAssetInfo(AssetSearchVO assetSearchVO);

	void culAppStealLeakValue(Date date);

	Result<String> addAssetOfConcern(AssetSearchVO assetSearchVO);

	Result<String> delAssetOfConcern(AssetSearchVO assetSearchVO);

	Result<List<Map<String, Object>>> getAssetTypeAndIcon(List<String> ips);

	public Result<List<Map<String, Object>>> getAssetMsg(String code);

	Result<List<Map<String, Object>>> getUsb(String code);

	void culAppMaintenStealLeakValue(Date date);

	public List<Map<String, Object>> getUnInstallList();
}
