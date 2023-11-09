package com.vrv.vap.alarmdeal.business.asset.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.enums.AssetLabelEnum;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetIpByAssetGroupVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetSearchNewVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetTypeByIpVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetWorthVO;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.util.AssetDomainCodeUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.*;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.common.FileHeaderUtil;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/asset")
public class AssetController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AssetController.class);
			
	@Autowired
	private AssetService assetService;
	
	@Autowired
	private AdminFeign adminFeign;
	
	@Autowired
	private AssetExtendService assetExtendService;
	@Autowired
	private AssetExportAndImportService assetExportAndImportService;

	@Autowired
	private AssetBaseDataService assetBaseDataService;
	@Autowired
	private MapperUtil mapper;

	@Autowired
	private AssetImportService assetImportService;

	@Autowired
	private MessageService messageService;
	@Autowired
	private FileConfiguration fileConfiguration;
	@Autowired
	private SelfConcernAssetService selfConcernAssetService;
	/**
	 * 获得资产列表
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/getAssetPager")
	@ApiOperation(value="获得资产分页列表（自带权限）",notes="")
	@SysRequestLog(description="获得资产分页列表（自带权限）", actionType = ActionType.SELECT,manually=false)
	public  PageRes<AssetVO> getAssetInfoPager(@RequestBody AssetSearchVO assetSearchVO){
		if(assetSearchVO==null) {
			assetSearchVO=new  AssetSearchVO();
		}
		if(assetSearchVO.getCount_()==null||assetSearchVO.getCount_()<0) {
			assetSearchVO.setCount_(10);
		}
		
		if(assetSearchVO.getStart_()==null||assetSearchVO.getStart_()<0) {
			assetSearchVO.setStart_(0);
		}
		PageReq pageReq=new PageReq();
		pageReq.setCount(assetSearchVO.getCount_());
		pageReq.setBy("desc");
		pageReq.setOrder("createTime");
		pageReq.setStart(assetSearchVO.getStart_());
		PageRes<AssetVO> pageRes = assetService.getAssetInfoPager(assetSearchVO, pageReq.getPageable());
		return pageRes;
	}
	/**
	 * 导出新标准画像资产文件
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/exportNewAssetInfo")
	@ApiOperation(value="生成资产导出文件",notes="")
	@SysRequestLog(description="生成资产导出文件", actionType = ActionType.EXPORT,manually=false)
	public  Result<String> exportNewAssetInfo(@RequestBody AssetSearchVO assetSearchVO){
		return assetService.exportNewAssetInfo(assetSearchVO);
	}
	/**
	 * 添加关注资产
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/addAssetOfConcern")
	@ApiOperation(value="添加关注资产",notes="")
	@SysRequestLog(description="添加关注资产", actionType = ActionType.EXPORT,manually=false)
	public  Result<String> addAssetOfConcern(@RequestBody AssetSearchVO assetSearchVO){
		return assetService.addAssetOfConcern(assetSearchVO);
	}
	/**
	 * 取消关注资产
	 * @param assetSearchVO
	 * @return
//	 */
	@PostMapping(value="/delAssetOfConcern")
