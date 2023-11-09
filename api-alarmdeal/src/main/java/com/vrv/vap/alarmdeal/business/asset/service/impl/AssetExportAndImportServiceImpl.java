package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.PushService;
import com.vrv.vap.alarmdeal.business.asset.model.*;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import com.vrv.vap.alarmdeal.business.asset.util.ExecutorServiceVrvUtil;
import com.vrv.vap.alarmdeal.business.asset.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.vo.*;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.alarmdeal.frameworks.feign.ServerSystemFegin;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.exportAndImport.excel.util.DateUtils;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * 资产导入导出实现类
 * @author wd-pc
 *
 */
@Service
@Transactional
public class AssetExportAndImportServiceImpl implements AssetExportAndImportService {

	private static Logger logger = LoggerFactory.getLogger(AssetExportAndImportServiceImpl.class);
	@Autowired
	private FileConfiguration fileConfiguration;
	@Autowired
	private AssetService assetService;
	@Autowired
	private AssetTypeService assetTypeService;
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	@Autowired
	private AssetExtendService assetExtendService;
	@Autowired
	private AssetTypeGroupService assetTypeGroupService;

	@Autowired
	AssetSettingsService assetSettingsService;

	@Autowired
	private AdminFeign adminFeign;

	@Autowired
	private ServerSystemFegin fileService;
	@Autowired
	PushService pushService;

	@Autowired
	private MapperUtil mapper;
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	@Autowired
	private AssetImportLogService assetImportLogService;

	@Autowired
    private BaseDataRedisCacheService baseDataRedisCacheService;
	@Autowired
	private AssetClassifiedLevel assetClassifiedLevel;

	@Autowired
	private AssetBaseDataService assetBaseDataService;

	@Autowired
	private AssetTemplateInitDataService asseTemplateInitDataService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TerminalAssteInstallTimeService terminalAssteInstallTimeService;

	// 保密产品梳理
	private String safeSecretProduceNum = "保密产品数量";
	private int batchNum = 500;  // 批量保存一次保存的最多条数

	private int importNum = 500;  // 大于500条采用异步，小于500采用同步

	private String responsibleCodeTitle = "责任人编号";

	private List<String> safeProduceNumTreeCode = Arrays.asList(new String[]{"asset-Host", "asset-service"});

	// 资产所有涉密等级
	private List<BaseDictAll> baseDictAlls;



