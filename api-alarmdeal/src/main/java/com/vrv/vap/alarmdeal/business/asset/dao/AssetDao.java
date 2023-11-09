package com.vrv.vap.alarmdeal.business.asset.dao;

import com.vrv.vap.alarmdeal.business.appsys.vo.AppServerVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.TerminalAssteInstallTime;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetExportDataVO;
import com.vrv.vap.jpa.web.page.QueryCondition;

import java.util.List;
import java.util.Map;

public interface AssetDao {
     
	/**
	 * 获得标签数据
	 * @return
	 */
	public List<String[]> getAllTopTagsType();
	
    /**
     * 根据资产类型继续宁
     * @return
     */
	public List<Map<String,Object>> getStaticsByAssetType();
 
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
	
	public List<Map<String, Object>> getAssetIpAndIds();
	
	public List<Map<String, Object>> getAssetIpAndIds(String oldIp,String newIp);
	
	public List<AssetExportDataVO> getAllData(List<QueryCondition> conditions);

	// 统计不同资产类型下的资产数量（一级资产类型）
	public List<Map<String, Object>> queryAssetTypeNumber();

	// 统计不同部门下的资产数量
	public  List<Map<String, Object>> queryDepartmentNumber();

	// 基础数据查询终端接口信息
	public List<Map<String, Object>> queryAssetHostsPage(String userCode, int stratpage, int endpage);

	// 基础数据查询终端总数量
	public Long queryAssetHostsTotal(String userCode);

	// 查询usb设备存储介质（支持分页）
	public List<Map<String, Object>> queryUSBMemorysPage(String responsibleCode, int stratpage, Integer endpage);

	// 查询usb设备存储介质总记录数
	public Long queryUSBMemorysTotale(String userCode);

	// USB外设总记录数
	public Long queryUSBPeripheralsTotal(String responsibleCode);

	// USB外设（支持分页）
	public List<Map<String, Object>> queryUSBPeripheralsPage(String responsibleCode, int i, Integer count);

	// 终端上安装安全保密产品数量
	public Long queryAssetHostSafeNums(String responsibleCode);

	// 获取安全保密产品安装情况
	public List<Map<String, Object>> querySafeProductInfo(String responsibleCode);

	public List<Map<String, Object>>  terminalAssetInstallCount();

	public int terminalAssetByTypeUniqueCode(String typeUniqueCode);

	public List<Map<String, Object>> allAssetDataValidata();

	public List<Map<String,Object>> getAllAssetTypes();

	public long queryWorkplatformnNum(String groupTreeCode);


	// 获取品牌信号配置的事件监控、性能监控配置信息
	public int queryAuhtPointByAssetTypeSno(List<String> name);
	// 终端下所有二级资产类型的uniqueCode
	List<String> getTypeUnicodesIsAssetHost();

	// 根据asset_terminal_install_time表中current_install_time的值与assset表os_setup_time比较，没有变化将last_install_time的值更新为当前的系统安装时间，有变化不处理
	public void updateLastInstallTime();

    // 更新asset_terminal_install_time中current_install_time的值为asset中系统安装时间
	public void updateCurrentInstallTime();

	/**
	 * 获取待新增的操作系统安装时间数据
	 * @return
	 */
	public List<TerminalAssteInstallTime> getTerminalAssteInstallTime();

	/**
	 * 更新LastInstallTime通过资产系统安装时间，主要为了资产修改后的情况
	 */
	public void updateLastInstallTimeByAsset();

	//应用系统信息中导出服务器信息   2022-05-05
    public List<AppServerVo> getAssetServer(String[] assetGuis);

	// 数据同步：获取所有资产信息 2022-06-21
	public List<AssetQueryVO> getAllAssetSync();


	/**
	 * cas大屏：根据一级资产类型获取对应资产总数
	 * @param s
	 * @return
	 */
	public int getAssetTotalByGroupType(String s);

	public List<Map<String, Object>> getAssetTypeAndIcon(List<String> ips);

	public List<Map<String, Object>> getAssetMsg(String code);

	public List<Map<String, Object>> getUsb(String code);

	public List<Map<String, Object>> getUnInstallList();
}
