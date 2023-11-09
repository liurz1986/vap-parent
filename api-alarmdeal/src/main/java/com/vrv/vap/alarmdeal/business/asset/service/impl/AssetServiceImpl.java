package com.vrv.vap.alarmdeal.business.asset.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementService;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.dao.BaseKoalOrgDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyExportVO;
import com.vrv.vap.alarmdeal.business.asset.enums.AssetTrypeGroupEnum;
import com.vrv.vap.alarmdeal.business.asset.enums.OperationTypeEnum;
import com.vrv.vap.alarmdeal.business.asset.model.*;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetIpByAssetGroupVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetSearchNewVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetTypeByIpVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetWorthVO;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetRepository;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.util.AssetDomainCodeUtil;
import com.vrv.vap.alarmdeal.business.asset.util.AssetRedisUtil;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.*;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.*;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



@Service
@Transactional
public class AssetServiceImpl extends BaseServiceImpl<Asset, String> implements AssetService {
	private static Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

	@Autowired
	private AssetRepository assetRepository;
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	@Autowired
	private AssetTypeGroupService assetTypeGroupService;
	@Autowired
	private AssetTypeService assetTypeService;
	@Autowired
	private AssetExtendService assetExtendService;
	@Autowired
	private AssetDao assetDao;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private MapperUtil mapper;
	@Autowired
	private AssetOperationLogService assetOperationLogService;
	@Autowired
	AdminFeign adminFeign;
	@Autowired
	private BaseKoalOrgDao baseKoalOrgDao;
    @Autowired
	AssetRedisUtil assetRedisUtil;
	@Autowired
	private AppSysManagerService appSysManagerService;
	@Autowired
	AssetSettingsService assetSettingsService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AssetAlarmService assetAlarmService;
	@Autowired
	private BaseDataRedisCacheService baseDataRedisCacheService;
	@Autowired
	private AssetBaseDataService assetBaseDataService;
	@Autowired
	private TerminalAssetInstallService terminalAssetInstallService;
	@Autowired
	private TerminalAssteInstallTimeService terminalAssteInstallTimeService;
	@Autowired
	private AlarmEventManagementService alarmEventManagementService;
	@Autowired
	private AlarmEventManagementForESService alarmEventManagementForESService;
	@Autowired
	private SelfConcernAssetService selfConcernAssetService;

	@Override
	public AssetRepository getRepository() {
		return assetRepository;
	}

