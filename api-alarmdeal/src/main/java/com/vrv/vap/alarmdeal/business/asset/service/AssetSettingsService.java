package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.model.AssetSettings;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetAttributeTreeNode;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetPanel;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.alarmdeal.business.asset.vo.PageColumnVO;
import com.vrv.vap.alarmdeal.business.asset.vo.PageCustomColumnVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.NameValue;

import java.util.List;

public interface AssetSettingsService  extends BaseService<AssetSettings, String>{
	public List<AssetPanel> getAssetPanels(AssetSettings assetSetting);

	public String  saveAssetPanels(List<AssetPanel> panles,String treeCode);
	
	public Boolean saveSettingScope(String type);

	public NameValue getSettingScope();
	
	public Boolean saveAssetPageColumns(List<PageColumnVO> columns, String treeCode);

	public List<PageColumnVO> getAssetPageColumnsAll(String treeCode);

	public List<PageColumnVO> getAssetPageColumns(String treeCode);
	// 获取导出模板展示的列
	public  List<CustomSettings> getExcelColumns(String treeCode, String type, String guid);

	public  List<PageColumnVO> getSystemPageColumns(String treeCode);

	public  List<PageCustomColumnVO> getCustomPageColumns(String treeCode);
	
	public List<AssetTypeTreeVO> getAssetTypeComboboxTree();

	public AssetSettings getAssetSettingByTreeCode(String treeCode,String guidType);

	/**
	 * 获取资产类型录入偏好配置: 2021-08-19
	 * 1. 首先根据配置的力度：如果配置的一级，展示一级的配置，如果一级没有展示顶层配置（顶层有初始化数据，不存在为空的情况）
	 * 2. 当前treeCode所在的资产类型不是当前配置的力度：如果当前资产类型是当前力度的下级，向上找到对应的力度级别的配置，
	 *    如果当前资产类型是当前力度的上级或同级，就用当期资产类型的配置
	 * 3. 查询当前资产类型配置时没有配置信息，向上，直到顶层配置
	 * @param treeCode
	 * @return
	 */
	public List<AssetAttributeTreeNode> querySystemAttributeTree(String treeCode);

}
