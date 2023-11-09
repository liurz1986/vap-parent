package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetSystemAttributeSettingsRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSystemAttributeSettingsService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSystemAttributeSettingsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetSystemAttributeSettingsServiceImpl  extends BaseServiceImpl<AssetSystemAttributeSettings, String> implements AssetSystemAttributeSettingsService {


	@Autowired
	AssetSystemAttributeSettingsRepository assetSystemAttributeSettingsRepository;

	@Autowired
	private MapperUtil mapper;

	@Override
	public BaseRepository<AssetSystemAttributeSettings, String> getRepository() {
		// TODO Auto-generated method stub
		return assetSystemAttributeSettingsRepository;
	}


	private static List<CustomSettings> systemAttributeCustomSettings=new ArrayList<>();

	@Override
	public List<CustomSettings> getSystemAttributeCustomSettings() {

		if (systemAttributeCustomSettings != null && !systemAttributeCustomSettings.isEmpty()) {
			return systemAttributeCustomSettings;
		}
		synchronized (systemAttributeCustomSettings) {
			List<QueryCondition> cons = new ArrayList<>();
			systemAttributeCustomSettings = new ArrayList<>();
			cons.add(QueryCondition.eq("visible", true));
			List<AssetSystemAttributeSettings> allAttributes = this.findAll(cons);
			// 添加系统控件
			for (AssetSystemAttributeSettings attribute : allAttributes) {

				AssetSystemAttributeSettingsVO setting = new AssetSystemAttributeSettingsVO(attribute);
				CustomSettings customSettings = setting.getCustomSettings();
				if (customSettings != null) {
					if (customSettings.getChildrenControl() == null || customSettings.getChildrenControl().isEmpty()) {
						customSettings.setAttributeType("system");
						systemAttributeCustomSettings.add(customSettings);

					} else {
						for (CustomSettings child : customSettings.getChildrenControl()) {
							if (Boolean.TRUE.equals(child.getVisible())) {
								child.setAttributeType("system");
								systemAttributeCustomSettings.add(child);
							}
						}
					}
				}
			}
			return systemAttributeCustomSettings;
		}

	}

	@Override
	public List<CustomSettings> getSystemAttributeCustomSettings(String treeCode) {
		List<CustomSettings> systemAttributeCustomSettings=new ArrayList<>();
		List<AssetSystemAttributeSettings> allAttributes = queryAssetSystemAttributeSettings(treeCode);
		// 添加系统控件
		for (AssetSystemAttributeSettings attribute : allAttributes) {
			AssetSystemAttributeSettingsVO setting = new AssetSystemAttributeSettingsVO(attribute);
			CustomSettings customSettings = setting.getCustomSettings();
			// 列展示，须是显示的
			if (attribute.getVisible() && customSettings != null) {
				if (customSettings.getChildrenControl() == null || customSettings.getChildrenControl().isEmpty()) {
					customSettings.setAttributeType("system");
					systemAttributeCustomSettings.add(customSettings);

				} else {
					for (CustomSettings child : customSettings.getChildrenControl()) {
						if (Boolean.TRUE.equals(child.getVisible())) {
							child.setAttributeType("system");
							systemAttributeCustomSettings.add(child);
						}
					}
				}
			}
		}
		return systemAttributeCustomSettings;
	}

	@Override
	public void  cleanCache()
	{
		synchronized (systemAttributeCustomSettings) {
			systemAttributeCustomSettings.clear();
		}
	}


	/**
	 * 获取字段录入偏好设置，获取的值包括不显示的
	 * 查询逻辑：当前资产类型没有配置的话，查询父类节点的配置，以此类推
	 * @param treeCode treeCode
	 * @return list
	 */
	@Override
	public List<AssetSystemAttributeSettings> queryAssetSystemAttributeSettings(String treeCode) {
		String assetSettingsGuid = "AssetPanels";
		if (StringUtils.isNotEmpty(treeCode)) {
			assetSettingsGuid = assetSettingsGuid + "_" + treeCode;
		}
		List<QueryCondition> querys = new ArrayList<>();
		querys.add(QueryCondition.eq("assetSettingsGuid", assetSettingsGuid));
		querys.add(QueryCondition.eq("visible", true));
		// 获取资产类型对应的配置信息
		List<AssetSystemAttributeSettings> settings = this.findAll(querys);
		// 查到数据返回或treeCode为空查顶层节点数据为空时返回
		if ((null != settings && settings.size() > 0) || StringUtils.isEmpty(treeCode)) {
			return settings;
		}
		settings = getCycleAssetSystemAttributeSettings(assetSettingsGuid, treeCode);
		return settings;
	}

	@Override
	public void saveAssetSystemAttributeSettings(String assetSettingsGuid, List<AssetSystemAttributeSettings> systemAttributeSettings) {
		if(null == systemAttributeSettings || systemAttributeSettings.size() <=0 ){
			return;
		}
		for(AssetSystemAttributeSettings settings : systemAttributeSettings){
			settings.setAssetSettingsGuid(assetSettingsGuid);
		}
		this.save(systemAttributeSettings);
		this.cleanCache();
	}

	// 针对当前节点没有数据的处理：当前节点没有数据，查找父节点，直到顶层节点
	private List<AssetSystemAttributeSettings> getCycleAssetSystemAttributeSettings(String assetSettingsGuid, String treeCode) {
		if (StringUtils.isNotEmpty(treeCode)) {
			assetSettingsGuid = assetSettingsGuid + "_" + treeCode;
		}
		List<QueryCondition> querys = new ArrayList<>();
		querys.add(QueryCondition.eq("assetSettingsGuid", assetSettingsGuid));
		// 获取资产类型对应的配置信息
		List<AssetSystemAttributeSettings> settings = this.findAll(querys);
		// 查到数据返回或treeCode为空查顶层节点数据为空时返回
		if ((null != settings && settings.size() > 0) || StringUtils.isEmpty(treeCode)) {
			return setAssetSystemAttributeSettingsGuid(settings);
		}
		int index = treeCode.lastIndexOf('-');
		// index为-1时，代表就是最顶层的资产类型了,不用再往上找了，没有的话直接获取默认配置
		if(-1 == index){
			return getCycleAssetSystemAttributeSettings("AssetPanels", null);
		}
		String parentTreeCode = treeCode.substring(0, index);
		return getCycleAssetSystemAttributeSettings("AssetPanels", parentTreeCode);
	}

	private List<AssetSystemAttributeSettings>  setAssetSystemAttributeSettingsGuid(List<AssetSystemAttributeSettings> settings) {
		List<AssetSystemAttributeSettings> newDatas = new ArrayList<AssetSystemAttributeSettings>();
		for (AssetSystemAttributeSettings setting : settings) {
			AssetSystemAttributeSettings settingNew =new AssetSystemAttributeSettings();
			mapper.copy(setting,settingNew);
			settingNew.setGuid(UUIDUtils.get32UUID());
			newDatas.add(settingNew);
		}
		return newDatas;
	}


}
