package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.json.JsonSanitizer;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetSettingsDao;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSettings;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetSettingsRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSettingsService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSystemAttributeSettingsService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeTemplateService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetAttributeTreeNode;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetPanel;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSystemAttributeSettingsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTemplateVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.alarmdeal.business.asset.vo.PageColumnVO;
import com.vrv.vap.alarmdeal.business.asset.vo.PageCustomColumnVO;
import com.vrv.vap.alarmdeal.business.asset.util.TreeFactory;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetSettingsServiceImpl extends BaseServiceImpl<AssetSettings, String> implements AssetSettingsService {

	@Autowired
	AssetSettingsRepository assetSettingsRepository;
		
	@Autowired
	AssetSettingsDao assetSettingsDao;
	
	@Autowired
	AssetTypeGroupService assetTypeGroupService;
	
	@Autowired
	AssetTypeService assetTypeService;
	
	@Autowired
	AssetTypeSnoService assetTypeSnoService;
	
	@Autowired
	AssetTypeTemplateService assetTypeTemplateService;
	
	@Autowired
	AssetSystemAttributeSettingsService assetSystemAttributeSettingsService;

	@Autowired
	private MapperUtil mapper;
	
	
	private static Logger logger = LoggerFactory.getLogger(AssetSettingsService.class);
	
 
	
	Gson  gson=new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
			.create();
	
	@Override
	public BaseRepository<AssetSettings, String> getRepository() {
		// TODO Auto-generated method stub
		return assetSettingsRepository;
	}
	
	
	@Override
	public NameValue getSettingScope()
	{
		NameValue result=new NameValue("基于资产类型","AssetType");
		AssetSettings assetSettings = this.getOne("SettingScope");
		if(assetSettings==null)
		{
			return result;
		}
		String data = assetSettings.getData();
		if(!StringUtils.isEmpty(data))
		{
			NameValue fromJson = gson.fromJson(data, NameValue.class);
			
			return fromJson;
		}
		return result;
	}
	
	@Override
	public Boolean saveSettingScope(String type) {
		NameValue data = null;
		if ("AssetTypeGroup".equals(type)) {
			data = new NameValue("基于资产一级类型", "AssetTypeGroup");
		} else if ("AssetType".equals(type)) {
			data = new NameValue("基于资产二级类型", "AssetType");
		} else if ("AssetTypeSno".equals(type)) {
			data = new NameValue("基于资产品牌型号", "AssetTypeSno");
		}else {
			return false;
		}

		AssetSettings assetSettings = this.getOne("SettingScope");
 
		if (assetSettings==null) {
			assetSettings=new AssetSettings();
			assetSettings.setGuid("SettingScope");
			assetSettings.setTitle("自定义模板作用域配置");

		} 
		assetSettings.setData(gson.toJson(data));
		getRepository().save(assetSettings);
		return true;
	}


	// 所有列数据，包括设置不显示的，主要是导出用了列的顺序（导出的列包括列中设置的不显示）--导出模板、导出数据用
	@Override
	public List<PageColumnVO> getAssetPageColumnsAll(String treeCode) {
		AssetSettings assetSettings = getAssetSettingByTreeCode(treeCode, "PageColumns");
		if (assetSettings == null) {
			return new ArrayList<>();
		}
		String data = assetSettings.getData();
		if (!StringUtils.isEmpty(data)) {
			List<PageColumnVO> fromJson = gson.fromJson(data, new TypeToken<List<PageColumnVO>>() {
			}.getType());

			return fromJson;
		}
		return new ArrayList<>();
	}

	// 获取显示的列，用于前台展示
	@Override
	public List<PageColumnVO> getAssetPageColumns(String treeCode) {
		List<PageColumnVO> assetPageColumnAlls = getAssetPageColumnsAll(treeCode);
		List<PageColumnVO> assetPageColumns = new ArrayList<PageColumnVO>();
		for (PageColumnVO vo : assetPageColumnAlls) {
			// check为true表示显示
			if (vo.getCheck()) {
				assetPageColumns.add(vo);
			}
		}
		return assetPageColumns;
	}


	/**
	 *  获取导入模板、导出的列模板信息（包括基本列+扩展字段列）
	 *  1. 获取基本列：根据当前配置的力度获取对应级别的资产类型配置的列，取值方式：当前没有，取父类，以此类推，直到顶层
	 *  2. 获取自定义扩展列：目前自定义模板基于三级资产类型配置的，比如：当前是二级资产类型的话，获取自定义模板
	 *   就是下面所有三级资产类型自定义模板集合；如果是一级类型，就是下面所有三级资产类型的集合
	 *
	 * @param treeCode 资产类型的treeCode
	 * @param type 当前资产类型；一级、二级、三级
	 * @param guid  三级资产类型guid
	 * @return list
	 * 2021 - 09 -16
	 */
	@Override
	public List<CustomSettings> getExcelColumns(String treeCode, String type, String guid) {
		logger.info("getExcelColumns sart,type:{}" , type);
		List<CustomSettings> result = new ArrayList<>();
		logger.info("treeCode :{}" , treeCode);
		//  获取treeCode对应所有列设置
		List<CustomSettings> systemAttributeCustomSettings = assetSystemAttributeSettingsService.getSystemAttributeCustomSettings(treeCode);
		result.addAll(systemAttributeCustomSettings);
		if (treeCode.startsWith("asset-NetworkDevice-Switch") || treeCode.equals("asset-NetworkDevice-Switch")) {
			result.addAll(getSnmpCustomSettings());
		}
		// 获取自定义模板信息
		List<AssetTypeTemplate> assetTypeTemplates = getAssetTemplateByTreeCode(treeCode);
		if (null != assetTypeTemplates) {
			result.addAll(handleTemplates(assetTypeTemplates));
		}
		// 获取treeCode列的排序
		List<PageColumnVO> pageColnums = getAssetPageColumnsAll(treeCode);
		// 设置列的顺序
		List<CustomSettings> customSettingSort = setColumsOder(result, pageColnums);
		return customSettingSort;
	}

	private List<CustomSettings>  handleTemplates(List<AssetTypeTemplate> assetTypeTemplates) {
		List<CustomSettings> result = new ArrayList<>();
        for(AssetTypeTemplate assetTypeTemplate : assetTypeTemplates){
			List<CustomSettings> customs  = getCustomSettings(assetTypeTemplate);
			if(customs.size() == 0){
				continue;
			}
			for(CustomSettings cus : customs){
				if(result.contains(cus)){
					continue;
				}
				result.add(cus);
			}
		}
        return result;
	}

	// 二级、一级模板
	private  List<AssetTypeTemplate> getAssetTemplateByTreeCode(String treeCode) {
		List<QueryCondition> cons = new ArrayList<>();
		List<QueryCondition> querys = new ArrayList<>();
		cons.add(QueryCondition.eq("status", 0));
		cons.add(QueryCondition.likeBegin("treeCode", treeCode+"-"));
		List<AssetTypeSno> snos = assetTypeSnoService.findAll(cons);
		List<String> typeGuids=new ArrayList<>();
		snos.forEach(a -> {
			typeGuids.add(a.getGuid());
		});
		querys.add(QueryCondition.in("guid", typeGuids));
		querys.add(QueryCondition.eq("deleteFlag", false));
		List<AssetTypeTemplate> templates =assetTypeTemplateService.findAll(querys);
		if(null == templates ||templates.size() == 0 ) {
			return null;
		}
		return templates;
	}


	private List<CustomSettings> setColumsOder(List<CustomSettings> result, List<PageColumnVO> pageColnums) {
		if(result.size() == 0 || pageColnums.size() == 0){
			return result;
		}
		//pageColnums按照index升序排序
		Collections.sort(pageColnums, new Comparator<PageColumnVO>() {
			@Override
			public int compare(PageColumnVO o1, PageColumnVO o2) {
				return o1.getIndex()-o2.getIndex();
			}
		});
		List<CustomSettings> resultNew = new ArrayList<CustomSettings>();
		for(PageColumnVO colVo: pageColnums){
			String name = colVo.getName();
			CustomSettings setting = getCustomSettingsByName(name,result);
			if(null == setting){
				continue;
			}
			resultNew.add(setting);
		}
		// 查询没有顺序的列
		result.removeAll(resultNew);
		if(result.size() >0 ){
			logger.info("pageColumns no sort:{}", JSON.toJSONString(result));
			resultNew.addAll(result);
		}
		return resultNew;
	}

	private CustomSettings getCustomSettingsByName(String name,List<CustomSettings> result) {
		for(CustomSettings custom: result){
			if(name.equalsIgnoreCase(custom.getName())){
				return custom;
			}
		}
		return null;
	}


	private List<CustomSettings> getSnmpCustomSettings() {
		List<CustomSettings> result=new ArrayList<>();
		/*
		 * case "SNMP版本": case "SNMP端口": case "SNMP用户名": case "SNMP加密级别": case
		 * "SNMP读团体字符串": case "SNMP私有密码": case "SNMP私钥加密方式": case "SNMP认证加密密码": case
		 * "SNMP认证加密方式":
		 */
		// String[] snmpAttributes=new String[] {"snmpVersion", "snmpPort",
		// "snmpUserName", "securityLevel", "readcommunityString",
		// "privPassWord", "privAlgorithm", "authPassWord", "authAlgorithm"};

		CustomSettings snmpVersion = getSnmpVersionCustomSettings();
		result.add(snmpVersion);

		CustomSettings snmpPort = getSnmpPortCustomSettings();
		result.add(snmpPort);

		CustomSettings snmpUserName = getSnmpUserNameCustomSettings();
		result.add(snmpUserName);

		CustomSettings securityLevel = getSnmpSecurityLevelCustomSettings();
		result.add(securityLevel);

		CustomSettings readcommunityString = getReadcommunityStringCustomSettings();
		result.add(readcommunityString);

		CustomSettings privPassWord = getPrivPassWordCustomSettings();
		result.add(privPassWord);

		CustomSettings authPassWord = getAuthPassWordCustomSettings();
		result.add(authPassWord);

		CustomSettings privAlgorithm = getPrivAlgorithmCustomSettings();
		result.add(privAlgorithm);

		CustomSettings authAlgorithm = getAuthAlgorithmCustomSettings();
		result.add(authAlgorithm);
		
		return result;
	}

	private List<CustomSettings> getCustomSettings(AssetTypeTemplate assetTypeTemplate)
	{
		if (assetTypeTemplate != null && !StringUtils.isEmpty(assetTypeTemplate.getFormdata())) {
			try {
				//logger.debug("json:" + assetTypeTemplate.getFormdata());
				AssetTemplateVO fromJson = gson.fromJson(assetTypeTemplate.getFormdata(), AssetTemplateVO.class);

				if (fromJson != null) {
					return fromJson.getCustomSettings();
				}
			} catch (Exception e) {
				logger.error("AssetTypeTemplate Json转换失败:", e);
			}
		}
		return new ArrayList<>();
	}
	
	private CustomSettings getAuthAlgorithmCustomSettings() {
		CustomSettings securityLevel=new CustomSettings();
		securityLevel.setAttributeType("custom");
		securityLevel.setDefaultValue("v1");
		securityLevel.setDefaultValueType("1");
		securityLevel.setDescription("资产监控协议信息，没有则无需填写。默认V1");
		securityLevel.setDescriptionTitle("必填");
		securityLevel.setInputMessage("请填写资产SNMP协议监控信息");
		securityLevel.setIsMust(true);
		securityLevel.setLength(2);
		securityLevel.setName("authAlgorithm");
		securityLevel.setPanel("snmp");
		securityLevel.setTitle("SNMP认证加密方式");
		securityLevel.setType("Select");
		securityLevel.setVisible(true);
		List<NameValue> option=new ArrayList<>();
		//"HMAC_MD5_96", "HMAC_SHA1_96"
		option.add(new NameValue("HMAC_MD5_96","HMAC_MD5_96"));
		option.add(new NameValue("HMAC_SHA1_96","HMAC_SHA1_96")); 
		
		securityLevel.setOption(option);
		return securityLevel;
	}
	private CustomSettings getPrivAlgorithmCustomSettings() {
		CustomSettings securityLevel=new CustomSettings();
		securityLevel.setAttributeType("custom");
		securityLevel.setDefaultValue("v1");
		securityLevel.setDefaultValueType("1");
		securityLevel.setDescription("资产监控协议信息，没有则无需填写。默认V1");
		securityLevel.setDescriptionTitle("必填");
		securityLevel.setInputMessage("请填写资产SNMP协议监控信息");
		securityLevel.setIsMust(true);
		securityLevel.setLength(2);
		securityLevel.setName("privAlgorithm");
		securityLevel.setPanel("snmp");
		securityLevel.setTitle("SNMP私钥加密方式");
		securityLevel.setType("Select");
		securityLevel.setVisible(true);
		List<NameValue> option=new ArrayList<>();
		// "CBC_DES", "CFB_AES_128", "3DES"
		option.add(new NameValue("CBC_DES","CBC_DES"));
		option.add(new NameValue("CFB_AES_128","CFB_AES_128"));
		option.add(new NameValue("3DES","3DES"));
		
		securityLevel.setOption(option);
		return securityLevel;
	}
	
	private CustomSettings getAuthPassWordCustomSettings() {
		CustomSettings snmpUserName=new CustomSettings();
		snmpUserName.setAttributeType("custom");
		snmpUserName.setDefaultValue("");
		snmpUserName.setDefaultValueType("0");
		snmpUserName.setDescription("资产监控协议信息，没有则无需填写。");
		snmpUserName.setDescriptionTitle("必填");
		snmpUserName.setInputMessage("请填写资产SNMP协议监控信息");
		snmpUserName.setIsMust(true);
		snmpUserName.setLength(30);
		snmpUserName.setName("authPassWord");
		snmpUserName.setPanel("snmp");
		snmpUserName.setTitle("SNMP认证加密密码");
		snmpUserName.setType("input");
		
		snmpUserName.setVisible(true);
		return snmpUserName;
	}
	
	private CustomSettings getPrivPassWordCustomSettings() {
		CustomSettings snmpUserName=new CustomSettings();
		snmpUserName.setAttributeType("custom");
		snmpUserName.setDefaultValue("");
		snmpUserName.setDefaultValueType("0");
		snmpUserName.setDescription("资产监控协议信息，没有则无需填写。");
		snmpUserName.setDescriptionTitle("必填");
		snmpUserName.setInputMessage("请填写资产SNMP协议监控信息");
		snmpUserName.setIsMust(true);
		snmpUserName.setLength(30);
		snmpUserName.setName("privPassWord");
		snmpUserName.setPanel("snmp");
		snmpUserName.setTitle("SNMP私有密码");
		snmpUserName.setType("input");
		
		snmpUserName.setVisible(true);
		return snmpUserName;
	}
	private CustomSettings getReadcommunityStringCustomSettings() {
		CustomSettings snmpUserName=new CustomSettings();
		snmpUserName.setAttributeType("custom");
		snmpUserName.setDefaultValue("public");
		snmpUserName.setDefaultValueType("1");
		snmpUserName.setDescription("资产监控协议信息，没有则无需填写。");
		snmpUserName.setDescriptionTitle("必填");
		snmpUserName.setInputMessage("请填写资产SNMP协议监控信息");
		snmpUserName.setIsMust(true);
		snmpUserName.setLength(30);
		snmpUserName.setName("readcommunityString");
		snmpUserName.setPanel("snmp");
		snmpUserName.setTitle("SNMP读团体字符串");
		snmpUserName.setType("input");
		
		snmpUserName.setVisible(true);
		return snmpUserName;
	}


	private CustomSettings getSnmpSecurityLevelCustomSettings() {
		CustomSettings securityLevel=new CustomSettings();
		securityLevel.setAttributeType("custom");
		securityLevel.setDefaultValue("v1");
		securityLevel.setDefaultValueType("1");
		securityLevel.setDescription("资产监控协议信息，没有则无需填写。默认V1");
		securityLevel.setDescriptionTitle("必填");
		securityLevel.setInputMessage("请填写资产SNMP协议监控信息");
		securityLevel.setIsMust(true);
		securityLevel.setLength(2);
		securityLevel.setName("securityLevel");
		securityLevel.setPanel("snmp");
		securityLevel.setTitle("SNMP加密级别");
		securityLevel.setType("Select");
		securityLevel.setVisible(true);
		List<NameValue> option=new ArrayList<>();
		
		option.add(new NameValue("NoAuthNoPriv","NoAuthNoPriv"));
		option.add(new NameValue("AuthNoPriv","AuthNoPriv"));
		option.add(new NameValue("AuthPriv","AuthPriv"));
		
		securityLevel.setOption(option);
		return securityLevel;
	}


	private CustomSettings getSnmpUserNameCustomSettings() {
		CustomSettings snmpUserName=new CustomSettings();
		snmpUserName.setAttributeType("custom");
		snmpUserName.setDefaultValue("");
		snmpUserName.setDefaultValueType("0");
		snmpUserName.setDescription("资产监控协议信息，没有则无需填写。");
		snmpUserName.setDescriptionTitle("必填");
		snmpUserName.setInputMessage("请填写资产SNMP协议监控信息");
		snmpUserName.setIsMust(true);
		snmpUserName.setLength(30);
		snmpUserName.setName("snmpUserName");
		snmpUserName.setPanel("snmp");
		snmpUserName.setTitle("SNMP用户名");
		snmpUserName.setType("input");
		
 
		snmpUserName.setVisible(true);
		return snmpUserName;
	}


	private CustomSettings getSnmpPortCustomSettings() {
		CustomSettings snmpPort=new CustomSettings();
		snmpPort.setAttributeType("custom");
		snmpPort.setDefaultValue("161");
		snmpPort.setDefaultValueType("1");
		snmpPort.setDescription("资产监控协议信息，没有则无需填写。161");
		snmpPort.setDescriptionTitle("必填");
		snmpPort.setInputMessage("请填写资产SNMP协议监控信息");
		snmpPort.setIsMust(true);
		snmpPort.setLength(5);
		snmpPort.setName("snmpPort");
		snmpPort.setPanel("snmp");
		snmpPort.setTitle("SNMP端口");
		snmpPort.setType("input");
		
		snmpPort.setRegex("^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$");
		snmpPort.setRegexMessage("端口：1~65535");
		
		snmpPort.setVisible(true);
		return snmpPort;
	}


	private CustomSettings getSnmpVersionCustomSettings() {
		CustomSettings snmpVersion=new CustomSettings();
		snmpVersion.setAttributeType("custom");
		snmpVersion.setDefaultValue("v1");
		snmpVersion.setDefaultValueType("1");
		snmpVersion.setDescription("资产监控协议信息，没有则无需填写。默认V1");
		snmpVersion.setDescriptionTitle("必填");
		snmpVersion.setInputMessage("请填写资产SNMP协议监控信息");
		snmpVersion.setIsMust(true);
		snmpVersion.setLength(2);
		snmpVersion.setName("snmpVersion");
		snmpVersion.setPanel("snmp");
		snmpVersion.setTitle("SNMP版本");
		snmpVersion.setType("Select");
		snmpVersion.setVisible(true);
		List<NameValue> option=new ArrayList<>();
		
		option.add(new NameValue("V1","v1"));
		option.add(new NameValue("V2","v2"));
		option.add(new NameValue("V3","v3"));
		
		snmpVersion.setOption(option);
		return snmpVersion;
	}

	/**
	 * 获取自定义属性：扩展属性  2021-9-16
	 *
	 * 顶层获取所有自定模板信息
	 *  一级类型：获取该一级类型下所有三级类型配置的自定义模板
	 *  二级类型：获取该二级类型下所有三级类型配置的自定义模板
	 *  三级类型：自己配置的自定义模板
	 * @param treeCode
	 * @return
	 */
	@Override
	public  List<PageCustomColumnVO> getCustomPageColumns(String treeCode)
	{
		List<PageCustomColumnVO> result = new ArrayList<>();
		// 自定义模板作用域配置：当前作用域是第几级
		String selecttype = this.getSettingScope().getName();
		List<Map<String, Object>> queryForList =null;
		if(StringUtils.isNotEmpty(treeCode)){
			// 获取当资产类型下的所有三级类型(包括顶层asset)
			// treeCode = JsonSanitizer.sanitize(treeCode);
			List<String> tempGuids= assetTypeTemplateService.geteAssetTypeSnoGuids(treeCode);
			if(null == tempGuids||tempGuids.size() == 0){
				return result;
			}
			queryForList = assetSettingsDao.getAssetTemplateAttribute(tempGuids);
		}else{ // 顶层资产类型：查询全部
			queryForList = assetSettingsDao.getAssetTemplateAttribute(null);
		}
		Map<String, Set<String>> mapResult = new HashMap<>();
        // 对查询所有的自定义模板属性，进行去重处理：名称+标题+类型作为唯一key,key包括多少个资产类型
		for (Map<String, Object> map : queryForList) {
			String codesJson = null;
			if(map.get("codes") instanceof  byte[] ){
				byte[] datas = (byte[])map.get("codes");
				codesJson = new String(datas);
			}else{
				codesJson =map.get("codes")==null?null:String.valueOf(map.get("codes"));
			}
			String fieldsJson = null;
			if(map.get("fields") instanceof  byte[] ){
				byte[] datas = (byte[])map.get("fields");
				fieldsJson = new String(datas);
			}else{
				fieldsJson =map.get("fields")==null?null:String.valueOf(map.get("fields"));;
			}
			String typesJson = null;
			if(map.get("types") instanceof  byte[] ){
				byte[] datas = (byte[])map.get("types");
				typesJson = new String(datas);
			}else{
				typesJson =map.get("types")==null?null:String.valueOf(map.get("types"));
			}
			String snoGuid =map.get("asset_type_guid")==null?"":String.valueOf(map.get("asset_type_guid"));
			String typeGuid =map.get("Guid")==null?"":String.valueOf(map.get("Guid"));
			String groupGuid =map.get("groupGuid")==null?"":String.valueOf(map.get("groupGuid"));
			if (codesJson != null && fieldsJson != null && typesJson !=null) {
				codesJson=JsonSanitizer.sanitize(codesJson);
				fieldsJson=JsonSanitizer.sanitize(fieldsJson);
				typesJson=JsonSanitizer.sanitize(typesJson);
				List<String> codes = gson.fromJson(codesJson, new TypeToken<List<String>>() {}.getType());
				List<String> fields = gson.fromJson(fieldsJson, new TypeToken<List<String>>() {}.getType());
				List<String> types = gson.fromJson(typesJson, new TypeToken<List<String>>() {}.getType());
				for (int i = 0; i < codes.size() && i < fields.size() && i < types.size(); i++) {
					String name = codes.get(i);
					String title = fields.get(i);
					String typet = types.get(i);
					String hash = getHash(name, title, typet);

					if (mapResult.containsKey(hash)) {
						Set<String> set = mapResult.get(hash);
						if ("AssetTypeSno".equals(selecttype)) { // 基于品牌型号
							set.add(snoGuid);
						} else if("AssetTypeGroup".equals(selecttype)){// 基于资产一级类型
							set.add(groupGuid);
						}else{
							set.add(typeGuid);
						}
					} else {
						Set<String> set = new HashSet<>();
						if ("AssetTypeSno".equals(selecttype)) { // 基于品牌型号
							set.add(snoGuid);
						} else if("AssetTypeGroup".equals(selecttype)){// 基于资产一级类型
							set.add(groupGuid);
						}else{
							set.add(typeGuid);
						}
						mapResult.put(hash, set);
					}
				}
			}
		}
		// 获取一、二、三级所有有效的名称和guid
		List<NameValue> allNameValues=new ArrayList<>();
		List<QueryCondition> quersy=new ArrayList<>();
		quersy.add(QueryCondition.eq("status", 0));
		if ("AssetTypeSno".equals(selecttype)) {
			List<AssetTypeSno> findAll = assetTypeSnoService.findAll(quersy);
			findAll.forEach(a -> {
				NameValue item = new NameValue(a.getName(), a.getGuid());
				allNameValues.add(item);
			});
		} else if("AssetTypeGroup".equals(selecttype)){
			List<AssetTypeGroup> findAll = assetTypeGroupService.findAll(quersy);
			findAll.forEach(a -> {
				NameValue item = new NameValue(a.getName(), a.getGuid());
				allNameValues.add(item);
			});
		} else {
			List<AssetType> findAll = assetTypeService.findAll(quersy);
			findAll.forEach(a -> {
				NameValue item = new NameValue(a.getName(), a.getGuid());
				allNameValues.add(item);
			});
		}
		// 获取对应资产类型下的列名，根据配置的是否显示，决定最终列是否展示
		List<PageColumnVO> assetPageColumns = this.getAssetPageColumnsAll(treeCode);

		// 解析自定义模板属性进行去重处理后的数据
		for (Map.Entry<String, Set<String>> entry : mapResult.entrySet()) {
			String key = entry.getKey();
			Set<String> value = entry.getValue();
			String[] split = key.split("-\\|-");
			if (split.length != 3) {
				continue;
			}
			String name = split[0];
			String title = split[1];
			String typet = split[2];
			PageCustomColumnVO vo = new PageCustomColumnVO();
			vo.setName(name);
			vo.setTitle(title);
			vo.setType(typet);
            // 有自定属性的资产类型guid进行：具备该属性的资产类型、不具备该属性的资产类型分类
			List<NameValue> failData = new ArrayList<>();

			List<NameValue> successData = new ArrayList<>();

			Set<String> guids = value;
			for (NameValue item : allNameValues) {
				if (guids.contains(item.getName())) {
					successData.add(item);
				} else {
					failData.add(item);
				}
			}
			vo.setSuccessData(successData);
			vo.setFailData(failData);

			vo.setAttributeType("custom");
			vo.setCheck(false);
			vo.setIndex(200);
			// 列表展示字段配置中自定义模板属性是否显示,column中check为true表示显示
			setColumCheck(assetPageColumns,vo); // 设置列是否显示
			result.add(vo);
		}
		Comparator<PageColumnVO> finalComparator= pageColumnsSort();
		
		result = result.stream().sorted(finalComparator).collect(Collectors.toList());
		return result;

    }

    @Override
    public List<PageColumnVO> getSystemPageColumns(String treeCode) {
        List<PageColumnVO> result = new ArrayList<>();
        // 获取reeCode对应列字段配置信息：取值规则，没有数据，查询父节点数据，以此类推
        List<PageColumnVO> assetPageColumns = this.getAssetPageColumnsAll(treeCode);
        // 获取treeCode下资产录入偏好配置信息：取值规则，没有数据，查询父节点数据，以此类推
        AssetSettings assetPanles = getAssetSettingByTreeCode(treeCode, "AssetPanels");
        List<QueryCondition> querys = new ArrayList<>();
        querys.add(QueryCondition.eq("visible", true));
        querys.add(QueryCondition.eq("assetSettingsGuid", assetPanles.getGuid()));
        List<AssetSystemAttributeSettings> allAttributes = assetSystemAttributeSettingsService.findAll(querys);
        for (AssetSystemAttributeSettings attribute : allAttributes) {
            AssetSystemAttributeSettingsVO setting = new AssetSystemAttributeSettingsVO(attribute);
            CustomSettings customSettings = setting.getCustomSettings();
            if (customSettings != null) {
                if (customSettings.getChildrenControl() == null || customSettings.getChildrenControl().isEmpty()) {
                    PageColumnVO column = new PageColumnVO();
                    column.setAttributeType("system");
                    column.setName(attribute.getName());
                    column.setTitle(customSettings.getTitle());
                    column.setType(attribute.getType());
                    setColumCheck(assetPageColumns, column); // 设置列是否显示
                    //解决系统字段中存在部分字段为特殊字段的情况
                    if ("extendInfo".equals(attribute.getPanel())) {
                        column.setAttributeType("custom");
                    }
                    result.add(column);
                } else {
                    for (CustomSettings child : customSettings.getChildrenControl()) {
                        if (Boolean.FALSE.equals(child.getVisible())) {
                            continue;
                        }
                        PageColumnVO column = new PageColumnVO();
                        column.setAttributeType("system");
                        column.setName(child.getName());
                        column.setTitle(child.getTitle());
                        column.setType(child.getType());
                        setColumCheck(assetPageColumns, column); // 设置列是否显示
                        //解决系统字段中存在部分字段为特殊字段的情况
                        if ("extendInfo".equals(attribute.getPanel())) {
                            column.setAttributeType("custom");
                        }
                        result.add(column);
                    }
                }

            }

        }
        Comparator<PageColumnVO> finalComparator = pageColumnsSort();
        result = result.stream().sorted(finalComparator).collect(Collectors.toList());
        return result;

	}

	// 设置列是否展示，及展示顺序 2021-08-12
	private void setColumCheck(List<PageColumnVO> assetPageColumns, PageColumnVO column) {
		column.setCheck(false);// 默认不展示
		column.setIndex(200);// 默认展示顺序
		for (PageColumnVO vo : assetPageColumns) {
			// 增加了列显示条件：以前只有显示的列才记录在列配置表中，现在显示与不显示的列都记录在里面，通过check来判断,check为true表示显示
			if (vo.getName().equals(column.getName())&&vo.getType().equals(column.getType())) {
				column.setIndex(vo.getIndex()); // 设置下标
				if ( vo.getCheck()) {
					column.setCheck(true);
					return;
				}
			}
		}
	}

	private Comparator<PageColumnVO> pageColumnsSort() {
		// index升序
		Comparator<PageColumnVO> byIndexASC = Comparator.comparing(PageColumnVO::getIndex);
 
		// named不分区大小写升序
		Comparator<PageColumnVO> byNameASC = Comparator.comparing(PageColumnVO::getName, String.CASE_INSENSITIVE_ORDER);

		// 联合排序
		Comparator<PageColumnVO> finalComparator = byIndexASC.thenComparing(byNameASC);
		return finalComparator;
	}
 
	
 
	
	  private String getHash(String name,String title,String type)
	  {
		  return name+"-|-"+title+"-|-"+type;
	  }
 
	
	@Override
	public Boolean saveAssetPageColumns(List<PageColumnVO> columns, String treeCode) {
		logger.info("资产列表展示字段配置保存，treeCode：{}" , treeCode);
		if (null == columns || columns.size() <= 0) {
			logger.info("columns is null");
			return true;
		}
		logger.info("columns:{}" , JSON.toJSONString(columns));
		String guid = "PageColumns";
		if (StringUtils.isNotEmpty(treeCode)) {
			guid = guid + "_" + treeCode;
		}
		AssetSettings assetSettings = this.getOne(guid);
		if (null == assetSettings) {
			assetSettings = new AssetSettings();
			assetSettings.setGuid(guid);
			assetSettings.setTitle("列设置");
		}

		Comparator<PageColumnVO> finalComparator = pageColumnsSort();
		columns = columns.stream().sorted(finalComparator).collect(Collectors.toList());
		for (int i = 0; i < columns.size(); i++) {
			PageColumnVO pageColumnVO = columns.get(i);
			pageColumnVO.setIndex(i);
		}
		assetSettings.setData(gson.toJson(columns));
		getRepository().save(assetSettings);

		return true;
	}


	@Override
	public String saveAssetPanels(List<AssetPanel> panles,String treeCode){
		String guid="AssetPanels";
		if(StringUtils.isNotEmpty(treeCode)){
			guid = guid+"_"+treeCode;
		}
		AssetSettings assetSettings = this.getOne(guid);
		if(assetSettings==null)
		{
			assetSettings = new AssetSettings();
			assetSettings.setGuid(guid);
			assetSettings.setTitle("资产面板配置");
		}
		assetSettings.setData(gson.toJson(panles));
		assetSettingsRepository.save(assetSettings);
		return guid;
	}

	@Override
	public List<AssetPanel> getAssetPanels(AssetSettings assetSettings){
		if(assetSettings==null)
		{
			return new ArrayList<>();
		}

		String data = assetSettings.getData();
		if(!StringUtils.isEmpty(data))
		{
			List<AssetPanel> fromJson = gson.fromJson(data, new TypeToken<List<AssetPanel>>(){}.getType());
			
			return fromJson;
		}
		return new ArrayList<>();
	}

	// 当前节点没有数据，查找父节点，直到顶层节点
	private AssetSettings getCycleAssetSettings(String guid, String treeCode) {
		String curGuid= guid;
		if (StringUtils.isNotEmpty(treeCode)) {
			curGuid = curGuid + "_" + treeCode;
		}
			// 获取资产类型对应的配置信息
		AssetSettings assetSettings =this.getOne(curGuid);
		// 查到数据返回或treeCode为空查顶层节点数据为空时返回
		if (null != assetSettings || StringUtils.isEmpty(treeCode)) {
			return assetSettings;
		}
		int index = treeCode.lastIndexOf('-');
		// index为-1时，代表就是最顶层的资产类型了,不用再往上找了，没有的话直接获取默认配置
		if(-1 == index){
			return getCycleAssetSettings(guid, null);
		}
		String parentTreeCode = treeCode.substring(0, index);
		return getCycleAssetSettings(guid, parentTreeCode);
	}
	@Override
	public List<AssetTypeTreeVO> getAssetTypeComboboxTree() {
	 	final List<AssetTypeTreeVO> listTree = new ArrayList<AssetTypeTreeVO>();
		//asset_type_group_tree
		List<QueryCondition> con1 = new ArrayList<>();
		con1.add(QueryCondition.eq("status", 0));
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<AssetTypeGroup> asset_type_group_list = assetTypeGroupService.findAll(con1, sort);
		List<AssetType> asset_type_list = assetTypeService.findAll(con1, sort);
	    List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(con1, sort); // 品牌
		
	    
	    List<AssetTypeGroup>  group_list =new ArrayList<>();
	    asset_type_group_list.forEach(group->{
	    	List<AssetType>  type_list =new ArrayList<>();
	    	asset_type_list.forEach(type->{
	    		
	    		if(type.getTreeCode().startsWith(group.getTreeCode()+"-")) {
	    			List<AssetTypeSno>  sno_list =new ArrayList<>();
	    			assetTypeSnos.forEach(sno->{
	    				
	    	    		if(sno.getTreeCode().startsWith(type.getTreeCode()+"-")) {
	    	    			sno_list.add(sno);
	    	    		}
	    			});
	    			
	    			if(!sno_list.isEmpty()) {
	    				type_list.add(type);
	    				NameValue settingScope = this.getSettingScope();
						if (settingScope != null && "AssetTypeSno".equals(settingScope.getName())) {
							List<AssetTypeTreeVO> sno_mapperTreeVO = assetTypeSnoService.mapperTreeVO(sno_list);
							listTree.addAll(sno_mapperTreeVO);
						}
	    			}
	    		}
	    	});
	    	
	    	
	    	
	    	if(!type_list.isEmpty())
	    	{
	    		group_list.add(group);
	    		List<AssetTypeTreeVO> asset_type_tree_list = assetTypeService.mapperTreeVO(type_list);
	    		listTree.addAll(asset_type_tree_list);
	    	}
	    });
		List<AssetTypeTreeVO> asset_type_group_treeVO = assetTypeGroupService.mapperTreeVO(group_list);
		listTree.addAll(asset_type_group_treeVO);

		return TreeFactory.buildTree(listTree);
	}

	@Override
	public AssetSettings getAssetSettingByTreeCode(String treeCode,String guidType) {
		AssetSettings assetSettings = getCycleAssetSettings(guidType,treeCode);
		AssetSettings settingNew =new AssetSettings();
		mapper.copy(assetSettings,settingNew);
		return settingNew;
	}

	/**
	 * 获取资产类型录入偏好配置: 2021-08-19
	 * 1. 首先根据配置的力度：如果配置的一级，展示一级的配置，如果一级没有展示顶层配置（顶层有初始化数据，不存在为空的情况）
	 * 2. 当前treeCode所在的资产类型不是当前配置的力度：如果当前资产类型是当前力度的下级，向上找到对应的力度级别的配置，
	 *    如果当前资产类型是当前力度的上级或同级，就用当期资产类型的配置
	 * 3. 查询当前资产类型配置时没有配置信息，向上，直到顶层配置
	 * @param treeCode
	 * @return
	 */
	@Override
	public List<AssetAttributeTreeNode> querySystemAttributeTree(String treeCode){
		logger.info("excSystemAttributeTree start");
		// 自定义模板作用域配置：据配置的力度
		String selecttype = this.getSettingScope().getName();
		logger.info("当前配置的力度:{}",selecttype);
		// 判断当前选择的资产类型属于几级
		int level = getAssetTypeLevel(treeCode);
		// 当前力度为一级类型
		if ("AssetTypeGroup".equalsIgnoreCase(selecttype)) {
			return getSystemAttributeTreeByLevel(1,level,treeCode);
		} else if("AssetType".equalsIgnoreCase(selecttype)){
			// 当前力度为二级类型
			return getSystemAttributeTreeByLevel(2,level,treeCode);
		} else {
			// 当前力度为三级类型
			return getSystemAttributeTreeByLevel(3,level,treeCode);
		}

	}

	/**
	 * getSystemAttributeTreeByLevel
	 * @param forceLevel 力度
	 * @param currentLevel 当前级别
	 * @param treeCode
	 * @return
	 */
	private List<AssetAttributeTreeNode> getSystemAttributeTreeByLevel(int forceLevel, int currentLevel, String treeCode) {
		// 力度与当前资产类型级别一致,力度是当前资产类型级别的下一级:用当前资产类型去获取模板
		if (forceLevel >= currentLevel) {
			return getSystemAttributeTreeByTreeCode(treeCode);
		}
		// 力度是当前资产类型级别的上一级：获取当前资产类型对应力度级别的父节点的资产类型，再去获取模板
		if (forceLevel < currentLevel) {
             String treeCodeStr = getTopLevelTreeCode(treeCode,forceLevel,currentLevel);
             if(StringUtils.isNotEmpty(treeCodeStr)){
				 return getSystemAttributeTreeByTreeCode(treeCodeStr);
			 }
		}
        return new ArrayList<AssetAttributeTreeNode>();
	}

	// 获取当前资产类型对应力度的资产类型:目前共三级：
	private String getTopLevelTreeCode(String treeCode, int forceLevel, int currentLevel) {
		switch (forceLevel) {
			case 1:  // 当前力度为一级类型
				// 当前资产类型为三级
				if(currentLevel == 3){
					int indexTwo = treeCode.lastIndexOf('-');
					if(-1 == indexTwo){
						return treeCode;
					}
					String two =  treeCode.substring(0, indexTwo); // 获取二级类型
					int indexOne = two.lastIndexOf('-');
					if(-1 == indexOne){
						return two;
					}
					return two.substring(0, indexOne); // 获取一级类型
				}
				// 当前资产类型为二级
				if(currentLevel == 2){
					int indexOne = treeCode.lastIndexOf('-');
					if(-1 == indexOne){
						return treeCode;
					}
					return treeCode.substring(0, indexOne); // 获取一级类型
				}
				return treeCode;
			case 2:  // 当前力度为二级类型:目前也只有三级类型
				if(currentLevel == 3){ // 当前资产类型为三级
					int indexTwo = treeCode.lastIndexOf('-');
					if(-1 == indexTwo){
						return treeCode;
					}
					return treeCode.substring(0, indexTwo); // 获取二级类型
				}
				return treeCode;
			default :
				return treeCode;
		}
	}

	private List<AssetAttributeTreeNode> getSystemAttributeTreeByTreeCode(String treeCode) {
		List<AssetAttributeTreeNode> result = new ArrayList<>();
		AssetSettings assetSetting = getAssetSettingByTreeCode(treeCode, "AssetPanels");
		List<AssetPanel> assetPanels = getAssetPanels(assetSetting);
		for (AssetPanel panel : assetPanels) {
			AssetAttributeTreeNode parentNode = new AssetAttributeTreeNode(panel);
			List<QueryCondition> querys = new ArrayList<>();
			querys.add(QueryCondition.eq("panel", panel.getName()));
			querys.add(QueryCondition.eq("assetSettingsGuid", assetSetting.getGuid()));
			Sort order = Sort.by(Direction.ASC, "guid");
			List<AssetSystemAttributeSettings> findAll = assetSystemAttributeSettingsService.findAll(querys, order);
			List<AssetAttributeTreeNode> children = parentNode.getChildren();
			for (AssetSystemAttributeSettings setting : findAll) {
				AssetSystemAttributeSettingsVO customSettings = new AssetSystemAttributeSettingsVO(setting);
				AssetAttributeTreeNode child = new AssetAttributeTreeNode(customSettings, 0);
				children.add(child);
			}
			result.add(parentNode);
		}
		return result;
	}
	// 获取当前treeCode属于几级资产：1表示一级资产，2表示二级资产，3表示三级资产，0表示不是资产类型
	private int getAssetTypeLevel(String treeCode) {
		if(StringUtils.isEmpty(treeCode)){ // treeCode为空默认为顶层
			return 0;
		}
		long number = 0;
		List<QueryCondition> querys = new ArrayList<>();
		querys.add(QueryCondition.eq("treeCode", treeCode));
		number = assetTypeGroupService.count(querys);
		if (number > 0) {
			return 1;
		}
		number = assetTypeService.count(querys);
		if (number > 0) {
			return 2;
		}
		number = assetTypeSnoService.count(querys);
		if (number > 0) {
			return 3;
		}
		return 0;
	}
}