//	@ApiOperation(value="取消关注资产",notes="")
	@SysRequestLog(description="取消关注资产", actionType = ActionType.EXPORT,manually=false)
	public  Result<String> delAssetOfConcern(@RequestBody AssetSearchVO assetSearchVO){
		return assetService.delAssetOfConcern(assetSearchVO);
	}
	/**
	 * 下载文件
	 * @param response
	 */
	@GetMapping(value="/exportFile/{fileName}")
	@ApiOperation(value="资产文件下载",notes="")
	@SysRequestLog(description="资产文件下载", actionType = ActionType.EXPORT,manually=false)
	public void exportFile(@PathVariable  String fileName, HttpServletResponse response){
		// 文件路径
		String realPath = fileConfiguration.getAsset();
		FileUtil.downLoadFile(fileName+".xls", realPath, response);
	}
	/**
	 * 新标准画像获得资产列表
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/getAssetImagePager")
	@ApiOperation(value="新标准画像获得资产列表（自带权限）",notes="")
	@SysRequestLog(description="新标准画像获得资产列表（自带权限）", actionType = ActionType.SELECT,manually=false)
	public  PageRes<AssetVO> getAssetImagePager(@RequestBody AssetSearchVO assetSearchVO){
		if(assetSearchVO==null) {
			assetSearchVO=new  AssetSearchVO();
		}
		if(assetSearchVO.getCount_()==null||assetSearchVO.getCount_()<0) {
			assetSearchVO.setCount_(10);
		}

		if(assetSearchVO.getStart_()==null||assetSearchVO.getStart_()<0) {
			assetSearchVO.setStart_(0);
		}
		PageReq pageReq=new PageReq();
		pageReq.setCount(assetSearchVO.getCount_());
		pageReq.setBy("desc");
		pageReq.setOrder("createTime");
		pageReq.setStart(assetSearchVO.getStart_());
		PageRes<AssetVO> pageRes = assetService.getAssetImagePager(assetSearchVO, pageReq.getPageable());
		return pageRes;
	}
	
	@PostMapping(value="/getAssetPager/{treeCode}")
	@SysRequestLog(description="通过资产类型tree获得资产分页列表", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="通过资产类型tree获得资产分页列表",notes="")
	public  PageRes<AssetVO> getAssetInfoPager(@PathVariable String treeCode,  @RequestBody PageReq pageReq){
		pageReq.setStart(pageReq.getStart());
		return assetService.getAssetInfoPager(treeCode, pageReq.getPageable());
	}
	
	
	/**
	 * 获得标签数据
	 * @return
	 */
	@GetMapping(value="/getAllTopTagsType")
	@SysRequestLog(description="获得资产标签数据", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获得标签数据",notes="")
	public Result<List<String>> getAllTopTagsType(){
		List<String> list = assetService.getAllTopTagsType();
		return ResultUtil.success(list);
	}
	@GetMapping(value="/getAllTopTagsCount")
	@SysRequestLog(description="获得资产标签数量统计", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获得标签数据",notes="")
	public Result<Map<String, Integer>> getAllTopTagsCount(){ 
		Map<String, Integer> allTopTagsCount = assetService.getAllTopTagsCount();

		return ResultUtil.success(allTopTagsCount);
	} 

	
	/**
	 * 验证IP类型
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/validateAssetIp")
	@SysRequestLog(description="验证资产IP", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="验证资产IP",notes="")
	public Result<Boolean> validateAssetIp(@RequestBody AssetSearchVO assetSearchVO){
		String ip = assetSearchVO.getIp();
		if(StringUtils.isEmpty(ip)) {
			return ResultUtil.success(true);
		}
		Result<Boolean> result = assetService.validateAssetIp(assetSearchVO);
		return result;
		
	}
	
	/**
	 * 保存资产
	 * @param assetVO
	 * @return
	 */
	@PostMapping(value="/saveAddAsset")
	@ApiOperation(value="保存资产",notes="")
	@SysRequestLog(description="保存资产", actionType = ActionType.ADD,manually=false)
	public Result<String> saveAddAsset(@RequestBody AssetVO assetVO){
		Result<String> result = assetService.saveAddAsset(assetVO);
		// 数据变化发消息 2022-06-01
		messageService.sendKafkaMsg("asset");
		return result;
		
	}
	
	/**
	 * 编辑资产
	 * @param assetVO
	 * @return
	 */
	@PostMapping(value="/saveEditdAsset")
	@ApiOperation(value="编辑资产",notes="")
	@SysRequestLog(description="编辑资产", actionType = ActionType.UPDATE,manually=false)
	public Result<String> saveEditdAsset(@RequestBody AssetVO assetVO){
		Result<String> result = assetService.saveEditAsset(assetVO);
		// 数据变化发消息 2022-06-01
		messageService.sendKafkaMsg("asset");
		return result;
		
	}
	
	/**
	 * 删除资产
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/delAsset/{guid}")
	@ApiOperation(value="删除资产",notes="")
	@SysRequestLog(description="删除资产", actionType = ActionType.DELETE,manually=false)
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产guid",required=true,dataType="Integer"))
	public Result<Boolean> delAsset(@PathVariable @ApiParam("资产主键ID") String guid) {
		try {
			Result<Boolean> result = assetService.deleteAsset(guid);
			// 数据变化发消息 2022-06-01
			messageService.sendKafkaMsg("asset");
			return  result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultCodeEnum.UNKNOW_FAILED.getMsg());
		}
	}
	/**
	 * 批量删除资产:{"guids": ["185da064deff49069a388db656dd19f8","53034bbb8a6d4367bf7db5c7003ef1e0"]}
	 * @param map
	 * @return  2023-08-07
	 */
	@PostMapping(value="/batchDeleteAsset")
	@ApiOperation(value="批量删除资产")
	@SysRequestLog(description="批量删除资产", actionType = ActionType.DELETE,manually=false)
	public Result<Boolean> batchDeleteAsset(@RequestBody Map<String,List<String>> map){
		try {
			List<String> guids = map.get("guids");
			if(CollectionUtils.isEmpty(guids)){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有删除的guid！");
			}
			Result<Boolean> result = assetService.batchDeleteAsset(guids);
			// 数据变化发消息
			messageService.sendKafkaMsg("asset");
			return  result;
		} catch (Exception e) {
			logger.error("批量删除资产:{}",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultCodeEnum.UNKNOW_FAILED.getMsg());
		}
	}

	
	/**
	 * 获得所有的资产
	 * @return
	 */
	@GetMapping(value="/getAssets")
	@SysRequestLog(description="获得所有的资产", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获得所有的资产",notes="")
	public Result<List<AssetVO>> getAssets(){
		List<Asset> list = null;
		List<String> userDomainCodes =  AssetDomainCodeUtil.getUserAuthorityDomainCodes();
		if(userDomainCodes!=null&&!userDomainCodes.isEmpty()) {
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.in("securityGuid", userDomainCodes));
			list=assetService.findAll(conditions);
		}else {
			list=assetService.findAll();
		}
		List<AssetVO> result = new ArrayList<>();
		List<BaseSecurityDomain> domains = assetBaseDataService.queryAllDomain();
		for (Asset asset : list){
			AssetVO vo = assetService.mapperVO(asset,domains);
			result.add(vo);
		}
		return ResultUtil.success(result);
	}
	/**
	 * 导出文件
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/exportAssetInfo")
	@ApiOperation(value="生成资产导出文件",notes="")
	@SysRequestLog(description="生成资产导出文件", actionType = ActionType.EXPORT,manually=false)
	public  Result<String> exportAssetInfo(@RequestBody AssetSearchVO assetSearchVO){
		return assetExportAndImportService.exportAssetInfo(assetSearchVO,assetSearchVO.getSelectIds());
	}
	
	@PostMapping(value="/exportAssetInfoTemplate")
	@SysRequestLog(description="生成资产模板导出文件", actionType = ActionType.EXPORT,manually=false)
	@ApiOperation(value="生成资产模板导出文件",notes="")
	public  Result<String> exportAssetInfoTemplate(@RequestBody SelectIds selectIds){
		return assetExportAndImportService.exportAssetInfoTemplate(selectIds.getSelectIds());
	}
	
	/**
	 * 下载文件
	 * @param response
	 */
	@GetMapping(value="/exportAssetFile/{fileName}")
	@SysRequestLog(description="下载资产导出文件", actionType = ActionType.EXPORT,manually=false)
	@ApiOperation(value="下载资产导出文件",notes="")
	public void exportAssetInfo(@PathVariable  String fileName, HttpServletResponse response){
		assetExportAndImportService.exportAssetFile(fileName,response);
	}
	
	
	/**
	 * 获得导入文件信息
	 * @param file
	 * @return
	 */
	@PostMapping(value="/getImportAssetFile")
	@SysRequestLog(description="获得资产导入文件信息", actionType = ActionType.IMPORT,manually=false)
	@ApiOperation(value="获得资产导入文件信息",notes="")
	//改过CommonsMultipartFile，影响了功能
	public Result<Map<String, List<Map<String, Object>>>> importAssetFile(@RequestParam("file") MultipartFile file) {
		try {
			Map<String, List<Map<String, Object>>> map = assetImportService.parseImportAssetInfo(file);
			if(null == map){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "导入文件解析异常");
			}
			return ResultUtil.success(map);
		} catch (Exception e) {
			logger.error("获得导入文件信息异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "导入文件解析异常");
		}

	}
	/**
	 * 保存资产信息
	 * @param list
	 * @return
	 */
	@PostMapping(value="/saveImportAssets")
	@SysRequestLog(description="保存导入的资产信息", actionType = ActionType.IMPORT,manually=false)
	@ApiOperation(value="保存导入的资产信息",notes="")
	public  Result<Boolean> saveImportAssets(@RequestBody List<Map<String,Object>> list){
		Result<Boolean> result = assetExportAndImportService.importAssetFile(list);
		// 数据变化发消息 2022-06-01
		messageService.sendKafkaMsg("asset");
		return result;
	}
	
	/**
	 * 获得资产额外信息
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAssetExtend/{guid}")
	@SysRequestLog(description="获得资产额外信息", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获得资产额外信息",notes="") 
	public Result<AssetExtend> getAssetExtend(@PathVariable("guid") String guid) {
		AssetExtend one = assetExtendService.getOne(guid);
		return ResultUtil.success(one);
	}
	
	/**
	 * 获得单个资产信息
	 * @param guid
	 * @return
	 */
	@GetMapping(value="/getSingleAsset/{guid}")
	@SysRequestLog(description="获得单个资产信息", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获得单个资产信息",notes="") 
	public Result<AssetVO> getSingleAsset(@PathVariable("guid") String guid){
		return assetService.getSingleAsset(guid);
	}
	/**
	 * 获得单个资产信息
	 * @return
	 */
	@PostMapping (value="/getSingleAssetInfo")
	@SysRequestLog(description="获得单个资产信息", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获得单个资产信息",notes="")
	public Result<AssetVO> getSingleAssetInfo(@RequestBody Map<String,String> map){
		String guid = map.get("guid");
		if (StringUtils.isBlank(guid)){
			String ip = map.get("ip");
			if (StringUtils.isNotBlank(ip)){
				List<QueryCondition> queryConditions=new ArrayList<>();
				queryConditions.add(QueryCondition.eq("ip",ip));
				List<Asset> assetServiceAll = assetService.findAll(queryConditions);
				if (assetServiceAll.size()>0){
					guid=assetServiceAll.get(0).getGuid();
				}
			}
		}
		if (StringUtils.isBlank(guid)){
			Result result=new Result();
			result.setCode(ResultCodeEnum.SUCCESS.getCode());
			result.setMsg("资产不存在");
			return  result;
		}
		Result<AssetVO> singleAsset = assetService.getSingleAsset(guid);
		AssetVO data = singleAsset.getData();
		if (data==null){
			return singleAsset;
		}
		List<BaseSecurityDomain> baseSecurityDomains = assetBaseDataService.queryAllDomain();
		if (StringUtils.isNotBlank(data.getSecurityGuid())){
			List<BaseSecurityDomain> collect = baseSecurityDomains.stream().filter(a -> a.getCode().equals(data.getSecurityGuid())).collect(Collectors.toList());
			if (collect.size()>0){
				BaseSecurityDomain baseSecurityDomain = collect.get(0);
				data.setSecurityName(baseSecurityDomain.getDomainName());
			}
		}
		VData<List<User>> allUser = adminFeign.getAllUser();
		List<User> userData = allUser.getData();
		VData<List<BasePersonZjg>> allPerson = adminFeign.getAllPerson();
		List<BasePersonZjg> allPersonData = allPerson.getData();
		if (userData.size()>0&&allPersonData.size()>0){
			List<BasePersonZjg> collect = allPersonData.stream().filter(a -> a.getUserNo().equals(data.getResponsibleCode())).collect(Collectors.toList());
			if (collect.size()>0){
				BasePersonZjg basePersonZjg = collect.get(0);
				for (User user:userData) {
					if (user.getPersonId()!=null){
						if (user.getPersonId().equals(basePersonZjg.getId())){
							data.setUserName(basePersonZjg.getPersonRank());
							data.setAccount(user.getAccount());
							break;
						}
					}
				}
			}
		}
		//查询是否关注
		com.vrv.vap.common.model.User currentUser = SessionUtil.getCurrentUser();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("userId", currentUser.getId()));
		conditions.add(QueryCondition.eq("ip",data.getIp()));
		conditions.add(QueryCondition.eq("type",0));
		List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(conditions);
		if (findAll.size()>0){
			data.setIsJustAssetOfConcern(true);
		}
		return singleAsset;
	}
	@GetMapping(value="/getAssetDetail/{guid}")
	@SysRequestLog(description="获得单个资产信息", actionType = ActionType.SELECT,manually=false)
	@ApiOperation(value="获取资产信息",notes="")
	public Result<AssetDetailVO> getAssetDetail(@PathVariable("guid") String guid){
		return ResultUtil.success(assetService.getAssetDetail(guid));
	}

	/**
	 * 按照任意列分组统计数值
	 * @param columnName
	 * @return
	 */
	@GetMapping(value="/getAssetCountByAnyColumn/{columnName}")
	@ApiOperation(value="按照任意列分组统计数值",notes="")
	@SysRequestLog(description="按照任意列分组统计数值", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAssetCountByAnyColumn(@PathVariable  @ApiParam(name="columnName",value="列名",required=true)  String columnName){
		return ResultUtil.success(assetService.getAssetCountByAnyColumn(columnName));
	}
	
	
	@GetMapping(value="/getAssetCountByDomain")
	@ApiOperation(value="安全域分组",notes="")
	@SysRequestLog(description="安全域分组", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAssetCountByDomain(){
		

		List<BaseSecurityDomain> allDomain = assetBaseDataService.queryAllDomain();
		 
		if(allDomain==null) {
			allDomain=new ArrayList<>();
		}
 
		List<Map<String, Object>> assetCountByAnyColumn = assetService.getAssetCountByAnyColumn("ifnull(`securityGuid`,'')");
		
		for(Map<String, Object>  map : assetCountByAnyColumn) {
			if(StringUtils.isEmpty( map.getOrDefault("key","").toString())) {
				map.put("DomainGuid", "isNull");
				map.put("DomainName", "未知");
			}else {
				String guid = FileHeaderUtil.checkFileHeader(map.get("key").toString());
				map.put("DomainGuid", FileHeaderUtil.checkFileHeader(guid));
				map.put("DomainName", "未知"+guid);
				for(BaseSecurityDomain domain : allDomain) {
					if(domain.getCode().equals(guid)  ) {
						map.put("DomainName", domain.getDomainName());
						break;
					}
				}
			}
			
		}
		return ResultUtil.success(assetCountByAnyColumn);
	}
	
	
	/**
	 * 获取资产数据统计 按照资产类型（大类分组）
	 * @return
	 */
	@GetMapping(value="/getAssetCountByAssetType")
	@ApiOperation(value="获取资产数据统计 按照资产类型（大类分组）",notes="")
	@SysRequestLog(description="获取资产数据统计 按照资产类型（大类分组）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAssetCountByAssetType(){
		return ResultUtil.success(assetService.getAssetCountByAssetType());
	}
	
	
	@GetMapping(value="/getAssetCreateCountByTime/{timeType}")
	@ApiOperation(value="按照统计时间周期范围内的资产创建情况",notes="")
	@SysRequestLog(description="按照统计时间周期范围内的资产创建情况", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAssetCreateCountByTime(@PathVariable  @ApiParam(name="timeType",value="时间类型  week、month、year",required=true) String timeType){
		return ResultUtil.success(assetService.getAssetCreateCountByTime(timeType));
	}
	
	
	@GetMapping(value="/getAssetCount")
	@ApiOperation(value="获取资产数量",notes="")
	@SysRequestLog(description="获取资产数量", actionType = ActionType.SELECT,manually=false)
	public Result<Long> getAssetCount(){
		return ResultUtil.success(assetService.count());
	}
	
	@PostMapping(value="/getAssetCount")
	@ApiOperation(value="获取资产数量(自带权限)",notes="")
	@SysRequestLog(description="获取资产数量(自带权限)", actionType = ActionType.SELECT,manually=false)
	public Result<Long> getAssetCount(@RequestBody AssetSearchVO assetSearchVO){
		List<QueryCondition> conditions = assetService.searchAssetCondition(assetSearchVO);
		if(SessionUtil.getCurrentUser()!=null&& SessionUtil.getauthorityType()) {
			List<String> userDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
			if(userDomainCodes==null||userDomainCodes.isEmpty()) {
				conditions.add(QueryCondition.eq("securityGuid", "!@#$%^&*"));//使查找不到数据
			}else {
				conditions.add(QueryCondition.in("securityGuid", userDomainCodes));
			}
		}
		return ResultUtil.success(assetService.count());
	}
	
	
	@GetMapping(value="/getOneAssetDetailByIp/{ip:.+}")
	@ApiOperation(value="获取资产信息",notes="")
	@SysRequestLog(description="获取资产信", actionType = ActionType.SELECT,manually=false)
	public Result<AssetDetailVO> getOneAssetDetailByIp(@PathVariable("ip") String ip){
		return ResultUtil.success(assetService.getOneAssetDetailByIp(ip));
	}
	
	@PostMapping(value="/getFailedAssetPager")
	@ApiOperation(value="获得失陷资产分页列表",notes="")
	@SysRequestLog(description="获得失陷资产分页列表", actionType = ActionType.SELECT,manually=false)
	public  PageRes<FailedAssetVO> getFailedAssetPager(@RequestBody AssetSearchVO assetSearchVO){
		 assetSearchVO.setFailedStatus("3");
		 PageRes<AssetVO> assetInfoPager = this.getAssetInfoPager(assetSearchVO);
		 List<AssetVO> list = assetInfoPager.getList();
		 PageRes<FailedAssetVO>  result=new PageRes<>();
		 result.setCode(assetInfoPager.getCode());
		 result.setMessage(assetInfoPager.getMessage());
		 result.setTotal(assetInfoPager.getTotal()); 
		 
		 List<FailedAssetVO> mapList =new ArrayList<>();
		if(list!=null&&list.size()>0) {
			for(AssetVO assetvo : list) {
				FailedAssetVO failedvo=mapper.map(assetvo, FailedAssetVO.class);
				List<LabelStatusVO> assetLabelStatus = assetvo.getAssetLabelStatus();
				for(LabelStatusVO label : assetLabelStatus) {
					if( AssetLabelEnum.FAILED_STATUS.getLabelName().equals(label.getLabelName())) {
						failedvo.setFailedStatus(label.getLabelValue());
						failedvo.setUpdateTime(label.getUpdateTime());
						break;
					}
				}
				
				//补充安全域
				VData<BaseSecurityDomain> oneDomainByCode = adminFeign.getOneDomainByCode(failedvo.getSecurityGuid());
				if(oneDomainByCode!=null&&"0".equals(oneDomainByCode.getCode())) {
					failedvo.setSecurityName(oneDomainByCode.getData().getDomainName());
				}
				mapList.add(failedvo);
			}
			
		}
		 
		 result.setList(mapList);
		return result;
	}
	
	@GetMapping(value="/getAssetAuthStatus/{guid}")
	@ApiOperation(value="获取资产权限状态",notes="true 有权限  false 无权限")
	@SysRequestLog(description="获取资产权限状态", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> getAssetAuthStatus(@PathVariable("guid") String guid){
		 
		Asset one = assetService.getOne(guid);
		if(one!=null) {
			List<String> userAuthorityDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
			if(userAuthorityDomainCodes==null) {
				return ResultUtil.success(true);
			}else {
				if(userAuthorityDomainCodes.contains(one.getSecurityGuid())) {
					return ResultUtil.success(true);
				}
			}
		}
		return ResultUtil.success(false);
	}

	/**
	 * 根据ip获取二级资产类型treeCode及图标
	 * @param map {"ips": ["192.168.121.108","192.168.120.102"]}
	 * @return  2023-08-04
	 */
	@PostMapping(value="/getAssetTypeAndIcon")
	@ApiOperation(value="根据ip获取二级资产类型treeCode及图标")
	@SysRequestLog(description="根据ip获取二级资产类型treeCode及图标", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getAssetTypeAndIcon(@RequestBody Map<String,List<String>> map){
         try{
			 List<String> ips = map.get("ips");
			 if(CollectionUtils.isEmpty(ips)){
				 return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"参数不能为空");
			 }
			 return assetService.getAssetTypeAndIcon(ips);
		 }catch (Exception e){
            logger.error("根据ip获取二级资产类型guid及图标异常",e);
			 return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"根据ip获取二级资产类型guid及图标异常");
		 }
	}

	//-----------------------关保相关的功能-----------------//
	/**
	 * 获取ip对应的资产类型：多个ip
	 * @param ips
	 * @return result
	 */
	@PostMapping(value="/getAssetTypeByIps")
	@ApiOperation(value="获取ip对应的资产类型(多个ip)",notes="")
	@SysRequestLog(description="获取ip对应的资产类型(多个ip)", actionType = ActionType.SELECT,manually=false)
	public  Result<List<AssetTypeByIpVO>> getAssetTypeByIps(@RequestBody List<String> ips){
		try{
			if(CollectionUtils.isEmpty(ips)){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip必传");
			}
			logger.info("获取ip对应的资产类型,ips为："+ JSON.toJSONString(ips));
			return assetService.getAssetTypeByIps(ips);
		}catch (Exception e){
			logger.error("获取ip对应的资产类型异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取ip对应的资产类型异常");
		}
	}

	/**
	 * 获取ip对应的资产类型：单个ip
	 * @return result
	 */
	@PostMapping(value="/getAssetTypeByIp")
	@ApiOperation(value="获取ip对应的资产类型(单个ip)",notes="")
	@SysRequestLog(description="获取ip对应的资产类型(单个ip)", actionType = ActionType.SELECT,manually=false)
	public  Result<AssetType> getAssetTypeByIp(@RequestBody AssetWorthVO assetWorthVO){

		try{
			if(null == assetWorthVO || StringUtils.isEmpty(assetWorthVO.getIp())){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip必传");
			}
			logger.info("获取ip对应的资产类型,ip为："+assetWorthVO.getIp());
			return assetService.getAssetTypeByIp(assetWorthVO.getIp());
		}catch (Exception e){
			logger.error("获取ip对应的资产类型异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取ip对应的资产类型异常");
		}
	}
	/**
	 * 查询全部资产
	 * @return result
	 */
	@GetMapping(value="/getAllAsset")
	@ApiOperation(value="查询全部资产",notes="")
	@SysRequestLog(description="查询全部资产", actionType = ActionType.SELECT,manually=false)
	public  Result<List<AssetWorthVO>> getAllAsset(){
		try{
			return assetService.getAllAsset();
		}catch (Exception e){
			logger.error("查询全部资产",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"查询全部资产异常");
		}
	}

	/**
	 * 资产权重：
	 * 所有资产价值总和
	 * @return result
	 */
	@GetMapping(value="/getAssetWeight")
	@ApiOperation(value="所有资产价值总和",notes="")
	@SysRequestLog(description="所有资产价值总和", actionType = ActionType.SELECT,manually=false)
	public  Result<Long> getAssetWeight(){
		try{
			return ResultUtil.success(assetService.getAssetWeight()) ;
		}catch (Exception e){
			logger.error("查询资产权重异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"查询资产权重异常");
		}
	}

	/**
	 * 通过ip获取资产权重：
	 * 所有资产价值总和
	 * @return result  2022-09-13
	 */
	@PostMapping(value="/getAssetWeightByIp")
	@ApiOperation(value="通过ip获取资产权重",notes="")
	@SysRequestLog(description="通过ip获取资产权重", actionType = ActionType.SELECT,manually=false)
	public  Result<Long> getAssetWeightByIp(@RequestBody Map<String,List<String>> param){
		try{
			List<String> ips = param.get("ips");
			if(CollectionUtils.isEmpty(ips)){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip不能为空");
			}
			return ResultUtil.success(assetService.getAssetWeightByIp(ips)) ;
		}catch (Exception e){
			logger.error("通过ip获取资产权重异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"通过ip获取资产权重异常");
		}
	}

	/**
	 * 服务器、终端、网络设备、安全保密产品对应的资产ip
	 *
	 * @return result  2022-09-14
	 */
	@PostMapping(value="/getIpByGroupType")
	@ApiOperation(value="服务器、终端、网络设备、安全保密产品对应的资产ip",notes="")
	@SysRequestLog(description="服务器、终端、网络设备、安全保密产品对应的资产ip", actionType = ActionType.SELECT,manually=false)
	public  Result<List<AssetIpByAssetGroupVO>> getIpByGroupType(){
		try{
			return ResultUtil.successList(assetService.getIpByGroupType()) ;
		}catch (Exception e){
			logger.error("服务器、终端、网络设备、安全保密产品对应的资产ip异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"服务器、终端、网络设备、安全保密产品对应的资产ip异常");
		}
	}

	/**
	 * 大屏接口：通过ip获取资产中对应责任人
	 *
	 * @return result  2022-09-14
	 */
	@PostMapping(value="/getEmpNameByIp")
	@ApiOperation(value="通过ip获取资产中对应责任人",notes="")
	@SysRequestLog(description="通过ip获取资产中对应责任人", actionType = ActionType.SELECT,manually=false)
	public  Result<String> getEmpNameByIp(@RequestBody Map<String,String> param){
		try{
			String ip = param.get("ip");
			if(StringUtils.isEmpty(ip)){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"ip不能为空");
			}
			return assetService.getEmpNameByIp(ip) ;
		}catch (Exception e){
			logger.error("通过ip获取资产中对应责任人异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"通过ip获取资产中对应责任人异常");
		}
	}

	/**
	 * 获取资产所有IP
	 *
	 * @return result  2022-11-10
	 */
	@GetMapping(value="/getAllAssetIps")
	@ApiOperation(value="获取资产所有IP",notes="")
	@SysRequestLog(description="获取资产所有IP", actionType = ActionType.SELECT,manually=false)
	public  Result<List<String>> getAllAssetIps(){
		try{
			return ResultUtil.successList(assetService.getAllAssetIps());
		}catch (Exception e){
			logger.error("获取台账所有IP异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取台账所有IP异常");
		}
	}
	@PostMapping(value="/getAssetIpByOrg")
	@ApiOperation(value="获取部门下的资产IP",notes="")
	@SysRequestLog(description="获取部门下的资产IP", actionType = ActionType.SELECT,manually=false)
	public  Result<List<String>> getAssetIpByOrg(@RequestBody AssetSearchNewVO assetSearchNewVO){
		try{
			String orgCode = assetSearchNewVO.getOrgCode();
			if(StringUtils.isEmpty(orgCode)){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"部门code不能为空");
			}
			return ResultUtil.successList(assetService.getAssetIpByOrg(orgCode));
		}catch (Exception e){
			logger.error("获取部门下的资产IP异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取部门下的资产IP异常");
		}
	}

	@PostMapping(value="/queryAssets")
	@ApiOperation(value="通过条件查询资产",notes="")
	@SysRequestLog(description="通过条件查询资产", actionType = ActionType.SELECT,manually=false)
	public  Result<List<Asset>> queryAssets(@RequestBody AssetSearchNewVO assetSearchNewVO){
		try{
			return ResultUtil.successList(assetService.queryAssets(assetSearchNewVO));
		}catch (Exception e){
			logger.error("查询资产通过条件异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"查询资产通过条件异常");
		}
	}
	//-----------------------关保相关的功能end-----------------//
}
