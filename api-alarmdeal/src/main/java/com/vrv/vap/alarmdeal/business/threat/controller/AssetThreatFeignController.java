package com.vrv.vap.alarmdeal.business.threat.controller;

import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月28日 下午4:51:05 
* 类说明  资产威胁Feign接口调用
*/
@RestController
@RequestMapping(value="/assetThreatFeign")
public class AssetThreatFeignController {

	private static final String ALL = "all";

	@Autowired
	private AssetService assetService;

	@GetMapping(value="/getAssetType/{id}")
	@ApiOperation(value="根据资产ID获得资产类型",notes="")
	public Result<String> getAssetType(@PathVariable String id){
		Asset asset = assetService.getOne(id);
		if(asset==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "找不到该资产");
		}
		String assetType = asset.getAssetType();
		return ResultUtil.success(assetType);
	}

	
	@PostMapping(value="/getAssetListByAssetType")
	@ApiOperation(value="根据资产类型获得对应的资产信息",notes="")
	public Result<List<Asset>> getAssetListByThreat(@RequestBody Map<String,Object> map){
		Object asset_type_info_obj = map.get("asset_type_info");   //获得资产
		if(asset_type_info_obj!=null){
			String asset_type_info = asset_type_info_obj.toString();
			if(asset_type_info.equals(ALL)) {
				List<Asset> all = assetService.findAll();
				return ResultUtil.success(all);
			}else{
				List<QueryCondition> conditions = new ArrayList<>();
				conditions.add(QueryCondition.in("assetType", asset_type_info.split(",")));
				List<Asset> list = assetService.findAll(conditions);
				return  ResultUtil.success(list);
			}
		}
		return ResultUtil.success(new ArrayList<>());
	}
	
	
	@PostMapping(value="/getAssetInfoByGuid")
	@ApiOperation(value="根据资产guid获得资产实体信息",notes="")
	public Result<Asset> getAssetInfoByGuid(@RequestBody Map<String,Object> map){
		String asset_guid = map.get("asset_guid").toString();   //获得资产guid
		Asset asset = assetService.getOne(asset_guid);
		return ResultUtil.success(asset);
	}
	
	@PostMapping(value="/getAssetInfoByIp")
	@ApiOperation(value="根据资产Ip获得资产实体信息",notes="")
	public Result<Asset> getAssetInfoByIp(@RequestBody Map<String,Object> map){
		String asset_ip = map.get("asset_ip").toString();   //获得资产guid
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ip", asset_ip));
		List<Asset> list = assetService.findAll(conditions);
		if(!list.isEmpty()){
			Asset asset = list.get(0);
			return ResultUtil.success(asset);
		}else {
			return ResultUtil.success(null);
		}
	}

	@PostMapping(value="/getAssetInfoByIps")
	@ApiOperation(value="根据资产Ip获得资产实体信息",notes="")
	public Result<List<Asset>> getAssetInfoByIps(@RequestBody Map<String,Object> map){
		List<String> asset_ip = new ArrayList<>();
		Object obj = map.get("ips");
		if (obj instanceof ArrayList<?>) {
			for (Object o : (List<?>) obj) {
				asset_ip.add(String.class.cast(o));
			}
		}
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.in("ip", asset_ip));
		List<Asset> list = assetService.findAll(conditions);
		return ResultUtil.successList(list);
	}
	

	@PostMapping(value="/getAffectedAssetStatistics/{domainGuid}")
	@ApiOperation(value="受影响资产统计情况",notes="返回结果  percentage、total、count")
	public Result<Map<String,String>> getAffectedAssetStatistics(@RequestBody String[] ids,@PathVariable("domainGuid")  @ApiParam(name="domainGuid",value="安全域guid，为空时查询所有",required=false)   String  domainGuid){
		
		List<QueryCondition>  querys=new ArrayList<>();
		if(!StringUtils.isEmpty(domainGuid)&&!domainGuid.equals("0")) {
			querys.add(QueryCondition.eq("securityGuid", domainGuid));
		}
		List<Asset> findAll = assetService.findAll(querys);
		Map<String,String> result=new HashMap<>();
		int total=findAll.size();
		int count=0;
		if(ids!=null&&ids.length>0) {
		for(Asset asset  : findAll ) {
			for(String  idsIp : ids) {
				if(idsIp.equals(asset.getIp())) {
					count++;
					break;
				}
			}
		}
		}
		double percentage=0;
		if(total>0) {
			percentage = (double) Math.round((count*100.00/total) * 100) / 100;
		}
		result.put("total",Integer.toString(total) );
		result.put("count",Integer.toString(count) );
		result.put("percentage",Double.toString(percentage) );
		return ResultUtil.success(result);
	}
	
}