	@Override
	public List<AssetCacheVo> queryAllAsset() {
		String sql = "select guid,ip,mac,equipment_intensive as equipmentIntensive,name,org_name as orgName,org_code as orgCode,Type_Guid as assetType,responsible_code as responsibleCode,securityGuid from asset where ip is not null or ip != ''";
        List<AssetCacheVo> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetCacheVo>(AssetCacheVo.class));
		return assets;
	}

	@Override
	public List<AssetCacheVo> queryAssetCacheVoByIp(String ip) {
		String sql = "select guid,ip,mac,equipment_intensive as equipmentIntensive,name,org_name as orgName,org_code as orgCode,Type_Guid as assetType,responsible_code as responsibleCode,securityGuid from asset where  ip = '"+ip+"'";
		List<AssetCacheVo> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetCacheVo>(AssetCacheVo.class));
		return assets;
	}

	@Override
	public Asset queryAssetByIp(String ip) {
		List<QueryCondition> param = new ArrayList<>();
		param.add(QueryCondition.eq("ip",ip));
		List<Asset> assets = findAll(param);
		if(CollectionUtils.isNotEmpty(assets)){
			return assets.get(0);
		}
		return null;
	}

	@Override
	public PageRes<AssetVO> getAssetInfoPager(AssetSearchVO assetSearchVO, Pageable pageable) {
		PageRes<AssetVO> pageRes = new PageRes<>();
		List<QueryCondition> conditions = assetInfoPagerSearchCondtion(assetSearchVO);
		// 获得资产列表
		Page<Asset> pager = findAll(conditions, pageable);
		List<Asset> content = pager.getContent();
		List<AssetVO> list = new ArrayList<>();
		List<BaseSecurityDomain> allDomains = assetBaseDataService.queryAllDomain();
		getAssetVOList(content, list,allDomains);
		long totalElements = pager.getTotalElements();
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setList(list);
		pageRes.setTotal(totalElements);
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		return pageRes;
	}

	@Autowired
	private FileConfiguration fileConfiguration;

	@Override
	public Result<String> exportNewAssetInfo(AssetSearchVO assetSearchVO) {
		String fileName = "画像信息" + com.vrv.vap.exportAndImport.excel.util.DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
		String rootPath = fileConfiguration.getAsset();
		File targetFile = new File(rootPath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		String filePath = Paths.get(rootPath, fileName).toString();

		List<QueryCondition> conditions = assetInfoPagerSearchCondtion(assetSearchVO);
		// 获得资产列表
		List<Asset> content = findAll(conditions);
		List<AssetVO> list = new ArrayList<>();
		List<BaseSecurityDomain> allDomains = assetBaseDataService.queryAllDomain();
		getAssetVOList(content, list,allDomains);
		//窃泄密值
		assetStealLeakValueService.setAssetValue(list);
		//事件数
		alarmEventManagementService.setEventNumber(list);
        List<AssetExportVO> assetExportVOS=new ArrayList<>();
        for (AssetVO assetVO:list){
			AssetExportVO assetExportVO=new AssetExportVO();
			BeanUtil.copyProperties(assetVO,assetExportVO);
			if(assetVO!=null&&assetVO.getEquipmentIntensive()!=null){
				switch (assetVO.getEquipmentIntensive()) {
					case "4":
						assetExportVO.setEquipmentIntensiveName("非密");
						break;
					case "3":
						assetExportVO.setEquipmentIntensiveName("内部");
						break;
					case "2":
						assetExportVO.setEquipmentIntensiveName("秘密");
						break;
					case "1":
						assetExportVO.setEquipmentIntensiveName("机密");
						break;
					case "0":
						assetExportVO.setEquipmentIntensiveName("绝密");
						break;
					default:
						break;

				}
			}
			assetExportVOS.add(assetExportVO);
		}
		try {
			ExportExcelUtils.getInstance().createExcel(assetExportVOS, AssetExportVO.class, filePath);
			return ResultUtil.success(fileName);
		} catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
			logger.error("导出excel异常", e);
		}
		return ResultUtil.error(-1,"导出excel异常");
	}


	@Override
	public PageRes<AssetVO> getAssetImagePager(AssetSearchVO assetSearchVO, Pageable pageable) {
		PageRes<AssetVO> pageRes = new PageRes<>();
		List<QueryCondition> conditions = assetInfoPagerSearchCondtion(assetSearchVO);
		// 获得资产列表
		Page<Asset> pager = findAll(conditions, pageable);
		List<Asset> content = pager.getContent();
		List<AssetVO> list = new ArrayList<>();
		List<BaseSecurityDomain> allDomains = assetBaseDataService.queryAllDomain();
		getAssetVOList(content, list,allDomains);
		//窃泄密值
		assetStealLeakValueService.setAssetValue(list);
		//事件数
		alarmEventManagementService.setEventNumber(list);
		//是否为关注资产
		cheakAssetOfConcern(list);
		long totalElements = pager.getTotalElements();
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setList(list);
		pageRes.setTotal(totalElements);
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		return pageRes;
	}

	private void cheakAssetOfConcern(List<AssetVO> list) {
		User currentUser = SessionUtil.getCurrentUser();
		if (currentUser!=null){
			if (list.size()>0){
				for (AssetVO assetVO:list){
					List<QueryCondition> conditions = new ArrayList<>();
					conditions.add(QueryCondition.eq("userId", currentUser.getId()));
					conditions.add(QueryCondition.eq("ip",assetVO.getIp()));
					conditions.add(QueryCondition.eq("type",0));
					List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(conditions);
					if (findAll.size()>0){
						assetVO.setIsJustAssetOfConcern(true);
					}
				}
			}
		}

	}

	private List<QueryCondition> assetInfoPagerSearchCondtion(AssetSearchVO assetSearchVO) {
		List<QueryCondition> conditions = searchAssetCondition(assetSearchVO);
		securityGuidCondition(conditions);
		//usb外设或usb存储介质处理 2021-09-16
		usbCondition(conditions,assetSearchVO);
		return conditions;
	}

	private void securityGuidCondition(List<QueryCondition> conditions) {
		if(SessionUtil.getCurrentUser()!=null&& SessionUtil.getauthorityType()) {
			List<String> userDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
			if(userDomainCodes==null||userDomainCodes.isEmpty()) {
				conditions.add(QueryCondition.eq("securityGuid", "!@#$%^&*"));//使查找不到数据
			}else {
				conditions.add(QueryCondition.in("securityGuid", userDomainCodes));
			}
		}
	}

	// 是usb外设或usb存储介质不处理；不是的话资产管理里面的查询排查usb外设或usb存储介质
	private void usbCondition(List<QueryCondition> conditions, AssetSearchVO assetSearchVO) {
		String hasUsbAsset = assetSearchVO.getHasUsbAsset();
		if(!(StringUtils.isNotEmpty(hasUsbAsset)&&"1".equalsIgnoreCase(hasUsbAsset))){
			List<QueryCondition> condition = new ArrayList<>();
			condition.add(QueryCondition.or(QueryCondition.likeBegin("treeCode","asset-USBMemory-"),QueryCondition.likeBegin("treeCode","asset-USBPeripheral-")));
			List<AssetType> assetTypes = assetTypeService.findAll(condition);

			if(null != assetTypes && assetTypes.size() > 0 ){
				List<String> guids =new  ArrayList<>();
				for(AssetType type : assetTypes){
					String guid = type.getGuid();
					guids.add(type.getGuid());
				}
				conditions.add(QueryCondition.not(QueryCondition.in("assetType", guids)));
			}
		}
	}

	@Override
	public  PageRes<AssetVO> getAssetInfoPager(String treeCode, Pageable pageable){
		List<QueryCondition> conditions =new ArrayList<>();

		conditions.add(QueryCondition.likeBegin("treeCode", treeCode));
		List<AssetType> assetTypes = assetTypeService.findAll(conditions);

		List<String> typeGuids=new ArrayList<>();
		for(AssetType type : assetTypes) {
			typeGuids.add(type.getGuid());
		}
		conditions.clear();

		conditions.add(QueryCondition.in("assetType", typeGuids));

		Page<Asset> findAll = this.findAll(conditions,pageable);

		List<AssetVO> mapList = mapper.mapList(findAll.getContent(), AssetVO.class);
		PageRes<AssetVO> pageRes = new PageRes<>();
		long totalElements = findAll.getTotalElements();
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setList(mapList);
		pageRes.setTotal(totalElements);
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		return pageRes;
	}


	/**
	 * assetList转assetVOList
	 * @param content
	 * @param list
	 */
	private void getAssetVOList(List<Asset> content, List<AssetVO> list,List<BaseSecurityDomain> allDomains) {
		for (Asset asset : content) {
			AssetVO assetVO = mapperVO(asset,allDomains);
			AssetExtend assetExtend = assetExtendService.getOne(asset.getGuid());
			if(assetExtend!=null&&!StringUtils.isEmpty(assetExtend.getExtendInfos())) {
				assetVO.setAssetExtendInfo(assetExtend.getExtendInfos());
			}
			list.add(assetVO);
		}
	}

	/**
	 * 查询资产条件
	 *
	 * @param assetSearchVO
	 * @return
	 */
	@Override
	public List<QueryCondition> searchAssetCondition(AssetSearchVO assetSearchVO) {
		List<QueryCondition> conditions = new ArrayList<>();
		String name = assetSearchVO.getName(); // 资产名称
		String ip = assetSearchVO.getIp(); // 资产IP
		String ipEq = assetSearchVO.getIpEq(); // 资产IP
		String assetType = assetSearchVO.getAssetType(); // 资产类型
		String typeGuid = assetSearchVO.getTypeGuid();
		String guid = assetSearchVO.getGuid();
		String securityGuid = assetSearchVO.getSecurityGuid();
		String domainSearchType = assetSearchVO.getDomainSearchType();
		String assetGuids=assetSearchVO.getAssetGuids();
		String assetGuidNodes=assetSearchVO.getAssetGuidNodes();
		String assetTypeCode=assetSearchVO.getAssetTypeCode();
		String orgName = assetSearchVO.getOrgName();
		String equipmentIntensive = assetSearchVO.getEquipmentIntensive();
		String beginValue = assetSearchVO.getBeginValue();
		String endValue = assetSearchVO.getEndValue();
		String assetInfo = assetSearchVO.getAssetInfo();
		String orgCode = assetSearchVO.getOrgCode();
		Boolean isJustAssetOfConcern = assetSearchVO.getIsJustAssetOfConcern();
		if (Boolean.TRUE.equals(isJustAssetOfConcern)) {
			List<String> ipsOfConcern = alarmEventManagementForESService.getIpsOfConcern();
			conditions.add(QueryCondition.in("ip", ipsOfConcern));
		}
		if (StringUtils.isNotBlank(assetInfo)){
			conditions.add(QueryCondition.or( QueryCondition.like("name", assetInfo),QueryCondition.like("ip", assetInfo),QueryCondition.like("mac", assetInfo)));
		}
		if (StringUtils.isNotBlank(beginValue)||StringUtils.isNotBlank(endValue)){
			conditions.addAll(stealLeakValue(beginValue,endValue));
		}
		// 资产guid
		if (StringUtils.isNotEmpty(guid)) {
			conditions.add(QueryCondition.eq("guid", guid));
		}
		// 部门
		if (StringUtils.isNotEmpty(orgName)) {
			conditions.add(QueryCondition.like("orgName", orgName));
		}
		// 部门
		if (StringUtils.isNotEmpty(orgCode)) {
			conditions.add(QueryCondition.eq("orgCode", orgCode));
		}
		// 密级
		if (StringUtils.isNotEmpty(equipmentIntensive)) {
			conditions.add(QueryCondition.eq("equipmentIntensive", equipmentIntensive));
		}
		// 资产名称
		if (StringUtils.isNotEmpty(name)) {
			conditions.add(QueryCondition.like("name", name));
		}
		// 资产IP
		if (StringUtils.isNotEmpty(ip)) {
			conditions.add(QueryCondition.like("ip", ip));
		}
		if (StringUtils.isNotEmpty(ipEq)) {
			conditions.add(QueryCondition.eq("ip", ipEq));
		}
		if (StringUtils.isNotEmpty(assetType)) {
			conditions.add(QueryCondition.like("assetType", assetType));
		}
		if (StringUtils.isNotEmpty(typeGuid)) {
			conditions.addAll(searchAssetType(typeGuid));
		}
		//资产类型code查询
		if (StringUtils.isNotEmpty(assetTypeCode)) {
			conditions.addAll(searchAssetTypeCode(assetTypeCode));
		}
		if (StringUtils.isNotEmpty(assetGuids)) {
			conditions.add(QueryCondition.in("guid", assetGuids.split(",")));
		}
		if (StringUtils.isNotEmpty(assetGuidNodes)) {
			conditions.add(QueryCondition.not(QueryCondition.in("guid", assetGuidNodes.split(","))));
		}
        // 序列号查询
		if (StringUtils.isNotEmpty(assetSearchVO.getSerialNumber())) {
			conditions.add(QueryCondition.like("serialNumber", assetSearchVO.getSerialNumber().trim()));
		}
        // 责任人姓名
		if(StringUtils.isNotEmpty(assetSearchVO.getResponsibleName())){
			conditions.add(QueryCondition.like("responsibleName", assetSearchVO.getResponsibleName().trim()));
		}
		// 查询安全域
		//安全域查询方式：1、查询自身 2、查询自身和子级 3、查询自身和子集的子集
		if(!StringUtils.isEmpty(domainSearchType)&&!"1".equals(domainSearchType)) {
			List<String> domainCodes=new ArrayList<>();
			List<BaseSecurityDomain> allDomain = assetBaseDataService.queryAllDomain();
			if(allDomain!=null&&!allDomain.isEmpty()) {
				List<BaseSecurityDomain> data = allDomain;

				for(BaseSecurityDomain domain : data) {
					if (StringUtils.isNotEmpty(securityGuid)) {
						if("2".equals(domainSearchType)) {
							if(securityGuid.equals(domain.getCode())||securityGuid.equals(domain.getParentCode())) {
								domainCodes.add(domain.getCode());
							}
						}else if("3".equals(domainSearchType)) {
							if(domainCodes.contains(domain.getParentCode())|| securityGuid.equals(domain.getCode())||securityGuid.equals(domain.getParentCode())) {
								domainCodes.add(domain.getCode());
							}
						}
					}
				}
			}
			if (StringUtils.isNotEmpty(securityGuid)) {
				conditions.add(QueryCondition.in("securityGuid", domainCodes));
			}
		}else {
			if (StringUtils.isNotEmpty(securityGuid)) {
				if("isNull".equals(securityGuid)) {
					conditions.add(QueryCondition.or(QueryCondition.eq("securityGuid", ""),QueryCondition.isNull("securityGuid")));
				}else {
					conditions.add(QueryCondition.eq("securityGuid", securityGuid));
				}
			}
		}
		String domainCodeTree = assetSearchVO.getDomainCodeTree();
		if (StringUtils.isNotEmpty(domainCodeTree)) {
			List<BaseSecurityDomain> allDomain =  assetBaseDataService.queryAllDomain();
			if (allDomain != null && !allDomain.isEmpty()) {
				List<BaseSecurityDomain> data = allDomain;
				List<String> domainCodes = getCodesByDomainCodeTree(domainCodeTree,data);
				if(domainCodes.isEmpty()) {
					//使无法查询得到数据
					conditions.add(QueryCondition.eq("securityGuid", "qwe&(*&*(iou3498273@$$%#%#984"));
				}else {
					conditions.add(QueryCondition.in("securityGuid", domainCodes));
				}
			}
		}
		return conditions;
	}
    @Autowired
	private AssetStealLeakValueService assetStealLeakValueService;

	private List<QueryCondition> stealLeakValue(String beginValue,String endValue) {
		List <QueryCondition> conditions=new ArrayList<>();
		List <QueryCondition> conditionSL=new ArrayList<>();
		if (StringUtils.isNotBlank(beginValue)){
			conditionSL.add(QueryCondition.ge("stealLeakValue",beginValue));
		}
		if (StringUtils.isNotBlank(endValue)){
			conditionSL.add(QueryCondition.le("stealLeakValue",endValue));
		}
		List<AssetStealLeakValue> all = assetStealLeakValueService.findAll(conditionSL);
		if (all.size()>0){
			List<String> collect = all.stream().map(m -> m.getIp()).collect(Collectors.toList());
			if (collect.size()>0){
				conditions.add(QueryCondition.in("ip",collect));
			}
		}else {
			conditions.add(QueryCondition.isNull("ip"));
		}
		return conditions;
	}

	/***
	 *
	 * @param domainCodeTree  不考虑异常传参情况
	 * @param allDomains
	 * @return
	 */
	private List<String>  getCodesByDomainCodeTree(String domainCodeTree,List<BaseSecurityDomain> allDomains) {
//		后端接收参数暂定：
//		domainCodeTree
//		格式：/code/code/code
//		序号	格式	说明
//		1	/+/+/+	表示查询所有数据
//		2	/*/*/*	
//		3	/*	
//		4	/+	
//		5	/code/code/*	查询2级下的所有节点数据
//		6	/code/*	查询一级下的所有节点数据
//		7	/code/code/+	查询2级+2级下所有节点数据
//		8	/code/+	查询一级+一级下的所有节点数据
//		9	/code/code/code	查询某个三级数据
//		10	/code/code	查询某个二级数据
//		11	/code	查询某个一级数据
//
//		复杂传参说明
//		序号	格式	说明
//		1	/code/code/code1,code2	逗号分隔，表示某一个节点下的多个下级节点（暂不支持）
//		2	/code/code1,code2/code	不支持
//		3	/code/code1/*;/code/code2/*	分号分隔（暂不支持）
//		4	/code/code1/code,code;/code/code2/*	分号分隔和逗号分隔同时存在（暂不支持）
		List<String> domainCodes=new ArrayList<>();
		if(domainCodeTree.contains(";")) {
			String[] domainCodeTrees = domainCodeTree.split(";");
			for(String tree : domainCodeTrees) {
				domainCodes.addAll(getCodesByDomainCodeTree(tree,allDomains));
			}
		}else {
			if(!domainCodeTree.contains("/")) {
				domainCodes.add(domainCodeTree);
			}else {
				//查询自身的下级
				//  /code/code/*   code/code/*
				if(domainCodeTree.endsWith("/*")) {
					String parentCode=domainCodeTree.split("/")[domainCodeTree.split("/").length-2];
					if(StringUtils.isNotEmpty(parentCode)) {
						for(BaseSecurityDomain domain : allDomains) {
							if(StringUtils.isEmpty(domain.getParentCode())) {
								continue;
							}
							if(parentCode.equals(domain.getParentCode())) {
								domainCodes.add(domain.getCode());
								//查询子集的下级
								domainCodes.addAll(getCodesByDomainCodeTree("/"+domain.getCode()+"/*",allDomains));
							}
						}
					}

				}
				//查询自身的下级的+自身
				//  /code/code/+   code/code/+
				else if (domainCodeTree.endsWith("/+")) {
					String parentCode = domainCodeTree.split("/")[domainCodeTree.split("/").length - 2];
					if (StringUtils.isNotEmpty(parentCode)) {
						domainCodes.add(parentCode);
						String newDomainCodeTree=domainCodeTree.replace("/+", "/*");
						domainCodes.addAll(getCodesByDomainCodeTree(newDomainCodeTree,allDomains));
					}
				}
				//异常传参(查询自身)
				//  code/   /code/
				else if(domainCodeTree.endsWith("/")) {
					domainCodes.add(domainCodeTree.split("/")[domainCodeTree.split("/").length-1]);
					logger.debug(domainCodeTree);
				}
				//查询自身
				//   /code   /code/code
				else {
					domainCodes.add(domainCodeTree.split("/")[domainCodeTree.split("/").length-1]);
				}
			}
		}

		return domainCodes;
	}

	public int getAssetCountNotWithAuth(String typeGuid) {

		List<QueryCondition> querys=searchAssetType(typeGuid);

		Long count = this.count(querys);
		return count.intValue();

	}

	/**
	 * 资产类型条件查询
	 * @param typeGuid
	 */
	private List<QueryCondition> searchAssetType(String typeGuid) {
		List<QueryCondition> conditions = new ArrayList<>();
		List<String> guids = new ArrayList<>();
		if (StringUtils.isNotEmpty(typeGuid)&&!typeGuid.equals("0")) {
			AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(typeGuid);
			if (assetTypeGroup != null) {
				String treeCode = assetTypeGroup.getTreeCode();
				List<QueryCondition> con = new ArrayList<>();
				con.add(QueryCondition.likeBegin("treeCode", treeCode + "-"));
				List<AssetType> find = assetTypeService.findAll(con);
				if (find != null && !find.isEmpty()) {
					for (AssetType assetType1 : find) {
						guids.add(assetType1.getGuid());
					}
				}
				if (!guids.isEmpty()) {
					conditions.add(QueryCondition.in("assetType", guids));
				}else{
					conditions.add(QueryCondition.isNull("assetType"));
				}
			} else {
				if (StringUtils.isNoneEmpty(typeGuid)) {
					//conditions.add(QueryCondition.eq("assetType", typeGuid));
					conditions.add(QueryCondition.or(QueryCondition.eq("assetType", typeGuid),QueryCondition.eq("assetTypeSnoGuid", typeGuid)) );
				}
			}
		}
		return conditions;
	}

	/**
	 * 资产类型code条件查询
	 * @param assetTypeCode
	 */
	private List<QueryCondition> searchAssetTypeCode(String assetTypeCode) {
		List<QueryCondition> conditions = new ArrayList<>();
		List<String> guids = new ArrayList<>();
		if (StringUtils.isNotEmpty(assetTypeCode)) {
			String assetType = getAssetType(assetTypeCode);
			if (StringUtils.isNotBlank(assetType)) {
				List<QueryCondition> con = new ArrayList<>();
				con.add(QueryCondition.likeBegin("treeCode", assetType + "-"));
				List<AssetType> find = assetTypeService.findAll(con);
				if (find != null && !find.isEmpty()) {
					for (AssetType assetType1 : find) {
						guids.add(assetType1.getGuid());
					}
				}
				if (!guids.isEmpty()) {
					conditions.add(QueryCondition.in("assetType", guids));
				}else{
					conditions.add(QueryCondition.isNull("assetType"));
				}
			}
		}
		return conditions;
	}

	public String getAssetType(String type){
		String treeCode = "";
		switch(type){
			case "assetHost":
				treeCode = AssetTrypeGroupEnum.ASSETHOSt.getTreeCode();
				break;
			case "assetService" :
				treeCode = AssetTrypeGroupEnum.ASSETSERVICE.getTreeCode();
				break;
			case "assetNetworkDevice" :
				treeCode = AssetTrypeGroupEnum.ASSETNET.getTreeCode();
				break;
			case "assetSafeDevice" :
				treeCode = AssetTrypeGroupEnum.ASSETSAFE.getTreeCode();
				break;
			case "assetMaintenHost" :
				treeCode = AssetTrypeGroupEnum.ASSETMAINTEN.getTreeCode();
				break;
			default:
				break;
		}
		return treeCode;
	}

	private List<QueryCondition> searchTag(String tags) {
		// 资产标签，支持多个标签查询
		// 资产标签 ，支持多个标签进行查询
		List<QueryCondition> conditions = new ArrayList<>();
		if (tags != null && !"".equals(tags.trim())) {
			QueryCondition[] tagsConditions = null;
			// 多个标签
			if (tags.indexOf(",") > 0) {
				String[] tempTags = tags.split("\\,");
				if (tempTags != null && tempTags.length >= 2) {
					QueryCondition conditionOne = null;
					QueryCondition conditionTwo = null;
					/**
					 * 当标签参数 > 2时，需要去 or多个标签
					 */
					if (tempTags.length >= 2) {
						tagsConditions = new QueryCondition[tempTags.length - 2];
						for (int i = 0, j = 0; i < tempTags.length; i++) {
							if (i == 0) {
								conditionOne = QueryCondition.like("tags", tempTags[0]);
							} else if (i == 1) {
								conditionTwo = QueryCondition.like("tags", tempTags[1]);
							} else {
								tagsConditions[j] = QueryCondition.like("tags", tempTags[i]);
								j++;
							}
						}
						if(conditionOne != null){
							conditions.add(conditionOne);
						}else if(conditionTwo != null){
							conditions.add(conditionTwo);
						}else if(tagsConditions != null){
							conditions.addAll(new ArrayList<QueryCondition>(Arrays.asList(tagsConditions)));
						}
					}

				}
			} else {
				conditions.add(QueryCondition.like("tags", tags));
			}
		}
		return conditions;
	}

	private List<QueryCondition> searchResponse(String employeeCode1) {
		List<QueryCondition> conditions = new ArrayList<>();
		// 责任人，支持多个责任人查询
		if (employeeCode1 != null && !"".equals(employeeCode1.trim())) {
			QueryCondition[] tagsConditions = null;
			// 多个标签
			if (employeeCode1.indexOf(",") > 0) {
				String[] tempTags = employeeCode1.split("\\,");
				if (tempTags != null && tempTags.length >= 2) {
					QueryCondition conditionOne = null;
					QueryCondition conditionTwo = null;
					if (tempTags.length >= 2) {
						tagsConditions = new QueryCondition[tempTags.length - 2];
						for (int i = 0, j = 0; i < tempTags.length; i++) {
							if (i == 0) {
								conditionOne = QueryCondition.findInSet("employeeCode1", tempTags[0]);
							} else if (i == 1) {
								conditionTwo = QueryCondition.findInSet("employeeCode1", tempTags[1]);
							} else {
								tagsConditions[j] = QueryCondition.findInSet("employeeCode1", tempTags[i]);
								j++;
							}
						}

						if(conditionOne != null){
							conditions.add(conditionOne);
						}else if(conditionTwo != null){
							conditions.add(conditionTwo);
						}else if(tagsConditions != null){
							conditions.addAll(new ArrayList<QueryCondition>(Arrays.asList(tagsConditions)));
						}
					}
				}
			} else {
				conditions.add(QueryCondition.findInSet("employeeCode1", employeeCode1));
			}
		}
		return conditions;
	}

	@Override
	public List<String> getAllTopTagsType() {
		List<String[]> list = assetDao.getAllTopTagsType();
		List<String> topTypeList = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (String[] temp : list) {
				if (temp != null && temp.length > 0) {
					String nodeString = temp[0];
					// 如果存在节点且节点像：A,B 需要分割成
					if(StringUtils.isNotEmpty(nodeString)) {

						if ( nodeString.indexOf(",") > 0) {
							String[] nodeArray = nodeString.split(",");
							for (String v_node : nodeArray) {
								if (!topTypeList.contains(v_node.trim())) {
									topTypeList.add(v_node.trim());
								}
							}
						} else {
							if (!topTypeList.contains(nodeString.trim())) {
								topTypeList.add(nodeString.trim());
							}
						}
					}

				}
			}
		}
		return topTypeList;
	}


	@Override
	public Map<String,Integer> getAllTopTagsCount() {
		List<String[]> list = assetDao.getAllTopTagsType();

		Map<String,Integer> result=new HashMap<>();

		if (list != null && !list.isEmpty()) {
			for (String[] temp : list) {
				if (temp != null && temp.length >= 2) {
					String node_string = temp[0];
					String node_count = temp[1];
					Integer count=Integer.parseInt(node_count);
					if(!StringUtils.isEmpty(node_string)) {
						String[] node_array = node_string.split(",");
						for(String tag : node_array) {

							if(result.containsKey(tag)) {
								result.put(tag,count + result.get(tag));
							}else {
								result.put(tag, count);
							}
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public Result<Boolean> validateAssetIp(AssetSearchVO assetSearchVO) {
		String ip = assetSearchVO.getIp();
		String guid = assetSearchVO.getGuid();
		String assetType = assetSearchVO.getAssetType();
		String typeGuid = assetSearchVO.getTypeSnoGuid();
		return validateAssetIp(ip, guid, assetType,typeGuid);

	}

	/**
	 * ip格式，重复性校验
	 * @param ip
	 * @param guid
	 * @param assetTypeGuid
	 * @param assetTypeSnoGuid
	 * @return
	 */
	@Override
	public Result<Boolean> validateAssetIp(String ip, String guid, String assetTypeGuid, String assetTypeSnoGuid) {
		if (StringUtils.isEmpty(assetTypeGuid)) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：资产类型传参异常");
		}
		if (StringUtils.isEmpty(ip)) {
			return ResultUtil.success(true);
		}
		boolean checkIPResult = AssetUtil.checkIP(ip);
		if(!checkIPResult){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "ip格式异常");
		}
		List<QueryCondition> conditions = new ArrayList<>();

		// ip不能重复
		if (StringUtils.isNotBlank(guid)) {
			conditions.add(QueryCondition.notEq("guid", guid));
		}
		conditions.add(QueryCondition.eq("ip", ip));
		long count = count(conditions);
		if (count > 0) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "已存在该IP的资产！");
		}
		return ResultUtil.success(true);
	}


	/**
	 * 资产新增：代码机构重构
	 * @param assetVO
	 * @return
	 */
	@Override
	@Transactional
	public Result<String> saveAddAsset(AssetVO assetVO) {
		try {
			if (StringUtils.isEmpty(assetVO.getAssetType())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产类型不能为空");
			}
			AssetType assetType = assetTypeService.getOne(assetVO.getAssetType());
			if(null == assetType){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产类型不存在");
			}
			// mac、序列号校验
			Result<String> validateResult = assetSaveValidate(assetVO,null);
			if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateResult.getCode())){
				return  validateResult;
			}
			assetWoorthValue(assetVO);  // 资产价值的计算
			// 构造资产相关对象
			Asset asset = getAssetByAssetVO(assetVO,assetType);
			AssetExtend assetExtend = getAssetExtendNew(asset.getGuid(),assetVO.getAssetExtendInfo());
            //终端类型新增时记录操作系统安装时间放入队列
			terminalAddQue(null,asset,null,"1");
			// 保存数据,更新缓存
			saveAssetData(asset,assetExtend,assetType);

			return ResultUtil.success("success");
		} catch (Exception e) {
			logger.error("新增资产异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "新增资产异常！");
		}
	}

	/**
	 * 新增资产更新相关缓存
	 *
	 * @param asset
	 */
	private void assetSaveUpdateCache(Asset asset,AssetType assetType) {
		try{
			updateRedisCache("",asset.getIp());
			logger.info("刷新缓存：{}",asset.getGuid());
			/*****************刷新缓存***************************/
			List<QueryCondition> conditions = new ArrayList<>();
			conditions.add(QueryCondition.notNull("ip"));
			conditions.add(QueryCondition.notEq("ip",""));
			List<Asset> assets = findAll(conditions);
			CommomLocalCache.put("asset-map",assets,2, TimeUnit.HOURS);
			/********************************************/
			AssetRedisCacheVO assetCache= mapper.map(asset,AssetRedisCacheVO.class);
			assetCache.setTypeName(assetType.getName());
			String treeCode = assetType.getTreeCode();
			int indexTwo = treeCode.lastIndexOf('-');
			String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
			List<QueryCondition> conditionList=new ArrayList<>();
			conditionList.add(QueryCondition.like("treeCode",treeCodeGroup));
			List<AssetTypeGroup> groups = assetTypeGroupService.findAll(conditionList);
			if(!org.springframework.util.CollectionUtils.isEmpty(groups)){
				assetCache.setGroupName(groups.get(0).getName());
			}
			baseDataRedisCacheService.addAsset(assetCache);  // 资产redis缓存  2022-08-08
		}catch (Exception e){
			logger.error("资产新增更新相关缓存异常",e);
		}
	}

	/**
	 * 保存资产相关数据
	 * 缓存更新
	 * @param asset
	 * @param assetExtend
	 */
	private void saveAssetData(Asset asset, AssetExtend assetExtend,AssetType assetType) {
		this.save(asset);
		assetExtendService.save(assetExtend);
		// 资产新增更新相关缓存
		assetSaveUpdateCache(asset,assetType);
		// 终端设置统计审计客户端安装情况 2023-1-30
		terminalAssetInstallService.sendCountKafkaMsg();
	}


	private AssetExtend getAssetExtendNew(String guid, String assetExtendInfo) {
		AssetExtend  assetExtend=new AssetExtend();
		assetExtend.setAssetGuid(guid);
		assetExtend.setExtendInfos(assetExtendInfo);
		return assetExtend;
	}

	/**
	 * 构造新增资产对象
	 * @param assetVO
	 * @param assetType
	 * @return
	 */
	private Asset getAssetByAssetVO(AssetVO assetVO, AssetType assetType) {
		assetVOCompletion(assetVO); // 数据补全
		Date osSetuptime = osSetuptimeHandle(assetVO);
		Asset asset = mapper.map(assetVO, Asset.class);
		if(null != osSetuptime){
			asset.setOsSetuptime(osSetuptime);
		}
		String newGuid = UUIDUtils.get32UUID();
		assetDomaiCompletion(asset); // 安全域补全
		asset.setGuid(newGuid);
		String ip = assetVO.getIp();
		if (ip != null) {
			asset.setIpNum(AssetUtil.ip2int(ip));
		}
		asset.setCreateTime(new Date());
		asset.setTypeUnicode(assetType.getUniqueCode());
		asset.setEmployeeCode1(asset.getResponsibleCode());
		asset.setDataSourceType(1); // 数据来源类型为手动输入  2022-06-16
		// 获取对应的一级,放在tags字段中，策略维表用到改字段 2023-09-19
		String treeCode = assetType.getTreeCode();
		int indexTwo = treeCode.lastIndexOf('-');
		String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
		asset.setTags(treeCodeGroup);
		return  asset;

	}

	/**
	 *  mac、序列号校验校验
	 * @param assetVO
	 * @return
	 */
	private Result<String> assetSaveValidate(AssetVO assetVO,String guid) {
		// mac地址校验
		if(StringUtils.isNotEmpty(assetVO.getMac())){
			Result<Boolean> validateAssetMac = validateAssetMac(assetVO.getMac(),guid,assetVO.getAssetType(),assetVO.getAssetTypeSnoGuid());
			if (validateAssetMac.getCode().intValue() != 0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), validateAssetMac.getMsg());
			}
		}
		// 序列号重复校验
		if(StringUtils.isNotEmpty(assetVO.getSerialNumber())){
			if (!validateAssetSerialNumber(String.valueOf(assetVO.getSerialNumber()),guid)) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "设备序列号或产品编号重复");
			}
		}
		return ResultUtil.success("success");
	}

	private Date osSetuptimeHandle(AssetVO assetVO) {
		if (StringUtils.isNotEmpty(assetVO.getOsSetuptime())) {
			try {
				Date osSetuptime = com.vrv.vap.exportAndImport.util.DateUtils.str2Date(assetVO.getOsSetuptime());
                assetVO.setOsSetuptime("");
				return osSetuptime;
			} catch (Exception e) {
				logger.error("操作系统安装时间解析异常", e);
				return null;
			}
		}
		return null;
	}


	/**
	 * 资产编辑---代码重构
	 * @param assetVO
	 * @return
	 */
	@Override
	public Result<String> saveEditAsset(AssetVO assetVO) {
		try {
			String guid = assetVO.getGuid();
			Asset assetOld = getOne(guid);
			if(null == assetOld){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "数据不存在");
			}
			AssetType assetType = assetTypeService.getOne(assetVO.getAssetType());
			if(null == assetType){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产类型不存在");
			}
			// mac、序列号校验
			Result<String> validateResult = assetSaveValidate(assetVO,guid);
			if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateResult.getCode())){
				return  validateResult;
			}
			assetWoorthValue(assetVO);  // 资产价值的计算
			// 资产变更发告警事件需要数据
			String oldIp=assetOld.getIp();
			Date oldOsSetupTime = assetOld.getOsSetuptime();
			String oldOsList = assetOld.getOsList();
			// 构造数据
			Asset assetNew = getAssetByAssetVOEdit(assetVO,assetOld,assetType);
			AssetExtend assetExtend = getAssetExtendEdit(guid,assetVO);
			// 操作系统安装时间放入队列
			terminalAddQue(oldOsSetupTime,assetNew,null,"2");
			// 保存资产相关数据
			saveEditAssets(assetNew,assetExtend,oldIp,oldOsSetupTime,oldOsList,assetType);
			return ResultUtil.success("success");
		} catch (Exception e) {
			logger.error("资产编辑异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产编辑异常");
		}

	}

	private void terminalAddQue(Date oldOsSetupTime, Asset assetNew,List<String> guids, String type)  {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					TerminalAssteInstallTimeJobVO terminalAssteInstallTimeJobVO = new TerminalAssteInstallTimeJobVO();
					terminalAssteInstallTimeJobVO.setAsset(assetNew);
					terminalAssteInstallTimeJobVO.setOldOsSetupTime(oldOsSetupTime);
					terminalAssteInstallTimeJobVO.setType(type);
					terminalAssteInstallTimeJobVO.setGuids(guids);
					terminalAssteInstallTimeService.excTerminalAssteInstallTime(terminalAssteInstallTimeJobVO);
				}catch (Exception e){
					logger.error("系统安装时间数据处理异常",e);
				}
			}
		}).start();

	}


	/**
	 * 编辑保存资产相关信息、更新缓存、资产变动告警事件、终端设置统计审计客户端安装情况
	 * @param asset
	 * @param assetExtend
	 * @param oldIp
	 * @param oldOsSetupTime
	 * @param oldOsList
	 * @param assetType
	 */

	private void saveEditAssets(Asset asset, AssetExtend assetExtend,String oldIp,Date oldOsSetupTime,String oldOsList,AssetType assetType) {
		this.save(asset);
		assetExtendService.save(assetExtend);
		// 资产变动告警事件 2022-07-11
		assetChangeSendAlarmEvent(asset,oldOsSetupTime,oldOsList,assetType.getTreeCode());
		// 刷新缓存
		editAssetUpdateCache(asset,assetType,oldIp);
		// 终端设置统计审计客户端安装情况 2023-1-30
		terminalAssetInstallService.sendCountKafkaMsg();

	}

	/**
	 * 资产编辑刷新缓存
	 * @param asset
	 * @param assetType
	 * @param oldIp
	 */
	private void editAssetUpdateCache(Asset asset, AssetType assetType, String oldIp) {
		try{
			updateRedisCache(oldIp,asset.getIp());
			/*****************刷新缓存***************************/
			List<QueryCondition> conditions = new ArrayList<>();
			conditions.add(QueryCondition.notNull("ip"));
			conditions.add(QueryCondition.notEq("ip",""));
			List<Asset> assets = findAll(conditions);
			CommomLocalCache.put("asset-map",assets,2, TimeUnit.HOURS);
			/********************************************/
			AssetRedisCacheVO assetCache= mapper.map(asset,AssetRedisCacheVO.class);
			assetCache.setTypeName(assetType.getName());
			String treeCode = assetType.getTreeCode();
			int indexTwo = treeCode.lastIndexOf('-');
			String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
			List<QueryCondition> conditionList=new ArrayList<>();
			conditionList.add(QueryCondition.like("treeCode",treeCodeGroup));
			List<AssetTypeGroup> groups = assetTypeGroupService.findAll(conditionList);
			if(!org.springframework.util.CollectionUtils.isEmpty(groups)){
				assetCache.setGroupName(groups.get(0).getName());
			}
			baseDataRedisCacheService.editAsset(assetCache,assetType.getTreeCode(),oldIp);  // 资产redis缓存  2022-08-08
		}catch (Exception e){
			logger.error("资产编辑刷新缓存异常",e);
		}

	}


	private AssetExtend getAssetExtendEdit(String guid, AssetVO assetVO) {
		AssetExtend assetExtend = assetExtendService.getOne(guid);
		if(assetExtend==null) {
			assetExtend=new AssetExtend();
			assetExtend.setAssetGuid(guid);
		}
		assetExtend.setExtendInfos(assetVO.getAssetExtendInfo());
		return assetExtend;
	}

	private Asset getAssetByAssetVOEdit(AssetVO assetVO, Asset asset,AssetType assetType) {
		assetVOCompletion(assetVO); // 数据补全
		Date createTimeOld = asset.getCreateTime();
		String synchUidOld = asset.getSyncUid();
		int dataSourceTypeOld = asset.getDataSourceType(); // 数据来源类型
		String syncSourceOld = asset.getSyncSource();
		Date osSetuptime = osSetuptimeHandle(assetVO);
		mapper.copy(assetVO, asset);
		if(null != osSetuptime){
			asset.setOsSetuptime(osSetuptime);
		}
		asset.setTypeUnicode(assetType.getUniqueCode());
		String ip = assetVO.getIp();
		if (ip != null) {
			asset.setIpNum(AssetUtil.ip2int(ip));
		}
		assetDomaiCompletion(asset); // 安全域补全
		asset.setCreateTime(createTimeOld);
		asset.setEmployeeCode1(asset.getResponsibleCode());
		asset.setSyncSource(syncSourceOld);
		asset.setDataSourceType(dataSourceTypeOld);
		asset.setSyncUid(synchUidOld);
		// 获取对应的一级,放在tags字段中，策略维表用到改字段 2023-09-19
		String treeCode = assetType.getTreeCode();
		int indexTwo = treeCode.lastIndexOf('-');
		String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
		asset.setTags(treeCodeGroup);
		return asset;
	}

	private void assetVOCompletion(AssetVO assetVO) {
		if(StringUtils.isEmpty(assetVO.getWorth())) {
			assetVO.setWorth("1");
		}
		if(StringUtils.isEmpty(assetVO.getSecrecy())) {
			assetVO.setSecrecy("1");
		}
		if(StringUtils.isEmpty(assetVO.getIntegrity())) {
			assetVO.setIntegrity("1");
		}
		if(StringUtils.isEmpty(assetVO.getAvailability())) {
			assetVO.setAvailability("1");
		}
	}

	// 安全域补全
	private void assetDomaiCompletion(Asset asset) {
		if(StringUtils.isEmpty(asset.getSecurityGuid())){
			return ;
		}
		List<BaseSecurityDomain> allDomain =  assetBaseDataService.queryAllDomain();
		for (BaseSecurityDomain domain : allDomain) {
			if (domain.getCode().equals(asset.getSecurityGuid())) {
				asset.setDomainSubCode(domain.getSubCode());
				asset.setDomainName(domain.getDomainName());  // 加上安全域名称 2022-06-29
				return;
			}
		}
	}

	/**
	 * 资产编辑发kafka告警事件消息
	 * @param asset
	 * @param osSetupTimeOld
	 * @param osListOld
	 * @param typeTreeCode
	 */
	private void assetChangeSendAlarmEvent(Asset asset,Date osSetupTimeOld,String osListOld,String typeTreeCode) {
		AlarmEventMsgVO event = new AlarmEventMsgVO();
		event.setOsType(AssetAlarmServiceImpl.OSTYPEEDIT);
		event.setIp(asset.getIp());
		event.setOsList(asset.getOsList());
		event.setOsSetuptime(asset.getOsSetuptime());
		event.setSyncSource(asset.getSyncSource());
		event.setOsListOld(osListOld);
		event.setOsSetuptimeOld(osSetupTimeOld);
		event.setTypeTreeCode(typeTreeCode);
		assetAlarmService.assetChangeSendAlarmEvnet(event);
	}

	/**
	 * 资产删除-----重构
	 * @param guid
	 * @return
	 */
	@Override
	public Result<Boolean> deleteAsset(String guid) {
		Asset entity = super.getOne(guid);
		if(null == entity){
			return ResultUtil.success(true);
		}
		// 删除资产扩展信息
		if(assetExtendService.exists(guid)){
			assetExtendService.delete(guid);
		}
		// 删除资产
		super.delete(entity);
		// 删除安装信息
		deleteTerminalAssteInstallTime(guid);
		// 应用系统删除对应信息
		appSysManagerService.deleteAppServers(guid);
		// 更新资产redis缓存  2022-08-08
		assetDeleteUpdateCache(entity);
		return ResultUtil.success(true);
	}

	private void deleteTerminalAssteInstallTime(String guid) {
		List<String> guids= new ArrayList<>();
		guids.add(guid);
		//终端类型新增时记录操作系统安装时间放入队列
		terminalAddQue(null,null,guids,"3");
	}

	/**
	 * 批量资产删除
	 * @param guids
	 * @return
	 */
	@Override
	public Result<Boolean> batchDeleteAsset(List<String> guids) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.in("guid",guids));
		List<Asset> assets = this.findAll(conditions);
		if(CollectionUtils.isEmpty(assets)){
			return ResultUtil.success(true);
		}
		// 删除资产
		super.deleteInBatch(assets);
		// 删除资产扩展信息
		conditions = new ArrayList<>();
		conditions.add(QueryCondition.in("assetGuid",guids));
		List<AssetExtend> assetExtends = assetExtendService.findAll(conditions);
		if(CollectionUtils.isNotEmpty(assetExtends)){
			assetExtendService.deleteInBatch(assetExtends);
		}
		// 其他附属信息处理:终端类型记录、应用系统删除、redis缓存更新
		othterHandle(guids,assets);
		return ResultUtil.success(true);
	}

	private void othterHandle(List<String> guids, List<Asset> assets) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					// 删除安装信息
					//终端类型新增时记录操作系统安装时间放入队列
					terminalAddQue(null,null,guids,"3");
					// 应用系统删除对应信息
					appSysManagerService.batchDeleteAppServers(guids);
					// 更新资产redis缓存
					for(Asset asset : assets) {
						assetDeleteUpdateCache(asset);
					}
				}catch (Exception e){
					logger.error("资产批量删除时，关联处理异常：{}",e);
				}
			}
		}).start();
	}

	private void assetDeleteUpdateCache(Asset entity) {
		try{
			AssetType assetType = assetTypeService.getOne(entity.getAssetType());
			baseDataRedisCacheService.delAsset(entity.getGuid(),assetType.getTreeCode());
			updateRedisCache(entity.getIp(), "");
		}catch (Exception e){
			logger.error("删除更新缓存异常",e);
		}
	}


	/**
	 * 按照资产品牌型号进行分类处理（自带权限）
	 *
	 * @param assetSearchVO
	 * @return Map<snoGuid, List<Asset>>
	 */
	@Override
	public Map<String, List<AssetExportDataVO>> getAssetBySno(AssetSearchVO assetSearchVO) {
		Map<String, List<AssetExportDataVO>> assetMap = new HashMap<>();
		List<QueryCondition> conditions = searchAssetCondition(assetSearchVO);
		if(assetSearchVO.getSelectIds()!=null) {
			List<String> selectIds = assetSearchVO.getSelectIds();
			if(!selectIds.isEmpty()) {
				conditions.add(QueryCondition.in("assetTypeSnoGuid", selectIds));
			}
		}
		List<String> userAuthorityDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();

		if(userAuthorityDomainCodes!=null) {
			conditions.add(QueryCondition.in("securityGuid", userAuthorityDomainCodes));
		}


		List<AssetExportDataVO> allData = assetDao.getAllData (conditions);

		for (AssetExportDataVO asset : allData) {

			String assetTypeSnoGuid = asset.getAssetTypeSnoGuid();

			if(assetMap.get(assetTypeSnoGuid)!=null) {
				List<AssetExportDataVO> subAssetList = assetMap.get(assetTypeSnoGuid);
				subAssetList.add(asset);
			}else {
				List<AssetExportDataVO> subAssetList = new ArrayList<>();
				subAssetList.add(asset);
				assetMap.put(assetTypeSnoGuid, subAssetList);
			}
		}
		return assetMap;
	}

	private static void printDebugLog(Date lastTime, Date now, String msg) {
		if (lastTime == null) {
			logger.debug(msg + "：" + DateUtil.format(now, "yyyy-MM-dd HH:mm:ss.SSS"));
		} else {
			logger.debug("耗时：{} ms ;{}：{}" ,DateUtils.truncatedCompareTo(now, lastTime, Calendar.MILLISECOND), msg , DateUtil.format(now, "yyyy-MM-dd HH:mm:ss.SSS"));
		}
		lastTime = now;
	}

	@Override
	public Map<String, List<AssetExportDataVO>> getAssetByType(AssetSearchVO assetSearchVO) {
		Date lastTime = new Date();
		printDebugLog(lastTime, new Date(), "开始数据构造");

		Map<String, List<AssetExportDataVO>> assetMap = new HashMap<>();
		List<QueryCondition> conditions = searchAssetCondition(assetSearchVO);

		if (assetSearchVO.getSelectIds() != null) {
			List<String> selectIds = assetSearchVO.getSelectIds();
			if (!selectIds.isEmpty()) {
				conditions.add(QueryCondition.in("assetType", selectIds));
			}
		}
		printDebugLog(lastTime, new Date(), "查询条件构造完成");

		List<String> userAuthorityDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();

		if (userAuthorityDomainCodes != null) {
			conditions.add(QueryCondition.in("securityGuid", userAuthorityDomainCodes));
		}
		printDebugLog(lastTime, new Date(), "securityGuid构造完成");

		List<AssetExportDataVO> list = assetDao.getAllData(conditions);
		printDebugLog(lastTime, new Date(), "数据查询完成");
		for (AssetExportDataVO asset : list) {
			String assetTypeSnoGuid = asset.getAssetType();

			if (assetMap.get(assetTypeSnoGuid) != null) {
				List<AssetExportDataVO> subAssetList = assetMap.get(assetTypeSnoGuid);
				subAssetList.add(asset);
			} else {
				List<AssetExportDataVO> subAssetList = new ArrayList<>();
				subAssetList.add(asset);
				assetMap.put(assetTypeSnoGuid, subAssetList);
			}
		}
		printDebugLog(lastTime, new Date(), "Map构造完成");
		return assetMap;
	}

	//  一级类型获取资产信息
	@Override
	public Map<String, List<AssetExportDataVO>> getAssetByTypeGroup(AssetSearchVO assetSearchVO,Map<String,List<AssetType>> assetTypesMap,Map<String,String> uniqueCodeAndTreeCodeMap ){
		Date lastTime = new Date();
		printDebugLog(lastTime, new Date(), "开始数据构造");
		// 获取一级类型对象
		List<QueryCondition> con1 = new ArrayList<>();
		con1.add(QueryCondition.eq("status", 0));
		if(assetSearchVO.getSelectIds()!=null&&!assetSearchVO.getSelectIds().isEmpty()) {
			con1.add(QueryCondition.in("guid", assetSearchVO.getSelectIds()));
		}
		List<AssetTypeGroup> assetGroupAll = assetTypeGroupService.findAll(con1);
		// 获取对应的二级类型
		Map<String, List<AssetExportDataVO>> assetMap = new HashMap<>();
		for(AssetTypeGroup group :assetGroupAll){
			List<String> assetTypeGuids = new ArrayList<String>();
			String treeCode = group.getTreeCode();
			con1 = new ArrayList<>();
			con1.add(QueryCondition.eq("status", 0));
			con1.add(QueryCondition.likeBegin("treeCode",treeCode));
			List<AssetType> assetTypes = assetTypeService.findAll(con1);
			if(null == assetTypes || assetTypes.size() == 0){
				continue;
			}
			for(AssetType assetType :assetTypes){
				assetTypeGuids.add(assetType.getGuid());
			}
			List<QueryCondition> conditions = searchAssetCondition(assetSearchVO);
			conditions.add(QueryCondition.in("assetType", assetTypeGuids));
			List<String> userAuthorityDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
			if (userAuthorityDomainCodes != null) {
				conditions.add(QueryCondition.in("securityGuid", userAuthorityDomainCodes));
			}
			List<AssetExportDataVO> list = assetDao.getAllData(conditions);
			if(null != list && list.size() > 0){
				assetMap.put(group.getGuid(),list);
				assetTypesMap.put(group.getGuid(),assetTypes);
				uniqueCodeAndTreeCodeMap.put(group.getGuid()+"uniqueCode",group.getUniqueCode());
				uniqueCodeAndTreeCodeMap.put(group.getGuid()+"treeCode",group.getTreeCode());
			}
		}
		printDebugLog(lastTime, new Date(), "Map构造完成");
		return assetMap;

	}

	@Override
	public AssetVO mapperVO(Asset asset,List<BaseSecurityDomain> allDomains) {
		AssetVO assetVO = mapper.map(asset, AssetVO.class);
		String assetTypeGuid = asset.getAssetType();
		AssetType assetType = null;
		if(StringUtils.isNotEmpty(assetTypeGuid)){
			assetType = assetTypeService.getOne(assetTypeGuid);
		}
		if (null != assetType) {
			assetVO.setTypeName(assetType.getName());
			assetVO.setTypeUnicode(assetType.getUniqueCode());
		}
		if (!StringUtils.isEmpty(asset.getSecurityGuid())) {
			for (BaseSecurityDomain domain : allDomains) {
				if (asset.getSecurityGuid().equals(domain.getCode())) {
					assetVO.setSecurityName(domain.getDomainName());
					break;
				}
			}
		}
		return assetVO;

	}

	@Override
	public Result<AssetVO> getSingleAsset(String guid) {
		Asset asset = getOne(guid);
		if (asset==null){
			Result result=new Result();
			result.setCode(ResultCodeEnum.SUCCESS.getCode());
			result.setMsg("资产不存在");
			return  result;
		}
		AssetVO assetVO = mapper.map(asset, AssetVO.class);

		AssetExtend one = assetExtendService.getOne(guid);
		if (one != null) {
			assetVO.setAssetExtendInfo(one.getExtendInfos());
		}
		Result<AssetVO> result = new Result<>();
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(assetVO);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}


	/**
	 * 按照任意列分组统计数值
	 *
	 * @param columnName
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getAssetCountByAnyColumn(String columnName) {
		return assetDao.getAssetCountByAnyColumn(columnName);
	}

	/**
	 * 获取资产数据统计 按照资产类型（大类分组）
	 *
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getAssetCountByAssetType() {
		return assetDao.getAssetCountByAssetType();
	}

	@Override
	public List<Map<String, Object>> getAssetCreateCountByTime(String timeType) {

		return assetDao.getAssetCreateCountByTime(timeType);
	}

	@Override
	public void delete(String id) {
		Asset one = super.getOne(id);
		assetOperationLogService.addLog(OperationTypeEnum.DELETE, one);
		super.delete(id);
	}

	@Override
	public void delete(Asset entity) {
		assetOperationLogService.addLog(OperationTypeEnum.DELETE, entity);
		super.delete(entity);
	}

	public AssetOrgTreeVO organizationByCodeChuTianYun() {
		AssetOrgTreeVO root = new AssetOrgTreeVO();
		User currentUser = SessionUtil.getCurrentUser();
		String orgCode = "";
		if (currentUser.getRoleIds().contains(101) || currentUser.getOrgCode().equals("0")) {
			orgCode = "0";
			root.setName("全网");
		} else {
			orgCode = currentUser.getOrgCode();
			root.setName(currentUser.getOrgName());
		}
		root.setCode(orgCode);
		List<Map<String, Object>> mapList;
		if (orgCode.equals("0")) {
			mapList = baseKoalOrgDao.organizationByParentCode(orgCode);
		} else {
			mapList = baseKoalOrgDao.organizationByCode(orgCode);
		}
		Map<String, List<Map<String, Object>>> listMap = mapList.stream()
				.collect(Collectors.groupingBy(item -> item.get("orgCode").toString()));
		for (Map.Entry<String, List<Map<String, Object>>> entry : listMap.entrySet()) {
			List<Map<String, Object>> list = entry.getValue();
			List<Map<String, Object>> childList = baseKoalOrgDao.organizationByParentCode(entry.getKey());
			AssetOrgTreeVO child = new AssetOrgTreeVO();
			List<AssetOrgTreeVO> assetOrgTreeVOList = mapper.mapList(list, AssetOrgTreeVO.class);
			assetOrgTreeVOList = assetOrgTreeVOList.stream().filter(assetOrgTreeVO -> assetOrgTreeVO.getCode() != null)
					.collect(Collectors.toList());
			if (childList != null && !childList.isEmpty()) {
				for (Map<String, Object> map : childList) {
					if (map.get("code") != null) {
						AssetOrgTreeVO appChild = new AssetOrgTreeVO();
						appChild.setName(map.get("name").toString());
						appChild.setCode(map.get("code").toString());
						assetOrgTreeVOList.add(appChild);
					}

				}
			}
			child.setChildren(assetOrgTreeVOList);
			child.setCode(entry.getKey());
			child.setName(list.get(0).get("domain_name").toString());
			if (!orgCode.equals("0")) {
				root = child;
				break;
			}
			List<AssetOrgTreeVO> rootChildren = root.getChildren();
			rootChildren.add(child);
			root.setChildren(rootChildren);
		}
		return root;
	}

	@Override
	public AssetOrgTreeVO organizationByCode() {

		String switchCase = systemConfigService.getCurrentConfig(null);
		if (switchCase.equals("chutianyun")) {
			return organizationByCodeChuTianYun();
		}

		AssetOrgTreeVO root = new AssetOrgTreeVO();

		VData<List<BaseSecurityDomain>> rootDomains = adminFeign.getRootDomains();
		if (rootDomains != null && rootDomains.getCode().equals("0")) {
			List<BaseSecurityDomain> data = rootDomains.getData();
			if (data.isEmpty()) {
				return null;
			}
			root.setCode(data.get(0).getCode());
			root.setName(data.get(0).getDomainName());
		}

		List<Map<String, Object>> assetCountByAnyColumn = this.getAssetCountByAnyColumn("ifnull(securityGuid,'')");
		List<BaseSecurityDomain> allDomain = assetBaseDataService.queryAllDomain();
		List<BaseSecurityDomain> list = new ArrayList<>();
		if (allDomain != null && !allDomain.isEmpty()) {
			list = allDomain;
			logger.info("请求安全域成功，数量：{}" , String.valueOf(list.size()));
		}
		Set<String> subCodes = new HashSet<>();
		for (Map<String, Object> map : assetCountByAnyColumn) {
			if (map.get("key") == null || StringUtils.isEmpty(map.get("key").toString())) {
				map.put("DomainGuid", "isNull");
				map.put("DomainName", "未知");
				map.put("subCode", "未知");
			} else {
				String guid = map.get("key").toString();
				map.put("DomainGuid", FileHeaderUtil.checkFileHeader(guid));
				for (BaseSecurityDomain domain : list) {
					if (domain.getCode().equals(guid)) {
						map.put("DomainName", domain.getDomainName());
						map.put("subCode", domain.getSubCode());
						subCodes.add(domain.getSubCode());
						break;
					}
				}
				if (!map.containsKey("DomainName")) {
					map.put("DomainName", "未知" + FileHeaderUtil.checkFileHeader(guid));
					map.put("subCode", "未知");
				}
			}
		}

		if (assetCountByAnyColumn.isEmpty()) {
			return root;
		}

		getAssetOrgTreeVO(root, subCodes);

		return root;
	}

	// 校验序列号是否存在 2021-08-16 新增/编辑
	@Override
	public boolean validateAssetSerialNumber(String serialNumber ,String guid ) {
		try {
			logger.info("validateAssetSerialNumber,serialNumber");
			List<QueryCondition> conditions = new ArrayList<>();
			conditions.add(QueryCondition.eq("serialNumber", serialNumber));
			if(StringUtils.isNotEmpty(guid)){ // 编辑时，排查当前资产
				conditions.add(QueryCondition.notEq("guid", guid));
			}
			long count = this.count(conditions);
			if (count > 0) {
				logger.info("序列号重复,"+serialNumber);
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("校验序列号重复校验异常,error", e);
			return false;
		}
	}

    private void getAssetOrgTreeVO(AssetOrgTreeVO root, Set<String> subCodes) {

		VData<List<BaseSecurityDomain>> childrenDomains = adminFeign.getChildrenDomainByCode(root.getCode());
		if (childrenDomains != null && childrenDomains.getCode().equals("0")) {
			if (childrenDomains.getData() != null && !childrenDomains.getData().isEmpty()) {
				List<BaseSecurityDomain> children = childrenDomains.getData();
				for (BaseSecurityDomain child : children) {
					String subCode = child.getSubCode();
					Boolean isHave = false;
					for (String treeCode : subCodes) {
						if (treeCode.startsWith(subCode)) {
							isHave = true;
							break;
						}
					}
					if (isHave) {
						List<AssetOrgTreeVO> childrenList = root.getChildren();
						if (childrenList == null) {
							childrenList = new LinkedList<>();
						}
						AssetOrgTreeVO childVo = new AssetOrgTreeVO();
						childVo.setCode(child.getCode());
						childVo.setName(child.getDomainName());

						getAssetOrgTreeVO(childVo, subCodes);
						childrenList.add(childVo);
					}
				}
			}
		}
	}

	/**
	 * 更新缓存
	 *
	 * @param oldIp
	 * @param newIp
	 */
	private void updateRedisCache(String oldIp, String newIp) {
		if (StringUtils.isEmpty(newIp)) {
			newIp = "";
		}

		if (StringUtils.isEmpty(oldIp)) {
			oldIp = "";
		}
		List<Map<String, Object>> assetIpAndIds = assetDao.getAssetIpAndIds(oldIp, newIp);
		if (assetIpAndIds != null && !assetIpAndIds.isEmpty()) {

			for (Map<String, Object> map : assetIpAndIds) {
				Object object1 = map.get("Ip");
				Object object2 = map.get("Ids");
				if (object1 != null && object2 != null) {
					String Ip = object1.toString();
					String Ids = object2.toString();
					if (StringUtils.isNotEmpty(Ip) && StringUtils.isNotEmpty(Ids)) {
						assetRedisUtil.delete(Ip);
						assetRedisUtil.save(newIp, Ids.split(","));
					}
				}
			}

		}
	}

	/**
	 * 统计不同资产类型下的资产数量（一级资产类型）
	 *
	 * @return list
	 */
	@Override
	public List<Map<String, Object>> queryAssetTypeNumber(){
		return assetDao.queryAssetTypeNumber();
	}

	/**
	 * 统计不同部门下的资产数量
	 *
	 * @return list
	 */
	@Override
	public List<Map<String, Object>> queryDepartmentNumber(){
		return assetDao.queryDepartmentNumber();
	}

	// 基础数据查询终端接口信息
	@Override
	public PageRes<Map<String, Object>> queryAssetHostsPager(AssetSearchVO assetSearchVO) {
		PageRes<Map<String, Object>> pageRes = new PageRes<Map<String, Object>>();
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		Long total = assetDao.queryAssetHostsTotal(assetSearchVO.getResponsibleCode());
		pageRes.setTotal(total);
		if (total <= 0) {
			return pageRes;
		}
		// 分页处理
		Integer start = assetSearchVO.getStart_();
		Integer count = assetSearchVO.getCount_();
		List<Map<String, Object>> assetDatas = assetDao.queryAssetHostsPage(assetSearchVO.getResponsibleCode(), start, count);
		dataHandle(assetDatas);
		pageRes.setList(assetDatas);
		return pageRes;
	}

	// 扩展类型字段处理
	private void dataHandle(List<Map<String, Object>> list) {
		if (null == list || list.size() <= 0) {
			return;
		}
		for (Map<String, Object> vo : list) {
			String extendInfos = vo.get("extendInfos") == null ? "" : String.valueOf(vo.get("extendInfos"));
			if (StringUtils.isEmpty(extendInfos)) {
				vo.put("diskNumber", "");
				vo.put("systemName", "");
				vo.put("visionInfo", "");
				vo.put("typeName", ""); // 设备品牌
				vo.put("sonName", ""); // 设备型号（型号）
				continue;
			}
			JSONObject extendDatas = JSONObject.parseObject(extendInfos);
			vo.put("diskNumber", extendDatas.get("extendDiskNumber")); // 磁盘序列号
			vo.put("systemName", extendDatas.get("extendSystem")); // 操作系统名称
			vo.put("visionInfo", extendDatas.get("sysSno")); // 操作系统版本
			vo.put("typeName",  extendDatas.get("extendTypeSno")); // 设备品牌
			vo.put("sonName",  extendDatas.get("extendVersionInfo")); // 设备型号（型号）
			// 清除扩展信息字段，便于接口展示
			vo.remove("extendInfos");
		}

	}

	// 查询usb设备存储介质（支持分页）
	@Override
	public PageRes<Map<String, Object>> queryUSBMemorysPager(AssetSearchVO assetSearchVO) {
		PageRes<Map<String, Object>> pageRes = new PageRes<Map<String, Object>>();
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		Long total = assetDao.queryUSBMemorysTotale(assetSearchVO.getResponsibleCode());
		pageRes.setTotal(total);
		if (total <= 0) {
			return pageRes;
		}
		// 分页处理
		Integer start = assetSearchVO.getStart_();
		Integer count = assetSearchVO.getCount_();
		List<Map<String, Object>> assetDatas = assetDao.queryUSBMemorysPage(assetSearchVO.getResponsibleCode(), start , count);
		// 使用范围
		for (Map<String, Object> vo : assetDatas) {
			String extendInfos = vo.get("extendInfos") == null ? "" : String.valueOf(vo.get("extendInfos"));
			vo.put("useRange", ""); // 使用范围
			if (!StringUtils.isEmpty(extendInfos)) {
				JSONObject extendDatas = JSONObject.parseObject(extendInfos);
				vo.put("useRange", extendDatas.get("useRange"));
				// 清除扩展信息字段，便于接口展示
				vo.remove("extendInfos");
			}
		}
		pageRes.setList(assetDatas);
		return pageRes;
	}
    // USB外设（支持分页）
	@Override
	public PageRes<Map<String, Object>> queryUSBPeripheralsPager(AssetSearchVO assetSearchVO){
		PageRes<Map<String, Object>> pageRes = new PageRes<Map<String, Object>>();
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		Long total = assetDao.queryUSBPeripheralsTotal(assetSearchVO.getResponsibleCode());
		pageRes.setTotal(total);
		if (total <= 0) {
			return pageRes;
		}
		// 分页处理
		Integer start = assetSearchVO.getStart_();
		Integer count = assetSearchVO.getCount_();
		List<Map<String, Object>> assetDatas = assetDao.queryUSBPeripheralsPage(assetSearchVO.getResponsibleCode(), start, count);
		pageRes.setList(assetDatas);
		return pageRes;
	}

    // 终端上安装安全保密产品数量
	@Override
	public Long queryAssetHostSafeNums(String responsibleCode){
		return assetDao.queryAssetHostSafeNums(responsibleCode);
	}

	// 获取安全保密产品安装情况
	@Override
	public List<Map<String, Object>>  querySafeProductInfo(String responsibleCode){
		return assetDao.querySafeProductInfo(responsibleCode);
	}

	// 终端类型安装与未安装统计
	@Override
	public List<Map<String, Object>> terminalAssetInstallCount(){
		return assetDao.terminalAssetInstallCount();
	}

	// 当前设备是不是终端类型
	@Override
	public boolean isTerminalAsset(String typeUniqueCode){
		int count =  assetDao.terminalAssetByTypeUniqueCode(typeUniqueCode);
		if(count > 0){
			return true;
		}
		return false;
	}

	// 硬件资产校验mac的唯一性，目前的资产都是硬件资产 2021-08-27
	@Override
	public Result<Boolean> validateAssetMac(String mac, String guid, String assetTypeGuid, String assetTypeSnoGuid){
		try{
			if (StringUtils.isEmpty(mac)) {
				return ResultUtil.success(true);
			}
			List<QueryCondition> conditions = new ArrayList<>();
			if (StringUtils.isNotBlank(guid)) {
				conditions.add(QueryCondition.notEq("guid", guid));
			}
			conditions.add(QueryCondition.eq("mac", mac));
			long count = count(conditions);
			if (count > 0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "已存在该mac的资产！");
			}
		}catch (Exception e){
			logger.error("mac的唯一性校验失败",e);
			return ResultUtil.error(-1,"false");
		}
		return ResultUtil.success(true);
	}

	// 工作台：统计终端、服务器、网络设备、安全产品资产的总数量
	@Override
	public long queryWorkplatformnNum(String groupTreeCode){
		return assetDao.queryWorkplatformnNum(groupTreeCode);
	}


	@Override
	public AssetDetailVO getOneAssetDetailByIp(String ip) {
		AssetDetailVO  result=new AssetDetailVO();
		List<QueryCondition> cons = new ArrayList<>();
		cons.add(QueryCondition.eq("ip", ip));
		List<Asset> findAll = this.findAll(cons);
		Asset asset = null;
		if(findAll!=null && !findAll.isEmpty()){
			asset = findAll.get(0);
		}else {
			return result;
		}
		result.setAsset(asset);

		AssetExtend assetExtend = assetExtendService.getOne(asset.getGuid());
		result.setAssetExtend(assetExtend);

		List<BaseSecurityDomain> allDomain = assetBaseDataService.queryAllDomain();
		if(allDomain!=null&&!allDomain.isEmpty()) {
			for(BaseSecurityDomain domain : allDomain) {
				if(domain.getCode().equals(asset.getSecurityGuid())) {
					result.setBaseSecurityDomain(domain);
					break;
				}
			}
		}
		AssetType assetType = assetTypeService.getOne(asset.getAssetType());
		result.setAssetType(assetType);
		AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(asset.getAssetTypeSnoGuid());
		result.setAssetTypeSno(assetTypeSno);
		return result;
	}

	@Override
	public AssetDetailVO getAssetDetail(String guid) {
		AssetDetailVO  result=new AssetDetailVO();
		Asset asset = this.getOne(guid);
		result.setAsset(asset);
		AssetExtend assetExtend = assetExtendService.getOne(guid);
		result.setAssetExtend(assetExtend);

		List<BaseSecurityDomain> allDomain = assetBaseDataService.queryAllDomain();
		if(allDomain!=null&&!allDomain.isEmpty()) {
			for(BaseSecurityDomain domain : allDomain) {
				if(domain.getCode().equals(asset.getSecurityGuid())) {
					result.setBaseSecurityDomain(domain);
					break;
				}
			}
		}

		AssetType assetType = assetTypeService.getOne(asset.getAssetType());
		result.setAssetType(assetType);

		AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(asset.getAssetTypeSnoGuid());
		result.setAssetTypeSno(assetTypeSno);
        return result;
	}

	@Override
	public long getAssetCount() {
		if(SessionUtil.getCurrentUser()!=null&& SessionUtil.getauthorityType()) {
			List<String> userDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
			List<QueryCondition> conditions=new ArrayList<>();
			if(userDomainCodes==null||userDomainCodes.isEmpty()) {
				return 0L;
			}else {
				conditions.add(QueryCondition.in("securityGuid", userDomainCodes));
				return this.count(conditions);
			}
		}
		return 0L;
	}

	/**
	 * 根据NTDS数据更新资产表数据    2022-04-14
	 * @param ntds
	 * @return
	 */
	@Override
	public Result<String> updateAssetByNTDS(List<AuditAssetVO> ntds) {
		Result<String> result = new Result<String>();
		if(null == ntds || ntds.size() ==0){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有数据！");
		}
		logger.info("根据NTDS数据更新资产表数据："+ntds.size());
		// 获取其他终端的guid asset-Host-HostOthers
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("treeCode", "asset-Host-HostOthers"));
		List<AssetType> assetTypes = this.assetTypeService.findAll(conditions);
		// 根据ip更新资产信息数据，如果ip不存在新增一条资产记录，一级资产未终端，二级资产未其他终端
		List<Asset> allAsset = new ArrayList<>();
		// ipAddress多个ip用分号分割
		for(AuditAssetVO data : ntds){
			List<Asset> assets = parseNtds(data,assetTypes);
			if(null == assets || assets.size() == 0){
				continue;
			}
			allAsset.addAll(assets);
		}
        // 执行资产保存处理
		logger.info("执行资产保存处理，资产数量："+allAsset.size());
		this.save(allAsset);
		// 更新缓存
		baseDataRedisCacheService.updateAllAssetCache(); // 全量更新资产相关缓存 2022-08-09
		return ResultUtil.success("根据NTDS数据更新资产表数据成功");
	}

	private List<Asset> parseNtds(AuditAssetVO data ,List<AssetType> assetTypes) {
		String ipAddress = data.getIpAddress();
		if(StringUtils.isEmpty(ipAddress)){
			logger.info("ipAddress为空，该条数据不处理："+ JSON.toJSONString(data));
			return null;
		}
		List<Asset> assets = new ArrayList<>(); //更新的资产
		// 分号分割获取具体ip
		String[] ips = ipAddress.split(";");
		// 判读ip是不是asset表中存在
		for(int i=0 ;i<ips.length;i++){
			String ip = ips[i];
			if(StringUtils.isEmpty(ip)){
				continue;
			}
			Asset asset = getAssetByIp(ip);
			if(null == asset){ // ip没有对应资产新增资产
				Asset assetNew = new Asset();
				constructAssetData(data,ip,assetNew);
				newAssetData(assetNew,assetTypes); //新资产数据处理
				assets.add(assetNew);
			}else{ //ip有对应资产，更新资产信息
				constructAssetData(data,ip,asset);
				asset.setUpdateTime(new Date()); //增加更新时间
				assets.add(asset);
			}
		}
		return assets;
	}

	/**
	 * 资产记录：新增guid、一级资产类型(终端)、二级资产类型(其他终端)
	 * @param assetNew
	 */
	private void newAssetData(Asset assetNew,List<AssetType> assetTypes) {
		if(null == assetTypes){
			throw new AlarmDealException(-1,"资产类型中其他终端不存在！");
		}
		AssetType assetType = assetTypes.get(0);
		assetNew.setGuid(UUIDUtils.get32UUID());
		assetNew.setAssetType(assetType.getGuid());
		assetNew.setTypeUnicode(assetType.getUniqueCode());
		assetNew.setCreateTime(new Date());
		// 考虑报表统计时：涉及到了国产与非国产，涉密等级，做以下处理： 2023-07-11
		// 是否国产，默认为非国产
		assetNew.setTermType("2");
		// 涉密等级，为空的，默认为非密 4
		String equipmentIntensive = assetNew.getEquipmentIntensive();
		if(StringUtils.isEmpty(equipmentIntensive)){
			assetNew.setEquipmentIntensive("4");
		}
	}

	/**
	 * 构造资产数据
	 *
	 * domain_name、install_anti_virus_status、client_status、device_status、client_register、client_up_last_time、
	 * device_id、name、ip、mac、equipment_intensive、serial_number、os_setup_time、os_list
	 * @param data
	 * @param ip
	 * @param asset
	 * @return
	 */
	private void constructAssetData(AuditAssetVO data, String ip,Asset asset) {
		asset.setIp(ip);
		if(StringUtils.isNotEmpty(data.getMac())){
			asset.setMac(data.getMac());
		}
		if(StringUtils.isNotEmpty(data.getName())){
			asset.setName(data.getName());
		}
		if(StringUtils.isNotEmpty(data.getDomainName())){
			asset.setDomainName(data.getDomainName());
		}
		if(null!= data.getInstallAntiVirusStatus()){
			asset.setInstallAntiVirusStatus(data.getInstallAntiVirusStatus());
		}
		if(null != data.getClientStatus()){
			asset.setClientStatus(data.getClientStatus());
		}
		if(null != data.getDeviceStatus()){
			asset.setDeviceStatus(data.getDeviceStatus());
		}
		if(null != data.getClientRegister()){
			asset.setIsMonitorAgent(data.getClientRegister()+"");
		}
		if(null != data.getClientUpLastTime()){
			asset.setClientUpLastTime(data.getClientUpLastTime());
			// 当前时间与clientUpLastTime差值分钟表示
			long clientUpLastTime = data.getClientUpLastTime().getTime();
			long curData= new Date().getTime();
			Long betweenDate = (curData - clientUpLastTime) / 60000L;
			asset.setClinetTimeDifference(betweenDate.intValue());
		}
		if(StringUtils.isNotEmpty(data.getDeviceId())){
			asset.setDeviceId(data.getDeviceId());
		}
		if(StringUtils.isNotEmpty(data.getEquipmentIntensive())){
			asset.setEquipmentIntensive(data.getEquipmentIntensive());
		}
		if(StringUtils.isNotEmpty(data.getSerialNumber())){
			asset.setSerialNumber(data.getSerialNumber());
		}
		if(null != data.getOssetuptime()){
			asset.setOsSetuptime(data.getOssetuptime());
		}
		if(StringUtils.isNotEmpty(data.getOsList())){
			asset.setOsList(data.getOsList());
		}
	}


	private Asset getAssetByIp(String ip){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ip", ip));
		List<Asset> assets = this.findAll(conditions);
		if(null != assets && assets.size() > 0){
			return assets.get(0);
		}
		return null;
	}

	/**
	 * 计算资产价值
	 *
	 * @param assetVO
	 */
	private void assetWoorthValue(AssetVO assetVO) {
		List<Integer> values = new ArrayList<>();
		if(StringUtils.isEmpty(assetVO.getSecrecy())) {
			assetVO.setSecrecy("0");
		}
		values.add(Integer.valueOf(assetVO.getSecrecy()));
		if(StringUtils.isEmpty(assetVO.getIntegrity())) {
			assetVO.setIntegrity("0");
		}
		values.add(Integer.valueOf(assetVO.getIntegrity()));
		if(StringUtils.isEmpty(assetVO.getAvailability())) {
			assetVO.setAvailability("0");
		}
		values.add(Integer.valueOf(assetVO.getAvailability()));
		if(StringUtils.isEmpty(assetVO.getImportance())) {
			assetVO.setImportance("0");
		}
		values.add(Integer.valueOf(assetVO.getImportance()));
		if(StringUtils.isEmpty(assetVO.getLoadBear())) {
			assetVO.setLoadBear("0");
		}
		values.add(Integer.valueOf(assetVO.getLoadBear()));
		// 计算方式：取最大值
		Integer max = values.stream().max(Integer :: compare).get();
		logger.info("资产价值："+ max);
		assetVO.setWorth(String.valueOf(max));
	}
	//-----------------关保相关start-----------------------//

	/**
	 * 通过ip获取资产的资产类型
	 *
	 * 2022-08-29
	 * @param ips
	 * @return
	 */
	public Result<List<AssetTypeByIpVO>> getAssetTypeByIps(List<String> ips){
		String sql ="select asset.IP as ip,aType.Guid as typeGuid from asset left join asset_type as aType" +
				" on asset.Type_Guid= aType.Guid";
		sql = sql + "  where  asset.IP in('"+StringUtils.join(ips, "','")+"') ";

		List<AssetTypeByIpVO> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetTypeByIpVO>(AssetTypeByIpVO.class));
		return ResultUtil.successList(assets);
	}


	/**
	 * 查询全部资产
	 * @return
	 */
	@Override
	public Result<List<AssetWorthVO>> getAllAsset(){
		String sql ="select guid,Type_Guid as assetType,name,ip,worth,org_name as orgName,org_code as orgCode,responsible_name as responsibleName,responsible_code as responsibleCode from asset";
		List<AssetWorthVO> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetWorthVO>(AssetWorthVO.class));
		return ResultUtil.successList(assets);

	}

	/**
	 * 资产权重
	 *
	 * 所有资产价值的总和
	 * @return
	 */
	@Override
	public Long getAssetWeight(){
		String sql = "select sum(worth) as total from asset ";
		Long total = jdbcTemplate.queryForObject(sql,Long.class);
		return total;
	}

	/**
	 * 通过ip获取权限
	 * @param ips
	 * @return
	 */
	@Override
	public Long getAssetWeightByIp(List<String> ips) {
		String sql = "select sum(worth) as total from asset where ip in ('"+StringUtils.join(ips, "','")+"')";
		Long total = jdbcTemplate.queryForObject(sql,Long.class);
		return total;
	}

	/**
	 * 服务器、终端、网络设备、安全保密产品对应的资产ip
	 * @return
	 */
	@Override
	public List<AssetIpByAssetGroupVO> getIpByGroupType(){
		List<QueryCondition> conditions = new ArrayList<>();
		String[] typs ={"asset-Host","asset-NetworkDevice","asset-SafeDevice","asset-service"};
		conditions.add(QueryCondition.in("treeCode",typs));
		List<AssetTypeGroup> groups = assetTypeGroupService.findAll(conditions);

		String sql = "select asset.ip as ip,asset_type_group.Name as name from asset inner join asset_type on asset.Type_Guid=asset_type.Guid" +
				" inner join asset_type_group on asset_type.TreeCode LIKE CONCAT(`asset_type_group`.`TreeCode`,'-%')  where asset_type_group.TreeCode in('asset-Host','asset-NetworkDevice','asset-SafeDevice','asset-service')";
		List<AssetVO> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetVO>(AssetVO.class));
		if(CollectionUtils.isEmpty(assets)){
			return null;
		}
		List<AssetIpByAssetGroupVO> result = new ArrayList<>();
		AssetIpByAssetGroupVO data = null;
		Map<String, List<AssetVO>> maps = assets.parallelStream().collect(Collectors.groupingBy(AssetVO::getName));
		for(AssetTypeGroup group :groups){
			String name = group.getName();
			List<AssetVO> value = maps.get(name);
			data= new AssetIpByAssetGroupVO();
			data.setName(name);
			if(null != value && value.size() >0){
				data.setIps(getIps(value));
			}else{
				data.setIps(new ArrayList<>());
			}
			result.add(data);
		}
		return result;
	}

	private List<String> getIps(List<AssetVO> value) {
		List<String> ips = new ArrayList<>();
		for(AssetVO asset : value){
			ips.add(asset.getIp());
		}
		return ips;
	}

	/**
	 * 通过ip获取资产中对应责任人
	 * @param ip
	 * @return
	 */
	public Result<String> getEmpNameByIp(String ip){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ip", ip));
		List<Asset> assets = this.findAll(conditions);
		if(CollectionUtils.isEmpty(assets)){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip没有查到对应的资产！");
		}
		String responsibleName = assets.get(0).getResponsibleName();
		return ResultUtil.success(responsibleName);
	}

	@Override
	public Result<AssetType> getAssetTypeByIp(String ip) {
		if(StringUtils.isBlank(ip)){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip不能为空！");
		}
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ip", ip));
		List<Asset> assets = this.findAll(conditions);
		if(CollectionUtils.isEmpty(assets)){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip没有查到对应的资产！");
		}
		String typeGuid = assets.get(0).getAssetType();
		if(StringUtils.isBlank(typeGuid)){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip对应的资产没有资产类型！");
		}
		conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("guid", typeGuid));
		AssetType assetType = this.assetTypeService.getOne(typeGuid);
		if(null == assetType){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip对应资产的资产类型不存在！");
		}
		return  ResultUtil.success(assetType);
	}

	@Override
	public List<String> getAllAssetIps() {
		String sql = "select distinct ip from asset where ip is not null";
		List<String> ips = jdbcTemplate.queryForList(sql,String.class);
		return ips;
	}

	@Override
	public List<String> getAssetIpByOrg(String orgCode){
		String sql = "select distinct ip from asset where org_code = '"+orgCode+"'";
		List<String> ips = jdbcTemplate.queryForList(sql,String.class);
		return ips;
	}

	@Override
	public List<Asset> queryAssets(AssetSearchNewVO assetSearchNewVO) {
		String orgCode = assetSearchNewVO.getOrgCode();
		String orgName= assetSearchNewVO.getOrgName();
		String guid = assetSearchNewVO.getGuid();
		String responsibleName = assetSearchNewVO.getResponsibleName();
		String responsibleCode = assetSearchNewVO.getResponsibleCode();
		String typeGuid = assetSearchNewVO.getTypeGuid();
		String ip = assetSearchNewVO.getIp();
		List<QueryCondition> conditions = new ArrayList<>();
		if(StringUtils.isNotEmpty(orgCode)){
			conditions.add(QueryCondition.eq("orgCode", orgCode));
		}
		if(StringUtils.isNotEmpty(orgName)){
			conditions.add(QueryCondition.eq("orgName", orgName));
		}
		if(StringUtils.isNotEmpty(guid)){
			conditions.add(QueryCondition.eq("Guid", guid));
		}
		if(StringUtils.isNotEmpty(responsibleName)){
			conditions.add(QueryCondition.eq("responsibleName", responsibleName));
		}
		if(StringUtils.isNotEmpty(responsibleCode)){
			conditions.add(QueryCondition.eq("responsibleCode", responsibleCode));
		}
		if(StringUtils.isNotEmpty(ip)){
			conditions.add(QueryCondition.eq("ip", ip));
		}
		if(StringUtils.isNotEmpty(typeGuid)){
			conditions.add(QueryCondition.eq("assetType", typeGuid));
		}
		return this.findAll(conditions);
	}
	//-----------------关保相关end-----------------------//

	/**
	 * 大屏展示资产总数：条件为开始时间和结束时间范围内
	 * 2023-4-20
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public long queryAssetTotalFilter(Date startTime, Date endTime) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.between("createTime", startTime,endTime));
		return this.count(conditions);
	}

	@Override
	public List<String> getAssetIpsByTypeGroup(String type) {
		String sql="SELECT distinct asset.IP FROM asset AS asset " +
				"INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid " +
				"INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX( type.TreeCode, '-', 2 ) = tgroup.TreeCode " +
				" where org_name IS NOT NULL  " +
				" AND org_name != ''  " +
				" AND tgroup.TreeCode = '"+type+"' ";
		List<String> ips = jdbcTemplate.queryForList(sql,String.class);
		return ips;
	}

	@Override
	public void culStealLeakValue(List<Map<String, Map<String, Long>>> list,Date date) {
		List<AssetStealLeakValue> assetStealLeakValues=new ArrayList<>();
		List<QueryCondition> assetConditions=new ArrayList<>();
		assetConditions.add(QueryCondition.notNull("ip"));
		List<Asset> all1 = findAll(assetConditions);
		List<AssetStealLeakValue> all2 = assetStealLeakValueService.findAll();
		if (all1.size()>0){
			List<String> collect = all1.stream().map(a -> a.getIp()).collect(Collectors.toList());
			List<String> collect1 = all2.stream().map(a -> a.getIp()).collect(Collectors.toList());
			collect.removeAll(collect1);
			List<AssetStealLeakValue> assetStealLeakSt=new ArrayList<>();
			for (String  ip:collect) {
				AssetStealLeakValue assetStealLeakValue=new AssetStealLeakValue();
				assetStealLeakValue.setIp(ip);
				assetStealLeakValue.setStealLeakValue(0);
				assetStealLeakValue.setCreateTime(date);
				assetStealLeakValue.setIpNum(VapUtil.ip2int(ip));
				assetStealLeakSt.add(assetStealLeakValue);

			}
			assetStealLeakValueService.save(assetStealLeakSt);
		}
		list.forEach(m->{
			for (Map.Entry<String, Map<String, Long>> entry:m.entrySet()) {
				String key = entry.getKey();
				List<QueryCondition> conditions=new ArrayList<>();
				conditions.add(QueryCondition.eq("ip", key));
				List<Asset> all = this.findAll(conditions);
				if (all.size()>0){
					Asset asset = all.get(0);
					String equipmentIntensive = asset.getEquipmentIntensive();
                   Integer equipmentIntensiveValue=culequipmentIntensiveValue(equipmentIntensive);
					Map<String, Long> value = entry.getValue();
					Integer eventValue=culEventValue(value);
					int valueLast = equipmentIntensiveValue * eventValue;
					AssetStealLeakValue assetStealLeakValue=new AssetStealLeakValue();
					assetStealLeakValue.setIp(key);
					assetStealLeakValue.setStealLeakValue(valueLast);
					assetStealLeakValue.setCreateTime(date);
					assetStealLeakValue.setIpNum(VapUtil.ip2int(key));
					List<AssetStealLeakValue> assetStealLeakValuess = assetStealLeakValueService.findAll(conditions);
					if (assetStealLeakValuess.size()>0){
						assetStealLeakValue.setId(assetStealLeakValuess.get(0).getId());
					}
					assetStealLeakValues.add(assetStealLeakValue);
				}
			}
		});
		assetStealLeakValueService.save(assetStealLeakValues);
	}

	@Autowired
	private AppStealLeakValueService appStealLeakValueService;

	@Override
	public void culAppMaintenStealLeakValue(Date date) {
		List<AppStealLeakValue> appStealLeakValues=new ArrayList<>();
		VData<List<BaseSecurityDomain>> allDomain = adminFeign.getAllDomain();
		List<BaseSecurityDomain> data = allDomain.getData();
		if (data.size()>0){
			for (BaseSecurityDomain baseSecurityDomain:data){
				AppStealLeakValue appStealLeakValue=new AppStealLeakValue();
				List<QueryCondition> queryConditions=new ArrayList<>();
				queryConditions.add(QueryCondition.eq("appId",baseSecurityDomain.getId()));
				queryConditions.add(QueryCondition.eq("type",1));
				List<AppStealLeakValue> all1 = appStealLeakValueService.findAll(queryConditions);
				if (all1.size()>0){
					appStealLeakValue.setId(all1.get(0).getId());
				}
				appStealLeakValue.setCreateTime(date);
				appStealLeakValue.setType(1);
				appStealLeakValue.setAppId(baseSecurityDomain.getId());
				List<String> ips=getIpsByAppMainten(baseSecurityDomain);
                if (ips!=null&&ips.size()>0){
                Integer stealLeakValue=culAgeValue(ips);
					appStealLeakValue.setStealLeakValue(stealLeakValue);
				}else {
					appStealLeakValue.setStealLeakValue(0);
				}
				appStealLeakValues.add(appStealLeakValue);
			}
			appStealLeakValueService.save(appStealLeakValues);
		}

	}

	@Override
	public void culAppStealLeakValue(Date date) {
		List<AppStealLeakValue> appStealLeakValues=new ArrayList<>();

		List<AppSysManager> all = appSysManagerService.findAll();
		if (all.size()>0){
			for (AppSysManager appSysManager:all){
				AppStealLeakValue appStealLeakValue=new AppStealLeakValue();
				List<QueryCondition> queryConditions=new ArrayList<>();
				queryConditions.add(QueryCondition.eq("appId",appSysManager.getId()));
				queryConditions.add(QueryCondition.eq("type",0));
				List<AppStealLeakValue> all1 = appStealLeakValueService.findAll(queryConditions);
				if (all1.size()>0){
					appStealLeakValue.setId(all1.get(0).getId());
				}
				appStealLeakValue.setCreateTime(date);
				appStealLeakValue.setType(0);
				appStealLeakValue.setAppId(appSysManager.getId());
				List<String> ips=getIpsByApp(appSysManager);
				if (ips!=null&&ips.size()>0){
					Integer stealLeakValue=culAgeValue(ips);
					appStealLeakValue.setStealLeakValue(stealLeakValue);
				}else {
					appStealLeakValue.setStealLeakValue(0);
				}
				appStealLeakValues.add(appStealLeakValue);
			}
			appStealLeakValueService.save(appStealLeakValues);
		}

	}

	@Override
	public Result<String> addAssetOfConcern(AssetSearchVO assetSearchVO) {
		User currentUser = SessionUtil.getCurrentUser();
		if (currentUser==null){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"用户未登录");
		}
//		User currentUser = new User();
//		currentUser.setId(33);
		List<QueryCondition> list=new ArrayList<>();
		list.add(QueryCondition.eq("ip",assetSearchVO.getIp()));
		list.add(QueryCondition.eq("userId",currentUser.getId()));
		list.add(QueryCondition.eq("type",assetSearchVO.getType()));
		List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(list);
		if (findAll.size()==0){
			SelfConcernAsset item = new SelfConcernAsset();
			item.setGuid(UUIDUtils.get32UUID());
			item.setIp(assetSearchVO.getIp());
			item.setUserId(Integer.toString(currentUser.getId()));
			item.setType(Integer.valueOf(assetSearchVO.getType()));
			selfConcernAssetService.save(item);
		}
		return ResultUtil.success("success");
	}

	/**
	 * 根据ip获取二级资产类型treeCode及图标
	 * @param ips
	 * @return  2023-08-04
	 */
	@Override
	public Result<List<Map<String, Object>>> getAssetTypeAndIcon(List<String> ips) {

		return ResultUtil.successList(assetDao.getAssetTypeAndIcon(ips));
	}

	/**
	 * 审批类型功能 202308
	 *
	 * 资产查询接口(终端、运维终端、网络设备、服务器、安全保密设备)
	 * 网络设备	NetworkDevice
	 * 服务器	service
	 * 安全保密设备	SafeDevice
	 * 终端	assetHost
	 * 运维终端	maintenHost
	 * @return
	 */
	@Override
	public Result<List<Map<String, Object>>> getAssetMsg(String code) {

		return ResultUtil.successList(assetDao.getAssetMsg(code));
	}

	/**
	 * 审批类型功能 202308
	 * USB查询接口(USB存储、USB外设设备)
	 * USB存储	USBMemory
	 * USB外设设备	USBPeripheral
	 * @return
	 */
	@Override
	public Result<List<Map<String, Object>>> getUsb(String code) {
		return ResultUtil.successList(assetDao.getUsb(code));
	}

	@Override
	public Result<String> delAssetOfConcern(AssetSearchVO assetSearchVO) {
		User currentUser = SessionUtil.getCurrentUser();
		if (currentUser==null){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"用户未登录");
		}
