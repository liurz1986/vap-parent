package com.vrv.vap.alarmdeal.business.asset.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSettings;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSettingsService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSystemAttributeSettingsService;
import com.vrv.vap.alarmdeal.business.asset.vo.*;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/assetSetting")
public class AssetSettingController extends BaseController {
	
	
	@Autowired

	AssetSystemAttributeSettingsService assetSystemAttributeSettingsService;
	
	@Autowired
	AssetSettingsService assetSettingsService;
	
	private static Logger logger = LoggerFactory.getLogger(AssetSettingController.class);
	
	
	Gson  gson=new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
			.create();

    @GetMapping(value = "/getSystemAttributeTree")
    @ApiOperation(value = "偏好数据(系统属性)查询接口", notes = "")
    @SysRequestLog(description = "偏好数据(系统属性)查询接口", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetAttributeTreeNode>> getSystemAttributeTree() {
        List<AssetAttributeTreeNode> result = assetSettingsService.querySystemAttributeTree(null);
        return ResultUtil.success(result);
    }

    @GetMapping(value = "/getSystemAttributeTree/{treeCode}")
    @ApiOperation(value = "获取偏好数据(系统属性)查询接口", notes = "")
    @SysRequestLog(description = "根据treeCode获取偏好数据(系统属性)查询接口", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetAttributeTreeNode>> getSystemAttributeTreeByTreeCode(@PathVariable("treeCode") String treeCode) {
        logger.info("根据treeCode获取偏好数据(系统属性)查询接口");
        List<AssetAttributeTreeNode> result =  assetSettingsService.querySystemAttributeTree(treeCode);
        return ResultUtil.success(result);
    }

	@PostMapping(value="/saveSystemAttributeTree")
	@ApiOperation(value="保存偏好数据(系统属性)接口",notes="")
	@SysRequestLog(description="保存偏好数据(系统属性)接口", actionType = ActionType.UPDATE,manually=false)
	public  Result<Boolean> saveSystemAttributeTree(@RequestBody List<AssetAttributeTreeNode>  nodes){
		if(nodes==null)
		{
			return ResultUtil.success(false);
		}
		saveSystemAttribute(nodes,null);
		return ResultUtil.success(true);
	}

	/**
	 * 按照资产类型保存偏好数据(系统属性)接口
	 * 2021- 08 -10
	 * @param nodes nodes
	 * @param treeCode treeCode
	 * @return Result
	 */
	@PostMapping(value="/saveSystemAttributeTree/{treeCode}")
	@ApiOperation(value="按照资产类型保存偏好数据(系统属性)接口",notes="")
	@SysRequestLog(description="按照资产类型保存偏好数据(系统属性)接口", actionType = ActionType.UPDATE,manually=false)
	public  Result<Boolean> saveSystemAttributeTreeByTreeCode(@RequestBody List<AssetAttributeTreeNode>  nodes, @PathVariable("treeCode") String treeCode){
		logger.info("按照资产类型保存偏好数据(系统属性)接口 satrt");
		Result<Boolean> result = new Result<Boolean>();
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(false);
		result.setMsg("偏好数据(系统属性)数据为空！");
		if(null == nodes)
		{
			return result;
		}
		if(StringUtils.isEmpty(treeCode)){
			result.setMsg("treeCode 不能为空！");
			return result;
		}
		saveSystemAttribute(nodes,treeCode);
		return ResultUtil.success(true);
	}

	private void saveSystemAttribute(List<AssetAttributeTreeNode> nodes, String treeCode) {
		List<AssetPanel> assetPanels = getAssetPanels(nodes, treeCode);
		List<AssetSystemAttributeSettings> systemAttributeSettings = getSystemAttributeSettings(nodes, treeCode);
		String assetSettingsGuid = assetSettingsService.saveAssetPanels(assetPanels, treeCode);
		assetSystemAttributeSettingsService.saveAssetSystemAttributeSettings(assetSettingsGuid, systemAttributeSettings);
	}

	@GetMapping(value="/getSystemPageColumns")
	@ApiOperation(value="获取系统属性列",notes="")
	@SysRequestLog(description="获取系统属性列", actionType = ActionType.SELECT,manually=false)
	public  Result<List<PageColumnVO>> getSystemPageColumns(){
		
		List<PageColumnVO> result = assetSettingsService.getSystemPageColumns(null);
		return ResultUtil.success(result);
	}
	@GetMapping(value="/getSystemPageColumns/{treeCode}")
	@ApiOperation(value="获取系统属性列",notes="")
	@SysRequestLog(description="获取系统属性列", actionType = ActionType.SELECT,manually=false)
	public  Result<List<PageColumnVO>> getSystemPageColumns(@PathVariable("treeCode") String treeCode){

		List<PageColumnVO> result = assetSettingsService.getSystemPageColumns(treeCode);
		return ResultUtil.success(result);
	}

	@GetMapping(value="/getCustomPageColumns")
	@ApiOperation(value="获取自定义属性列",notes="")
	@SysRequestLog(description="获取自定义属性列", actionType = ActionType.SELECT,manually=false)
	public  Result<List<PageCustomColumnVO>> getCustomPageColumns(){
		List<PageCustomColumnVO> customPageColumns = assetSettingsService.getCustomPageColumns(null);
		return ResultUtil.success(customPageColumns);
	}

	@GetMapping(value="/getCustomPageColumns/{treeCode}")
	@ApiOperation(value="获取自定义属性列",notes="")
	@SysRequestLog(description="获取自定义属性列", actionType = ActionType.SELECT,manually=false)
	public  Result<List<PageCustomColumnVO>> getCustomPageColumnsByTreeCode(@PathVariable("treeCode") String treeCode){
		List<PageCustomColumnVO> customPageColumns = assetSettingsService.getCustomPageColumns(treeCode);
		return ResultUtil.success(customPageColumns);
	}

	@PostMapping(value="/saveAssetPageColumns")
	@ApiOperation(value="保存列数据",notes="")
	@SysRequestLog(description="保存列数据", actionType = ActionType.UPDATE,manually=false)
	public  Result<Boolean> saveAssetPageColumns(@RequestBody List<PageColumnVO> columns){
		Boolean saveAssetPageColumns = assetSettingsService.saveAssetPageColumns(columns,null);
		return ResultUtil.success(saveAssetPageColumns);
	}

	@PostMapping(value="/saveAssetPageColumns/{treeCode}")
	@ApiOperation(value="保存列数据(根据具体treeCode的值)",notes="")
	@SysRequestLog(description="保存列数据(根据具体treeCode的值)", actionType = ActionType.UPDATE,manually=false)
	public  Result<Boolean> saveAssetPageColumns(@RequestBody List<PageColumnVO> columns,@PathVariable("treeCode") String treeCode){
		Boolean saveAssetPageColumns = assetSettingsService.saveAssetPageColumns(columns,treeCode);
		return ResultUtil.success(saveAssetPageColumns);
	}
	
	@PostMapping(value="/getAssetPageColumns")
	@ApiOperation(value="获取列数据",notes="")
	@SysRequestLog(description="获取列数据", actionType = ActionType.SELECT,manually=false)
	public  Result<List<PageColumnVO>> getAssetPageColumns(){
		List<PageColumnVO> assetPageColumns = assetSettingsService.getAssetPageColumns(null);
		if(assetPageColumns==null||assetPageColumns.isEmpty())
		{
			assetPageColumns= assetSettingsService.getSystemPageColumns(null);
		}
		return ResultUtil.success(assetPageColumns);
	}
	@PostMapping(value="/getAssetPageColumns/{treeCode}")
	@ApiOperation(value="获取列数据(根据treeCode的值)",notes="")
	@SysRequestLog(description="获取列数据(根据treeCode的值)", actionType = ActionType.SELECT,manually=false)
	public  Result<List<PageColumnVO>> getAssetPageColumnsByTreeCode(@PathVariable("treeCode") String treeCode){
		List<PageColumnVO> assetPageColumns = assetSettingsService.getAssetPageColumns(treeCode);
		if(assetPageColumns==null||assetPageColumns.isEmpty())
		{
			assetPageColumns= assetSettingsService.getSystemPageColumns(treeCode);
		}
		return ResultUtil.success(assetPageColumns);
	}
	@GetMapping(value="/getSettingScope")
	@ApiOperation(value="获取自定义模板作用域",notes="")
	@SysRequestLog(description="获取自定义模板作用域", actionType = ActionType.SELECT,manually=false)
	public  Result<NameValue> getSettingScope(){
		NameValue settingScope = assetSettingsService.getSettingScope();
		return ResultUtil.success(settingScope);
	}
	
	@PostMapping(value="/saveSettingScope")
	@ApiOperation(value="保存自定义模板作用域数据",notes="")
	@SysRequestLog(description="保存自定义模板作用域", actionType = ActionType.UPDATE,manually=false)
	public  Result<Boolean> saveSettingScope(@RequestBody NameValue item){
		Boolean saveAssetPageColumns = assetSettingsService.saveSettingScope(item.getName());
		return ResultUtil.success(saveAssetPageColumns);
	}
	

	private List<AssetSystemAttributeSettings> getSystemAttributeSettings(List<AssetAttributeTreeNode> nodes,String treeCode) {
		List<AssetSystemAttributeSettings> settings = assetSystemAttributeSettingsService.queryAssetSystemAttributeSettings(treeCode);
		for(AssetSystemAttributeSettings setting : settings) {
			AssetSystemAttributeSettingsVO assetSystemAttributeSettingsVO = new AssetSystemAttributeSettingsVO(setting);
			SystemControlSettings systemSettings = assetSystemAttributeSettingsVO.getSystemSettings();
			CustomSettings originalCustomSettings = assetSystemAttributeSettingsVO.getCustomSettings();
			for(AssetAttributeTreeNode node :  nodes)
			{
				setSystemAttributeSettings(setting, systemSettings, originalCustomSettings, node);
			}
			
			setting.setCustomSettings(gson.toJson(originalCustomSettings));
		}
		
		return settings;
	}

	private void setSystemAttributeSettings(AssetSystemAttributeSettings setting, SystemControlSettings systemSettings,
			CustomSettings originalCustomSettings, AssetAttributeTreeNode node) {
		if("attribute".equals(node.getType())) {
 
			AssetSystemAttributeSettingsVO attribute =gson.fromJson(gson.toJson(node.getAttribute()), AssetSystemAttributeSettingsVO.class);
			
			if(setting.getName().equals(attribute.getName()) && setting.getType().equals(attribute.getType()) ) {
				
				CustomSettings customSettings = attribute.getCustomSettings();
				
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateVisible()))
				{
					setting.setVisible(customSettings.getVisible());
					originalCustomSettings.setVisible(customSettings.getVisible());
					if(originalCustomSettings.getChildrenControl()!=null&&!originalCustomSettings.getChildrenControl().isEmpty()) {
						for(CustomSettings child : originalCustomSettings.getChildrenControl()) {
							child.setVisible(customSettings.getVisible());
						}
					}
				}
				
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateDefaultValue())){
					originalCustomSettings.setDefaultValue(customSettings.getDefaultValue());
					originalCustomSettings.setDefaultValueType(customSettings.getDefaultValueType());
					originalCustomSettings.setDefaultValueBind(customSettings.getDefaultValueBind());
				}
				
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateDescription())) {
					originalCustomSettings.setDescription(customSettings.getDescription());
				}
				
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateInputMessage())) {
					originalCustomSettings.setInputMessage(customSettings.getInputMessage());
				}
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateLength()))
				{
					Integer length = customSettings.getLength();
					if(length==null)
					{
						length=systemSettings.getLengthMix();
					}
					if(length>=systemSettings.getLengthMix()&& length<=systemSettings.getLengthMax()) {
						originalCustomSettings.setLength(length);
					}
				}
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateTitle()))
				{
					originalCustomSettings.setTitle(customSettings.getTitle());
				}
				if(Boolean.TRUE.equals(systemSettings.getCanUpdateMust()))
				{
					originalCustomSettings.setIsMust(customSettings.getIsMust());
				}
				
				originalCustomSettings.setRegex(customSettings.getRegex());
				originalCustomSettings.setRegexMessage(customSettings.getRegexMessage());
			}else {
				logger.debug("name equals:{}",setting.getName().equals(attribute.getName()));
				logger.debug("name equals:{}",setting.getType().equals(attribute.getType()));
				return;
			}
		}else {
			
			if(node.getChildren()!=null&&!node.getChildren().isEmpty()) {
				for( AssetAttributeTreeNode an :node.getChildren() )
				{
					setSystemAttributeSettings(setting, systemSettings, originalCustomSettings, an);
				}
			}
			
			return;
		}
	}

	private List<AssetPanel> getAssetPanels(List<AssetAttributeTreeNode> nodes,String treeCode) {
		AssetSettings assetSetting = assetSettingsService.getAssetSettingByTreeCode(treeCode,"AssetPanels");
		List<AssetPanel> assetPanels = assetSettingsService.getAssetPanels(assetSetting);
		for(AssetPanel panel : assetPanels)
		{
			for(AssetAttributeTreeNode node : nodes)
			{
				if("panel".equals(node.getType())) {

					AssetPanel attribute =gson.fromJson(gson.toJson(node.getAttribute()), AssetPanel.class);
					
					if(panel.getName().equals(node.getName()))
					{
						if(Boolean.TRUE.equals(panel.getCanUpdateIndex()))
						{
							panel.setIndex(attribute.getIndex());
						}
						if(Boolean.TRUE.equals(panel.getCanUpdateTitle()))
						{
							panel.setTitle(attribute.getTitle());
						}
						if(Boolean.TRUE.equals(panel.getCanUpdateVisible()))
						{
							panel.setVisible(!Boolean.FALSE.equals(attribute.getVisible()));
						}
						
					}else {
						continue;
					}
				}else {
					continue;
				}
			}
		}
		
		return assetPanels;
	}
	

	/**
	 * 获得资产类型CombobxTree
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeComboboxTree")
	@ApiOperation(value="获得资产类型CombobxTree",notes="")
	@SysRequestLog(description="获得资产类型CombobxTree", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeTreeVO>> getAssetTypeComboboxTree() {
		List<AssetTypeTreeVO> list = assetSettingsService.getAssetTypeComboboxTree();
		return ResultUtil.success(list);
	}
	

}