	@Override
	public Result<String> exportAssetInfo(AssetSearchVO assetSearchVO, List<String> types, String token) {
		String filePath = getFilePath(token);
		File targetFile = new File(fileConfiguration.getFilePath());
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		logger.info("filePath: {}", filePath);
		OutputStream out = null;
		HSSFWorkbook workbook = null;
		try {
			try {
				out = new FileOutputStream(filePath);
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException: {}", e);
			}
			// 生成Excel
			workbook = new HSSFWorkbook(); // POI生成对象
			ExportExcelUtils excelUtil = new ExportExcelUtils();
			VData<List<User>> allUser = adminFeign.getAllUser();
			List<User> allUsers = new ArrayList<>();
			if (allUser != null && "0".equals(allUser.getCode())) {
				allUsers = allUser.getData();
			}

			List<BaseSecurityDomain> allDomains = assetBaseDataService.queryAllDomain();
			List<String> userNames = new ArrayList<>();
			allUsers.forEach(a -> {
				userNames.add(a.getName());
			});
			List<String> orgNames = new ArrayList<>();
			List<BaseKoalOrg> userOrgs = getUserOrgs();
			userOrgs.forEach(a -> orgNames.add(a.getName()));
			// 获取配置的作用域
			NameValue settingScope = assetSettingsService.getSettingScope();
			String name = settingScope.getName();
			// 资产所有涉密等级
			baseDictAlls =assetClassifiedLevel.findAll();
			logger.info("settingScope name:{}", name);
			if(!"AssetTypeGroup".equals(name)){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"目前模板作用域配置只支持一级资产类型,当前为："+name);
			}
			assetTypeGroupHandle(workbook, allDomains, allUsers, userOrgs, excelUtil, assetSearchVO);
			workbook.write(out);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("IOException:{}", e);
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(out);
		}
		return ResultUtil.success(token);


	}


	// 作用于为一级类型处理
	private void assetTypeGroupHandle(HSSFWorkbook workbook, List<BaseSecurityDomain> allDomains, List<User> allUsers, List<BaseKoalOrg> userOrgs, ExportExcelUtils excelUtil, AssetSearchVO assetSearchVO) throws Exception {
		Map<String, List<AssetType>> assetTypesMap = new HashMap<String, List<AssetType>>();
		Map<String, String> uniqueCodeAndTreeCodeMap = new HashMap<String, String>();
		Map<String, List<AssetExportDataVO>> assetTypeGroup = assetService.getAssetByTypeGroup(assetSearchVO, assetTypesMap, uniqueCodeAndTreeCodeMap);
		for (Map.Entry<String, List<AssetExportDataVO>> entry : assetTypeGroup.entrySet()) {
			String key = entry.getKey();
			List<AssetExportDataVO> value = entry.getValue();
			String sheetName = uniqueCodeAndTreeCodeMap.get(key + "uniqueCode");
			String treeCode = uniqueCodeAndTreeCodeMap.get(key + "treeCode");
			List<QueryCondition> cons = new ArrayList<>();
			cons.add(QueryCondition.eq("status", 0));
			cons.add(QueryCondition.likeBegin("treeCode", treeCode));
			List<AssetTypeSno> snos = assetTypeSnoService.findAll(cons);
			List<AssetExportDataVO> list = value;
			// 导出不能超过6万条
			if (null != value && value.size() > 60000) {
				list = value.subList(0, 60000);
				logger.error("导出不能超过6万条：" + value.size());
			}
			List<CustomSettings> excelColumns = assetSettingsService.getExcelColumns(treeCode, "assetTypeGroup", key);
			// asset-Host 终端  asset-service 服务器增加保密产品数据 2022-04-12（目前自监管配置的一级资产类型,只处理了配置的一级资产类型）
			boolean isSafeProduct = false;
			if (safeProduceNumTreeCode.contains(treeCode)) {
				isSafeProduct = true;
			}
			exportExcelByEntities(workbook, excelUtil, sheetName, list, excelColumns, allDomains, allUsers, assetTypesMap.get(key), snos, userOrgs, isSafeProduct,false,null);
		}
	}


	private String getFilePath(String token) {
		String fileName = token + ".xls";
		;// 文件名称
		String filePath = Paths.get(fileConfiguration.getFilePath(), fileName).toString();
		return filePath;
	}

	@Override
	public Result<String> exportAssetInfo(AssetSearchVO assetSearchVO, List<String> types) {
		logger.info("exportAssetInfo start，types：{}", types);
		String uuid = "资产导出" + DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
		return exportAssetInfo(assetSearchVO, types, uuid);
	}

	@Override
	public Result<String> exportAssetInfoTemplate(List<String> types) {
		logger.info("exportAssetInfoTemplate start ,types:{}", types);
		// String uuid=UUIDUtils.get32UUID();
		String uuid = "导入模板" + DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
		String fileName = uuid + ".xls";// 文件名称
		String filePath = Paths.get(fileConfiguration.getFilePath(), fileName).toString();
		// filePath ="D:\\file.xls";
		File targetFile = new File(fileConfiguration.getFilePath());
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		logger.info("filePath: {}", filePath);
		OutputStream out = null;
		HSSFWorkbook workbook = null;
		try {
			out = new FileOutputStream(filePath);

			// 生成Excel
			workbook = new HSSFWorkbook(); // POI生成对象
			ExportExcelUtils eeu = new ExportExcelUtils();
			VData<List<User>> allUser = adminFeign.getAllUser();
			List<User> allUsers = new ArrayList<>();
			if (allUser != null && "0".equals(allUser.getCode())) {
				allUsers = allUser.getData();
			}
			List<BaseSecurityDomain> allDomains = assetBaseDataService.queryAllDomain();
			List<BaseKoalOrg> userOrgs = getUserOrgs();
			List<QueryCondition> con1 = new ArrayList<>();
			con1.add(QueryCondition.eq("status", 0));
			if (types != null && !types.isEmpty()) {
				con1.add(QueryCondition.in("guid", types));
			}
			Sort sort = Sort.by(Direction.ASC, "orderNum");
			NameValue settingScope = assetSettingsService.getSettingScope();
			baseDictAlls = assetClassifiedLevel.findAll();
			logger.info("settingSope is:{}", settingScope.getName());
			if(!"AssetTypeGroup".equals(settingScope.getName())){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"目前模板作用域配置只支持一级资产类型,当前为："+settingScope.getName());
			}
			List<AssetTypeGroup> assetGroupAll = assetTypeGroupService.findAll(con1, sort);
			if (null == assetGroupAll || assetGroupAll.size() <= 0) {
				logger.info("当前处于一级资产类型，传入的guid获取不到对应信息");
				return ResultUtil.success(uuid);
			}
			for (AssetTypeGroup group : assetGroupAll) {
				String sheetName = group.getUniqueCode();
				// 获取一级下的所有三级类型
				List<QueryCondition> cons = new ArrayList<>();
				cons.add(QueryCondition.eq("status", 0));
				cons.add(QueryCondition.likeBegin("treeCode", group.getTreeCode()));
				List<AssetType> assetTypes = assetTypeService.findAll(cons); // 获取当前一级下的所有二级类型，为模板中对资产类型进行校验用
				List<AssetTypeSno> snos = assetTypeSnoService.findAll(cons); // 获取当前一级下的所有三级类型，为模板中对品牌型号进行校验用
				List<CustomSettings> excelColumns = assetSettingsService.getExcelColumns(group.getTreeCode(), "assetTypeGroup", group.getGuid());
				exportExcelByEntities(workbook, eeu, sheetName, null, excelColumns, allDomains, allUsers, assetTypes, snos, userOrgs, false,true, asseTemplateInitDataService.getTypeByTreeCode(group.getTreeCode()+"-"));
			}
			// 输入文件流
			try {
				workbook.write(out);
			} catch (IOException e) {
				logger.error("IOException: ", e);
			}
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException: {}", e);
		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ResultUtil.success(uuid);


	}

	private List<BaseKoalOrg> getUserOrgs() {
		List<BaseKoalOrg> allOrg = new ArrayList<>();
		try {
			VData<BaseKoalOrg> orgByUser = adminFeign
					.getOrgByUser(Integer.toString(SessionUtil.getCurrentUser().getId()));
			if (orgByUser != null && "0".equals(orgByUser.getCode())) {
				BaseKoalOrg rootOrg = orgByUser.getData();
				allOrg.add(rootOrg);
				VData<List<BaseKoalOrg>> getOrgChildren = adminFeign.getOrgChildren(rootOrg.getCode());
				if (getOrgChildren != null && "0".equals(getOrgChildren.getCode())) {
					allOrg.addAll(getOrgChildren.getData());
				}
			}
			logger.debug("allOrg size:{} ", allOrg.size());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return allOrg;
	}

	private void exportExcelByEntities(HSSFWorkbook workbook, ExportExcelUtils eeu, String sheetName, List<AssetExportDataVO> rowData, List<CustomSettings> excelColumns, List<BaseSecurityDomain> allDomains, List<User> allUsers
			, List<AssetType> assetTypeList, List<AssetTypeSno> assetTypeSnoList, List<BaseKoalOrg> orgs, boolean isSafeProductNum,boolean isImportTemplate,String type
	)
			throws Exception {
		List<String> assetSnoNames = new ArrayList<>();
		List<String> assetTypeNames = new ArrayList<>();
		assetTypeList.forEach(a -> {
			assetTypeNames.add(a.getName());
		});
		if(assetTypeSnoList.size() > 0) {
			assetTypeSnoList.forEach(a -> {
				assetSnoNames.add(a.getName());
			});
		}
		String[] typeName = assetTypeNames.toArray(new String[assetTypeNames.size()]);
		String[] assetSnoName = assetSnoNames.toArray(new String[assetSnoNames.size()]);
		List<String> userNames = new ArrayList<>();
		allUsers.forEach(a -> {
			userNames.add(a.getName());
		});

		List<String> domainNames = new ArrayList<>();
		allDomains.forEach(a -> {
			domainNames.add(a.getDomainName());
		});
		List<String> orgNames = new ArrayList<>();
		orgs.forEach(a -> orgNames.add(a.getName()));
		List<String> headColumns = new ArrayList<>();
		List<ExcelValidationData> validationDatas = new ArrayList<>();
		List<String> helpDocuments = new ArrayList<>();
		List<List<String>> rows = new ArrayList<>();
		helpDocuments.add("模板填写说明：");
		if (excelColumns != null) {
			int index = 0;
			int helpIndex = 1;
			for (CustomSettings column : excelColumns) {
				headColumns.add(column.getTitle());
				ExcelValidationData excelValidationData = getExcelValidationData(index, column, typeName, assetSnoName, domainNames, userNames, orgNames);
				if (excelValidationData != null) {
					validationDatas.add(excelValidationData);
					if (StringUtils.isNotEmpty(excelValidationData.getPromptContent())) {
						helpDocuments.add(helpIndex + "、" + column.getTitle() + "：" + excelValidationData.getPromptContent());
						helpIndex++;
						// 责任人姓名存在，填写模板 2021-09-17(模板中只配置了责任人姓名，没有配置责任编号，代码中进行关联处理)
						if ("system".equals(column.getAttributeType()) && "responsibleName".equalsIgnoreCase(column.getName())) {
							boolean isMust = Boolean.TRUE.equals(column.getIsMust());
							if (isMust) {
								helpDocuments.add(helpIndex + "、" + responsibleCodeTitle + "：" + responsibleCodeTitle + "为必填内容");
							} else {
								helpDocuments.add(helpIndex + "、" + responsibleCodeTitle + "：" + responsibleCodeTitle);
							}
							helpIndex++;
						}
					}
					// 责任人姓名存在，新加code 2021-08-24
					if ("system".equals(column.getAttributeType()) && "responsibleName".equalsIgnoreCase(column.getName())) {
						headColumns.add(responsibleCodeTitle);
						boolean isMust = Boolean.TRUE.equals(column.getIsMust());
						index = index + 1;
						// 必填时增加提示
						if (isMust) {
							ExcelValidationData validate = new ExcelValidationData(index, 0, 30, "必填", "提示", "必填", "责任人编号为必填内容");
							validationDatas.add(validate);
						}
					}
					index++;
				}

			}
			Map<String, Map<String, String>> cacheMap = new HashMap<>();
			if (rowData != null && !rowData.isEmpty()) {
				for (AssetExportDataVO asset : rowData) {
					Map<String, Object> extendMap = new HashMap<>();
					String extendInfos = asset.getExtendInfos();
					if (!StringUtils.isEmpty(extendInfos)) {
						extendMap = JsonMapper.fromJsonString(extendInfos, Map.class);
					}
					List<String> row = getRowData(excelColumns, asset, extendMap, cacheMap, allDomains, allUsers, assetTypeList, assetTypeSnoList, orgs);
					// 增加保密产品数量 2021-08-16 没有在模板中配置,手动增加 2022-4-12
					if (isSafeProductNum) {
						row.add(asset.getSafeSecretProduceNum() + "");
					}
					rows.add(row);
				}
			}
			if(isImportTemplate){  // 导入模板增加 ：导入示例数据，只针对一级资产类型 2022-09-16
				rows.add(asseTemplateInitDataService.getInitDataByType(type,excelColumns));
			}
		}

		HSSFRichTextString helpDocumentsRich = new HSSFRichTextString(StringUtils.join(helpDocuments, "\r\n"));
		//设置第一行大写
		helpDocumentsRich.applyFont(0, helpDocuments.get(0).length(), eeu.getDefaultBoldFont(workbook));
		// 服务器和终端 增加保密产品数量定制的字段加上 2022-04-12
		if (rowData != null && !rowData.isEmpty() && isSafeProductNum) { // 暂时只针对导出数据，后面加上导出模板把这个判断直接去掉
			headColumns.add(safeSecretProduceNum);
		}
		eeu.exportExcel(workbook, sheetName, headColumns.toArray(new String[headColumns.size()]), rows, validationDatas, helpDocumentsRich);
	}


	private List<String> getRowData(List<CustomSettings> excelColumns, AssetExportDataVO asset, Map<String, Object> extendMap, Map<String, Map<String, String>> cacheMap, List<BaseSecurityDomain> allDomains, List<User> allUsers, List<AssetType> assetTypeList, List<AssetTypeSno> assetTypeSnoList, List<BaseKoalOrg> orgs) {
		List<String> row = new ArrayList<>();
		for (CustomSettings column : excelColumns) {
			String value = getColumnValue(column, asset, extendMap, cacheMap, allDomains, allUsers, assetTypeList, assetTypeSnoList, orgs);
			// 存在责任人姓名，导出时增加责任人code 2021-08-24
			if ("system".equals(column.getAttributeType()) && "responsibleName".equalsIgnoreCase(column.getName())) {
				String responsibleCode = asset.getResponsibleCode();
				row.add(value);
				row.add(responsibleCode);
			} else {
				row.add(value);
			}
		}
		return row;
	}

	private String getColumnValue(CustomSettings column, Asset asset, Map<String, Object> assetExtendMap, Map<String, Map<String, String>> cacheMap, List<BaseSecurityDomain> allDomains,
								  List<User> allUsers, List<AssetType> assetTypeList, List<AssetTypeSno> assetTypeSnoList, List<BaseKoalOrg> orgs) {
		String value = "";

		if ("system".equals(column.getAttributeType())) {

			switch (column.getName()) {
				case "name":
					value = asset.getName();
					break;
				case "ip":
					value = asset.getIp();
					break;
				case "typeName":
				case "assetTypeName":
					if (cacheMap.containsKey("assetTypeName")) {
						Map<String, String> typeSnoNameMap = cacheMap.get("assetTypeName");
						if (typeSnoNameMap.containsKey(asset.getAssetType())) {
							value = typeSnoNameMap.get(asset.getAssetType());
						} else {
							for (AssetType assetType : assetTypeList) {
								if (asset.getAssetType().equals(assetType.getGuid())) {
									value = assetType.getName();
									break;
								}
							}
							typeSnoNameMap.put(asset.getAssetType(), value);
							cacheMap.put("assetTypeName", typeSnoNameMap);
						}
					} else {
						for (AssetType assetType : assetTypeList) {
							if (asset.getAssetType().equals(assetType.getGuid())) {
								value = assetType.getName();
								break;
							}
						}
						Map<String, String> typeSnoNameMap = new HashMap<>();
						typeSnoNameMap.put(asset.getAssetType(), value);
						cacheMap.put("assetTypeName", typeSnoNameMap);
					}
					break;

				case "typeSnoName":
				case "assetTypeSnoName":
					if (cacheMap.containsKey("assetTypeSnoName")) {
						Map<String, String> typeSnoNameMap = cacheMap.get("assetTypeSnoName");
						if (typeSnoNameMap.containsKey(asset.getAssetTypeSnoGuid())) {
							value = typeSnoNameMap.get(asset.getAssetTypeSnoGuid());
						} else {
							for (AssetTypeSno assetTypeSno : assetTypeSnoList) {
								if (asset.getAssetTypeSnoGuid().equals(assetTypeSno.getGuid())) {
									value = assetTypeSno.getName();
									break;
								}
							}
							typeSnoNameMap.put(asset.getAssetTypeSnoGuid(), value);
							cacheMap.put("assetTypeSnoName", typeSnoNameMap);
						}
					} else {
						if (StringUtils.isNotEmpty(asset.getAssetTypeSnoGuid())) {
							for (AssetTypeSno assetTypeSno : assetTypeSnoList) {
								if (asset.getAssetTypeSnoGuid().equals(assetTypeSno.getGuid())) {
									value = assetTypeSno.getName();
									break;
								}
							}
						}
						Map<String, String> typeSnoNameMap = new HashMap<>();
						typeSnoNameMap.put(asset.getAssetTypeSnoGuid(), value);
						cacheMap.put("assetTypeSnoName", typeSnoNameMap);
					}
					break;
				case "mac":
					value = asset.getMac();
					break;
				case "assetNum":
					value = asset.getAssetNum();
					break;
				case "assetUse":
					value = asset.getAssetUse();
					break;
				case "location":
					value = asset.getLocation();
					break;
				case "assetDescribe":
					value = asset.getAssetDescribe();
					break;
				case "core":
					value = Boolean.TRUE.equals(asset.getCore()) ? "是" : "否";
					break;
				case "securityGuid":
				case "securityName":
					if (!StringUtils.isEmpty(asset.getSecurityGuid())) {
						if (cacheMap.containsKey("securityName")) {
							Map<String, String> typeSnoNameMap = cacheMap.get("securityName");
							if (typeSnoNameMap.containsKey(asset.getSecurityGuid())) {
								value = typeSnoNameMap.get(asset.getSecurityGuid());
							} else {
								for (BaseSecurityDomain domain : allDomains) {
									if (domain.getCode().equals(asset.getSecurityGuid())) {

										value = domain.getDomainName();
										break;
									}
								}

								typeSnoNameMap.put(asset.getSecurityGuid(), value);
							}
						} else {
							for (BaseSecurityDomain domain : allDomains) {
								if (domain.getCode().equals(asset.getSecurityGuid())) {

									value = domain.getDomainName();
									break;
								}
							}

							Map<String, String> typeSnoNameMap = new HashMap<>();
							typeSnoNameMap.put(asset.getSecurityGuid(), value);
							cacheMap.put("securityName", typeSnoNameMap);
						}


					}
					break;

				case "employeeCode":
					if (!StringUtils.isEmpty(asset.getEmployeeCode1())) {
						if (cacheMap.containsKey("employeeCode")) {
							Map<String, String> typeSnoNameMap = cacheMap.get("employeeCode");
							if (typeSnoNameMap.containsKey(asset.getEmployeeCode1())) {
								value = typeSnoNameMap.get(asset.getEmployeeCode1());
							} else {
								for (User user : allUsers) {
									if (user.getId().equals(asset.getEmployeeCode1())) {

										value = user.getName();
										break;
									}
								}

								typeSnoNameMap.put(asset.getEmployeeCode1(), value);
							}
						} else {
							for (User user : allUsers) {
								if (user.getName().equals(asset.getEmployeeCode1())) {

									value = user.getName();

									break;
								}
							}

							Map<String, String> typeSnoNameMap = new HashMap<>();
							typeSnoNameMap.put(asset.getEmployeeCode1(), value);
							cacheMap.put("employeeCode", typeSnoNameMap);
						}

					}
					break;
				case "serialNumber":  // 序列号 2021-08-16
					value = asset.getSerialNumber();
					break;
				case "equipmentIntensive": // 涉密等级 绝密5，机密4，秘密3，内部2，非密1
					value = assetClassifiedLevel.getValueByCode(asset.getEquipmentIntensive(), baseDictAlls);
					break;
				case "orgName": // 单位
					value = asset.getOrgName();
					break;
				case "responsibleName": //责任人姓名
					value = asset.getResponsibleName();
					break;
				case "termType": //国产与非国产 2021-08-20  1：表示国产 2：非国产
					if ("1".equalsIgnoreCase(asset.getTermType())) {
						value = "是";
					}
					if ("2".equalsIgnoreCase(asset.getTermType())) {
						value = "否";
					}
					break;
				case "isMonitorAgent": //（终端类型）1.应安装；2.未安装
					if ("1".equalsIgnoreCase(asset.getIsMonitorAgent())) {
						value = "已安装";
					}
					if ("2".equalsIgnoreCase(asset.getIsMonitorAgent())) {
						value = "未安装";
					}
					break;
				case "osSetuptime": //终端类型操作系统安装时间e
					if(null != asset.getOsSetuptime()){
						value = DateUtils.date2Str(asset.getOsSetuptime());
					}
					break;
				case "osList": //终端类型安装操作系统
					value = asset.getOsList();
					break;
				case "terminalType": //终端类型 ：运维终端/用户终端
					String terminalType = asset.getTerminalType();
					if ("1".equalsIgnoreCase(terminalType)) {
						value = "用户终端";
					}
					if ("2".equalsIgnoreCase(terminalType)) {
						value = "运维终端";
					}
					break;
				case "vid": //VID
					value = asset.getVid();
					break;
				case "pid": //PID
					value = asset.getPid();
					break;
				case "operationUrl": //管理入口url
					value = asset.getOperationUrl();
					break;
				default:
					break;
			}

		} else {
			Object data = assetExtendMap.get(column.getName());
			if (null != data) {
				value = String.valueOf(data);
			}
		}
		return value;
	}

	private ExcelValidationData getExcelValidationData(int index, CustomSettings column
			, String[] assetTypeName, String[] assetSnoNames
			, List<String> domainNames
			, List<String> userNames
			, List<String> orgNames
	) {
		boolean isMust = Boolean.TRUE.equals(column.getIsMust());


		int colIndex = index;
		int textMinLenth = (isMust) ? 1 : 0;
		int textMaxLenth = (column.getLength() == null ? 200 : column.getLength().intValue());
		String errorTitle = "错误提示";
		String errorMsg = column.getTitle() + "填写错误";
		String promptTitle = "";
		String promptContent = "";


		String descriptionTitle = column.getDescriptionTitle();
		String description = column.getDescription();

		if (StringUtils.isNotEmpty(descriptionTitle)) {
			promptTitle = descriptionTitle;
		}

		if (StringUtils.isEmpty(promptTitle)) {
			if (isMust) {
				promptTitle = "必填";
			} else {
				promptTitle = column.getTitle();
			}
		} else {
			if (!descriptionTitle.contains("必填")) {
				promptTitle += "(必填)";
			}
		}

		if (StringUtils.isEmpty(description)) {
			if (isMust) {
				promptContent = column.getTitle() + "为必填内容";
			}
		} else {
			promptContent = column.getTitle() + "为必填内容;" + description;
		}
		ExcelValidationData excelValidationData = null;//new ExcelValidationData(colIndex, textMinLenth, textMaxLenth,errorTitle, errorMsg,promptTitle,promptContent);
		String colName = column.getTitle();

		if (excelValidationData == null) {
			switch (column.getName()) {
				case "name":
					excelValidationData = new ExcelValidationData(colIndex, 2, 50, "错误提示", column.getTitle() + "填写错误，长度为2~50", "必填", column.getTitle() + "为必填内容");
					break;
				case "mac":
					excelValidationData = new ExcelValidationData(colIndex, 0, 18, "错误提示", "MAC不可以重复", "MAC字段", "请务必按照MAC格式格式填写，如：0C-9D-92-15-CB-49 或 0C:9D:92:15:CB:49");
					break;
				case "ip":
					excelValidationData = new ExcelValidationData(colIndex, 0, 16, "错误提示", "资产IP不能重复", "IP字段", "请务必按照ip格式填写");
					break;

				case "typeName":
				case "assetTypeName":
					excelValidationData = new ExcelValidationData(colIndex, assetTypeName, "必填", "资产类型为必填内容");
					break;
				case "assetTypeSnoName":
					excelValidationData = new ExcelValidationData(colIndex, assetSnoNames, "提示", "品牌型号");
					break;
				case "securityName":
				case "securityGuid":
					if (!domainNames.isEmpty()) {
						excelValidationData = new ExcelValidationData(colIndex, domainNames.toArray(new String[domainNames.size()]),
								"必填", "安全域为必填内容");
					}
					break;

				case "关联组织机构":
				case "org":
					//if (!orgNames.isEmpty()) {
					excelValidationData = new ExcelValidationData(colIndex, orgNames.toArray(new String[orgNames.size()]),
							"必填", "组织机构为必填内容");
					//}
					break;
				case "equipmentIntensive": // 涉密等级
					String[] eis = assetClassifiedLevel.getAllCodeValue(baseDictAlls); //改为字典获取
					if (column.getIsMust()) {
						excelValidationData = new ExcelValidationData(colIndex, eis, "必填", column.getTitle() + "为必填内容");
					} else {
						excelValidationData = new ExcelValidationData(colIndex, eis, "提示", "涉密等级");
					}
					break;
				case "isMonitorAgent": // 是否安装终端客户端 2021-08-23
					String[] isMonitorAgents = new String[]{"已安装", "未安装"};
					if (column.getIsMust()) {
						excelValidationData = new ExcelValidationData(colIndex, isMonitorAgents, "必填", column.getTitle() + "为必填内容");
					} else {
						excelValidationData = new ExcelValidationData(colIndex, isMonitorAgents, "提示", "是否安装终端客户端");
					}
					break;
				case "termType": // 是否国产 2021-09-01
					String[] isTermType = new String[]{"是", "否"};
					if (column.getIsMust()) {
						excelValidationData = new ExcelValidationData(colIndex, isTermType, "必填", column.getTitle() + "为必填内容");
					} else {
						excelValidationData = new ExcelValidationData(colIndex, isTermType, "提示", "是否国产");
					}
					break;
				case "terminalType": // 终端类型 ：2.运维终端 1. 用户终端  --针对终端类型 2021-09-01
					String[] terminalType = new String[]{"运维终端", "用户终端"};
					if (column.getIsMust()) {
						excelValidationData = new ExcelValidationData(colIndex, terminalType, "必填", column.getTitle() + "为必填内容");
					} else {
						excelValidationData = new ExcelValidationData(colIndex, terminalType, "提示", "终端类型");
					}
					break;
				case "osSetuptime": // 安装时间格式
					excelValidationData = new ExcelValidationData(index, 1, 50, null, null, "提示", "格式：yyyy-MM-dd HH:mm:ss");
					break;
				case "serialNumber": // 序列号
					if (column.getIsMust()) {
						excelValidationData = new ExcelValidationData(colIndex, textMinLenth, 64, errorTitle, errorMsg, promptTitle, promptContent);
					}
					break;
				case "secrecy": // 机密性
					String[] secrecy = new String[]{"基本无损害", "轻度损害", "中度损害", "严重损害", "致命损害"};
					excelValidationData = new ExcelValidationData(colIndex, secrecy, "提示", "机密性");
					break;
				case "availability": // 可用性
					String[] availability = new String[]{"基本无损害", "轻度损害", "中度损害", "严重损害", "致命损害"};
					excelValidationData = new ExcelValidationData(colIndex, availability, "提示", "可用性");
					break;
				case "importance": // 业务重要性
					String[] importance = new String[]{"基本无损害", "轻度损害", "中度损害", "严重损害", "致命损害"};
					excelValidationData = new ExcelValidationData(colIndex, importance, "提示", "业务重要性");
					break;
				case "loadBear": // 系统资产业务承载性
					String[] loadBear = new String[]{"基本无损害", "轻度损害", "中度损害", "严重损害", "致命损害"};
					excelValidationData = new ExcelValidationData(colIndex, loadBear, "提示", "系统资产业务承载性");
					break;
				case "integrity": // 完整性
					String[] integrity = new String[]{"基本无损害", "轻度损害", "中度损害", "严重损害", "致命损害"};
					excelValidationData = new ExcelValidationData(colIndex, integrity, "提示", "完整性");
					break;
				case "operationUrl": // 管理入口URL
				case "vid": //VID
				case "pid": //PID
					excelValidationData = new ExcelValidationData(index, 1, 100000, null, null, promptTitle, promptContent);
					break;
				default:
					if (column.getIsMust()) {
						excelValidationData = new ExcelValidationData(colIndex, textMinLenth, textMaxLenth, errorTitle, errorMsg, promptTitle, promptContent);
					}
					break;
			}
		}
		if (excelValidationData == null) {
			excelValidationData = getExcelValidationDataByType(column, colIndex);
		}

		if (excelValidationData == null) {
			excelValidationData = new ExcelValidationData(colIndex, textMinLenth, textMaxLenth, errorTitle, errorMsg, promptTitle, promptContent);
		}

		return excelValidationData;
	}

	private ExcelValidationData getExcelValidationDataByType(CustomSettings column, int colIndex) {
		ExcelValidationData excelValidationData = null;
		switch (column.getType()) {
			case "Switch":
				excelValidationData = new ExcelValidationData(colIndex, new String[]{"是", "否"}, column.getTitle(),
						column.getTitle() + "的值只能为是或否");
				break;
			case "Select":

				List<String> names = new ArrayList<>();
				List<NameValue> option = column.getOption();
				if (option != null && !option.isEmpty()) {
					option.forEach(a -> {
						names.add(a.getName());
					});
				}
				excelValidationData = new ExcelValidationData(colIndex, names.toArray(new String[names.size()]),
						column.getDescriptionTitle(), column.getDescription());
				break;
			default:
				break;
		}
		return excelValidationData;
	}





	@Override
	public void exportAssetFile(String _fileName, HttpServletResponse response) {
		String realPath = fileConfiguration.getFilePath(); // 文件路径
		// String fileName = fileConfiguration.getFileName();// 文件名称
		FileUtil.downLoadFile(_fileName + ".xls", realPath, response);
	}


	/**
	 * 数据重构：构造资产信息
	 *
	 * @param map
	 * @param guid
	 */
	private Asset getAssetInfo(Map<String, Object> map, String guid) {
		Asset asset = JSONObject.parseObject(gson.toJson(map),Asset.class);
		Object object = map.get("ip");
		String ip = null;
		if (object != null) {
			ip = object.toString();
		}
		// 将责任人code同时赋给employeeCode1
		if (map.containsKey("responsibleCode") && map.get("responsibleCode") != null) {
			asset.setEmployeeCode1(map.get("responsibleCode").toString());
		}
		// 将组织code同时赋给org
		if (map.containsKey("orgCode") && map.get("orgCode") != null) {
			asset.setOrg(map.get("orgCode").toString());
		}
		asset.setGuid(guid);
		asset.setProtocol("");
		asset.setCanMonitor("off");
		// 资产价值计算处理 2023-1-5
		assetWoorthValue(map,asset);
		asset.setCreateTime(new Date());
		if (ip != null) {
			asset.setIpNum(AssetUtil.ip2int(ip));
		}
		asset.setDataSourceType(1); // 数据来源类型为手动输入  2022-06-16
		return asset;

	}

	/**
	 * 资产价值计算处理 2023-1-5
	 * @param map
	 * @param asset
	 */
	private void assetWoorthValue(Map<String, Object> map,Asset asset) {
		List<Integer> values = new ArrayList<>();
		// 机密性
		String secrecy =map.get("secrecy")==null?"0": String.valueOf(map.get("secrecy"));
		asset.setSecrecy(secrecy);
		values.add(Integer.valueOf(secrecy));
		// 可用性
		String availability =map.get("availability")==null?"0": String.valueOf(map.get("availability"));
		asset.setAvailability(availability);
		values.add(Integer.valueOf(availability));
		// 业务重要性
		String importance =map.get("importance")==null?"0": String.valueOf(map.get("importance"));
		asset.setImportance(importance);
		values.add(Integer.valueOf(importance));
		// 系统资产业务承载性
		String loadBear =map.get("loadBear")==null?"0": String.valueOf(map.get("loadBear"));
		asset.setLoadBear(loadBear);
		values.add(Integer.valueOf(importance));
		// 完整性
		String integrity =map.get("integrity")==null?"0": String.valueOf(map.get("integrity"));
		asset.setIntegrity(integrity+"");
		values.add(Integer.valueOf(integrity));
		Integer max = values.stream().max(Integer :: compare).get();
		logger.info("资产价值："+ max);
		asset.setWorth(String.valueOf(max));
	}

	/**
	 * 保存导入的方法
	 *
	 * @param list list
	 * @return
	 */
	private void saveImportAssetInfo(List<Map<String, Object>> list) {
		List<AssetExtend> assetExtends = new ArrayList<AssetExtend>();
		List<Asset> assets = new ArrayList<Asset>();
		List<AssetRedisCacheVO> assetCaches = new ArrayList<>();
		List<String> typeGuids = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Boolean result = (Boolean) map.get("state");
			if (result != null && result) {
				String guid = UUIDUtils.get32UUID();
				// 数据重构：资产信息 2022-6-27
				Asset asset = getAssetInfo(map, guid);
				// 扩展字段处理 2021-08-27
				AssetExtend assetExtend = getExtendAssetHandle(map, guid);
				assetExtends.add(assetExtend);
				assets.add(asset);
				AssetRedisCacheVO assetCache= mapper.map(asset,AssetRedisCacheVO.class);
				assetCaches.add(assetCache);
				if(!typeGuids.contains(asset.getAssetType())){
					typeGuids.add(asset.getAssetType());
				}
			}
		}
		// 批量数据入库处理 --20210729
		batchSaveData(assetExtends, assets);
		// 资产redis缓存  2022-08-08
		assetRedisCache(assetCaches,typeGuids);

	}

	private void assetRedisCache(List<AssetRedisCacheVO> assetCaches,List<String> typeGuids) {
		String sql="select atype.name as typeName,agroup.name as groupName ,atype.Guid as typeGuid  from asset_type as atype " +
				"left join asset_type_group as agroup on atype.TreeCode LIKE CONCAT(agroup.`TreeCode`,'-%') " +
				" where atype.Guid in ('" + org.apache.commons.lang3.StringUtils.join(typeGuids, "','") + "')";
		List<AssetCacheExtendVO> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetCacheExtendVO>(AssetCacheExtendVO.class));
        for(AssetRedisCacheVO cacheVO : assetCaches){
			String typeGuid = cacheVO.getAssetType();
			addExtendData(cacheVO,typeGuid,list);

		}
		baseDataRedisCacheService.addAssets(assetCaches);
	}

	private void addExtendData(AssetRedisCacheVO cacheVO, String typeGuid,List<AssetCacheExtendVO> list) {
		AssetCacheExtendVO vo = getAssetCacheExtendVOByGuid(typeGuid,list);
		if(null == vo){
			return;
		}
		cacheVO.setTypeName(vo.getTypeName());
		cacheVO.setGroupName(vo.getGroupName());
	}

	private AssetCacheExtendVO getAssetCacheExtendVOByGuid(String typeGuid, List<AssetCacheExtendVO> list) {
		if(StringUtils.isEmpty(typeGuid)){
			return null;
		}
		for(AssetCacheExtendVO vo : list){
			if(typeGuid.equals(vo.getTypeGuid())){
				return vo;
			}
		}
		return null;
	}


	// 获取扩展字段的值 2021-08-27
	private AssetExtend getExtendAssetHandle(Map<String, Object> map, String guid) {
		// 获取扩展字段
		AssetExtend assetExtend = new AssetExtend();
		assetExtend.setAssetGuid(guid);
		assetExtend.setExtendInfos("{}");
		if (null == map.get("customsColumns")) {
			return assetExtend;
		}
		if (map.get("customsColumns") instanceof List) { // 导入解析时存放的自定义列
			List<String> customsColumns = (List<String>) map.get("customsColumns");
			Map<String, Object> extendParams = new HashMap<>();
			for (String colname : customsColumns) {
				extendParams.put(colname, map.get(colname));
			}
			assetExtend.setExtendInfos(JsonMapper.toJsonString(extendParams));
			return assetExtend;
		}
		return assetExtend;
	}

	// 批量保存数据 20210729
	private void batchSaveData(List<AssetExtend> assetExtends, List<Asset> assets) {
		assetsBatchSave(assets);
		assetExtendsBatchSave(assetExtends);
		// 保存新增设备，针对终端类型的，记录操作系统安装时间到asset_terminal_install_time表中 2021-08-24
		terminalAssteInstallTimeAddQue();

	}

	private void terminalAssteInstallTimeAddQue() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					TerminalAssteInstallTimeJobVO terminalAssteInstallTimeJobVO = new TerminalAssteInstallTimeJobVO();
					terminalAssteInstallTimeJobVO.setType("4");
					terminalAssteInstallTimeService.excTerminalAssteInstallTime(terminalAssteInstallTimeJobVO);
				}catch(Exception e){
					logger.error("操作系统安装时间放入队列异常",e);
				}
			}
		}).start();

	}

	private void assetsBatchSave(List<Asset> assets) {
		if (null == assets || assets.size() <= 0) {
			return;
		}
		// 500保存一次
		if (assets.size() <= batchNum) {
			setEmployeeCode1(assets);
			assetService.save(assets);
		} else {
			List<Asset> saveDatas = assets.subList(0, batchNum);
			// 责任人赋值
			setEmployeeCode1(saveDatas);
			assetService.save(saveDatas);
			assets.removeAll(saveDatas);
			assetsBatchSave(assets);
		}
	}

	private void setEmployeeCode1(List<Asset> saveDatas) {
		for (Asset asset : saveDatas) {
			asset.setEmployeeCode1(asset.getResponsibleCode());
		}
	}

	private void assetExtendsBatchSave(List<AssetExtend> assetExtends) {
		if (null == assetExtends || assetExtends.size() <= 0) {
			return;
		}
		// 500保存一次
		if (assetExtends.size() <= batchNum) {
			assetExtendService.save(assetExtends);
		} else {
			List<AssetExtend> saveDatas = assetExtends.subList(0, batchNum);
			assetExtendService.save(saveDatas);
			assetExtends.removeAll(saveDatas);
			assetExtendsBatchSave(assetExtends);
		}
	}

	@Override
	public Result<Boolean> importAssetFile(List<Map<String, Object>> list) {
		try {
			if (null == list || list.size() <= 0) {
				return ResultUtil.success(true);
			}
			if (list.size() > importNum) {
				logger.info("asynch handle start");
				com.vrv.vap.common.model.User user = SessionUtil.getCurrentUser();
				asynchSaveImportAssetInfo(list, user);
				Result<Boolean> result = new Result<Boolean>();
				result.setCode(ResultCodeEnum.SUCCESS.getCode());
				result.setData(true);
				result.setMsg("资产正在导入过程当中，请稍后查看！");
				return result;
			} else {
				logger.info("current handle start");
				saveImportAssetInfo(list);
				return ResultUtil.success(true);
			}

		} catch (Exception e) {
			logger.error("导入文件数据保存异常", e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

	}

	/**
	 * 异步执行保存(20210730)
	 *
	 * @param list list
	 */
	private void asynchSaveImportAssetInfo(List<Map<String, Object>> list, com.vrv.vap.common.model.User user) {
		ExecutorServiceVrvUtil.getThreadPool().submit(new Runnable() {
			@Override
			public void run() {
				AssetImportLog log = new AssetImportLog();
				Integer result = 0;
				String errorMsg = "";
				try {
					int count = list.size();
					logger.info("asynchSaveImportAssetInfo start,size={}", count);
					String logGuid = UUIDUtils.get32UUID();
					log.setCount(count);
					log.setGuid(logGuid);
					String userName = "";
					if (null != user) {
						userName = user.getName();
						log.setUserId(user.getId() + "");
						log.setUserName(userName);
					} else {
						errorMsg = "获取当前登录用户为空";
					}
					log.setStartTime(new Date());
					log.setResult(2);
					assetImportLogService.save(log);
					saveImportAssetInfo(list);
					logger.info("asynchSaveImportAssetInfo success");
				} catch (Exception e) {
					result = 1;
					errorMsg = e.toString();
					logger.error("asynchSaveImportAssetInfo fail,{}", e);
				} finally {
					log.setResult(result);
					log.setErrorMessage(errorMsg);
					log.setEndTime(new Date());
					assetImportLogService.save(log);
					logger.info("assetImportLogService save");
				}
			}
		});
	}

}