//		User currentUser = new User();
//		currentUser.setId(33);
		List<QueryCondition> list=new ArrayList<>();
		list.add(QueryCondition.eq("ip",assetSearchVO.getIp()));
		list.add(QueryCondition.eq("userId",currentUser.getId()));
		list.add(QueryCondition.eq("type",assetSearchVO.getType()));
		List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(list);
		if (findAll.size()>0){
			SelfConcernAsset selfConcernAsset = findAll.get(0);
			selfConcernAssetService.delete(selfConcernAsset.getGuid());
		}
		return ResultUtil.success("success");
	}

	private Integer culAgeValue(List<String> ips) {
		List<QueryCondition> queryConditions=new ArrayList<>();
		queryConditions.add(QueryCondition.in("ip",ips));
		List<AssetStealLeakValue> stealLeakValueServiceAll = assetStealLeakValueService.findAll(queryConditions);
		if (stealLeakValueServiceAll.size()>0){
			int sum = stealLeakValueServiceAll.stream().mapToInt(AssetStealLeakValue::getStealLeakValue).sum();
			int i = new BigDecimal(sum).divide(new BigDecimal(stealLeakValueServiceAll.size()), 1).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			return i;
		}
		return 0;
	}

	private List<String> getIpsByApp(AppSysManager appSysManager) {
		if (StringUtils.isNotBlank(appSysManager.getServiceId())){
			String[] split = appSysManager.getServiceId().split(",");
			List<QueryCondition> queryConditions=new ArrayList<>();
			queryConditions.add(QueryCondition.in("guid",Arrays.asList(split)));
			List<Asset> assets = findAll(queryConditions);
			if (assets.size()>0){
				List<String> strings = assets.stream().map(a -> a.getIp()).collect(Collectors.toList());
				return strings;
			}
		}
		return null;
	}

	private List<String> getIpsByAppMainten(BaseSecurityDomain baseSecurityDomain) {
		List<String> ips=new ArrayList<>();
		IpRangeVO ipRangeVO=new IpRangeVO();
		ipRangeVO.setCode(baseSecurityDomain.getCode());
		ResultObjVO<List<IpRangeVO>> listResultObjVO = adminFeign.bySecruityCode(ipRangeVO);
		List<IpRangeVO> data = listResultObjVO.getData();
		if (data!=null&&data.size()>0){
		 for (IpRangeVO ipRangeVO1:data){
			 List<QueryCondition> queryConditions=new ArrayList<>();
			 queryConditions.add(QueryCondition.between("ipNum",ipRangeVO1.getStartIpValue(),ipRangeVO1.getEndIpValue()));
			 List<AssetStealLeakValue> stealLeakValueServiceAll = assetStealLeakValueService.findAll(queryConditions);
			 if (stealLeakValueServiceAll.size()>0){
				 List<String> strings = stealLeakValueServiceAll.stream().map(p -> p.getIp()).collect(Collectors.toList());
				 ips.addAll(strings);
			 }
		 }
		}
        return ips;
	}

	private Integer culEventValue(Map<String, Long> value) {
		Integer aLong1 = value.get("1")==null?0:value.get("1").intValue();
		Integer aLong2 = value.get("2")==null?0:value.get("2").intValue();
		Integer aLong3 = value.get("3")==null?0:value.get("3").intValue();
		Integer aLong4 = value.get("4")==null?0:value.get("4").intValue();
		Integer aLong5 = value.get("5")==null?0:value.get("5").intValue();
		Integer a =aLong5*5+aLong4*4+aLong3*3+aLong2*2+aLong1*1;
		Integer b =aLong5+aLong4+aLong3+aLong2+aLong1;
		if (b>0){
			int i = new BigDecimal(a).divide(new BigDecimal(b), 0).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			return i;
		}
		return 0;
	}

	private Integer culequipmentIntensiveValue(String equipmentIntensive) {
		Integer value=1;
		switch(equipmentIntensive){
			case "0":
				value=5;
				break;
			case "1" :
				value=4;
				break;
			case "2" :
				value=3;
				break;
			case "3" :
				value=2;
				break;
			case "4" :
				value=1;
				break;
			default:
				break;
		}
		return value;
	}

	/**
	 * 终端类型获取未安装列表数据
	 *
	 * 2023-09-26
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getUnInstallList() {
		return assetDao.getUnInstallList();
	}

}
