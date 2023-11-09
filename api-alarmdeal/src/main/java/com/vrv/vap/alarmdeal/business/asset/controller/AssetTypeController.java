package com.vrv.vap.alarmdeal.business.asset.controller;

import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.util.ResultEnum;
import com.vrv.vap.alarmdeal.business.asset.util.StaticKey;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeVO;
import com.vrv.vap.alarmdeal.business.asset.vo.ValueTextVO;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/assettype")
public class AssetTypeController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AssetTypeController.class);

	@Autowired
	private AssetTypeService assetTypeService;
	
	@Autowired
	private AssetService assetService;
	
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	@Autowired
	private MapperUtil mapper;
	
	/**
	 * 获得资产类型树

	 * @return
	 */
	@GetMapping(value="/getAssetTypeTree")
	@ApiOperation(value="获得资产类型树用于资产类型页签",notes="")
	@SysRequestLog(description="获得资产类型树用于资产类型页签", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeTreeVO>> getAssetTypeTree(){
		List<AssetTypeTreeVO> tree = assetTypeService.getAssetTypeTree(null);
		return ResultUtil.success(tree)  ;
	}
	/**
	 * 获得资产类型树 2021-08-16 资产类型与资产管理树结构分开
	 * @return
	 */
	@GetMapping(value="/getAssetTypeNodeTree")
	@ApiOperation(value="获得资产类型树用于资产管理页签",notes="")
	@SysRequestLog(description="获得资产类型树用于资产管理页签", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeTreeVO>> getAssetTypeNodeTree(){
		List<AssetTypeTreeVO> tree = assetTypeService.getAssetTypeTree("assetType");
		return ResultUtil.success(tree)  ;
	}
	/**
	 * 获得资产类型树根据设置资产偏好作用域配置
	 * @return
	 * 2021-08-09
	 */
	@GetMapping(value="/getAssetTypeTreeByConfigure")
	@ApiOperation(value="获得资产类型树根据设置资产偏好作用域配置",notes="")
	@SysRequestLog(description="获得资产类型树根据设置资产偏好作用域配置", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeTreeVO>> getAssetTypeTreeByConfigure(){
		List<AssetTypeTreeVO> tree = assetTypeService.getAssetTypeTreeByConfigure();
		return ResultUtil.success(tree)  ;
	}
	/**
	 * 获得二级资产类型列表
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/getAssetTypeList")
	@ApiOperation(value=" 获得二级资产类型列表",notes="")
	@SysRequestLog(description="获得二级资产类型列表", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeVO>> getAssetTypeList(@RequestBody AssetSearchVO assetSearchVO){
		List<AssetTypeVO> list = assetTypeService.getAssetTypeList(assetSearchVO);
		return ResultUtil.success(list);
	}
	
	@PostMapping(value="/getAssetTypeListPage")
	@ApiOperation(value=" 获得二级资产类型列表(分页)",notes="")
	@SysRequestLog(description="得二级资产类型列表(分页)", actionType = ActionType.SELECT,manually=false)
	public PageRes<AssetTypeVO> getAssetTypeListPage(@RequestBody AssetSearchVO assetSearchVO){
		List<AssetTypeVO> list = assetTypeService.getAssetTypeList(assetSearchVO);
		PageRes<AssetTypeVO> page=new PageRes<>();
		if(assetSearchVO.getStart_()==null) {
			assetSearchVO.setStart_(0);
		}
		if(assetSearchVO.getCount_()==null) {
			assetSearchVO.setCount_(10);
		}
		int end=assetSearchVO.getStart_()+assetSearchVO.getCount_();
		Long total=list.size()+0L;
		page.setList(list.subList(assetSearchVO.getStart_(), end>=total.intValue()?total.intValue():end));
		page.setTotal(total);
		page.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
		return page ;
	}
	
	
	/**
	 * 获得二级资产查询接口(在一级资产类型下)
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeAll")
	@ApiOperation(value=" 获得二级资产类型列表(在一级资产类型下)",notes="获得二级资产")
	@SysRequestLog(description="得二级资产类型列表(在一级资产类型下)", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeVO>> getAssetTypeAll() {
		List<AssetType> assetTypes = assetTypeService.getAllAssetTypeByGroup();
		List<AssetTypeVO> assetTypeVOs = mapper.mapList(assetTypes, AssetTypeVO.class);
		return ResultUtil.success(assetTypeVOs);
	}
	
	/**
	 * 添加二级资产
	 * @param assetTypeGroupVO
	 * @return
	 */
	@PostMapping(value = "/saveAddAssetType")
	@SysRequestLog(description="获得二级资产类型列表", actionType = ActionType.ADD,manually=false)
	@ApiOperation(value=" 获得二级资产类型列表",notes="添加二级资产")
	public 
	Result<Boolean> saveAddAssetType(@RequestBody AssetTypeVO assetTypeVO) {
		assetTypeVO.setGuid(UUIDUtils.get32UUID());
		AssetType assetType = mapper.map(assetTypeVO, AssetType.class);
		try {
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeVO.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeVO.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeVO.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeVO.getTreeCode().split("-").length!=3) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为3部分");
			}
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME, assetType.getName()));
			long count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型名称重复");
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetType.getNameEn()));
			count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型英文名称重复");
			}
			
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetType.getUniqueCode()));
			count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型唯一编码重复");
			}
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeVO.getTreeCode()));
			count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型树结构编码重复");
			}
			
			
			assetTypeService.save(assetType);
			return getResultTrue();
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
	}
	
	
	/**
	 * 编辑一级类型资产
	 * @param assetTypeGroupVO
	 * @return
	 */
	@PostMapping(value = "/saveEditAssetType")
	@SysRequestLog(description="编辑一级类型资产", actionType = ActionType.UPDATE,manually=false)
	@ApiOperation(value="编辑一级类型资产",notes="编辑一级资产")
	public Result<Boolean> saveEditAssetType(@RequestBody AssetTypeVO assetTypeVO) {
		
		AssetType assetType = assetTypeService.getOne(assetTypeVO.getGuid());
		
		if(assetType==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作的原始数据不存在，可能被删除");
		}
		if(assetType.getPredefine()!=null&&assetType.getPredefine()) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型为预定义数据，禁止编辑！");
		}
		
		if(!assetType.getTreeCode().equals(assetTypeVO.getTreeCode())) {
			List<QueryCondition> querys=new ArrayList<>();
			querys.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetType.getTreeCode()+"-"));
			long count = assetTypeService.count(querys);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产大类下存在"+count+"个资产类型，\r\n需先删除资产类型才可继续修改资产大类树形结构！");
			}
		}
		
		
	    if(assetType.getStatus()!=null&&assetType.getStatus().intValue()==1) {//禁用
	    	//判断是否存在资产  如果存在资产则禁止编辑
			int assetsCount = assetService.getAssetCountNotWithAuth(assetType.getGuid());
			if(assetsCount>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该类型下存在资产，需删除资产之后禁用");
			}
	    }
	    
	    
		List<QueryCondition> coons=new ArrayList<>();
		coons.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetType.getTreeCode()+"-"));
		long countt = assetTypeSnoService.count(coons);
		 
		if(countt>0&&!assetType.getTreeCode().equals(assetTypeVO.getTreeCode())) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型分类下存在"+countt+"个品牌型号，\r\n需先删除资产品牌型号才可继续修改资产类型！");
		}
		 
		
		
		
		mapper.copy(assetTypeVO, assetType);
		try {
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeVO.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeVO.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeVO.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeVO.getTreeCode().split("-").length!=3) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为3部分");
			}
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME, assetType.getName()));
			conditions.add(QueryCondition.notEq("guid", assetTypeVO.getGuid()));
			long count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型名称重复");
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetType.getNameEn()));
			conditions.add(QueryCondition.notEq("guid", assetTypeVO.getGuid()));
			count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型英文名称重复");
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetType.getUniqueCode()));
			conditions.add(QueryCondition.notEq("guid", assetTypeVO.getGuid()));
			count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型唯一编码重复");
			}
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeVO.getTreeCode()));
			conditions.add(QueryCondition.notEq("guid", assetTypeVO.getGuid()));
			count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "二级类型树结构编码重复");
			}
 
			
			assetTypeService.save(assetType);
			return getResultTrue();
		} catch (Exception e) {
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
	}

	private Result<Boolean> getResultTrue() {
		Result<Boolean> result = new Result<Boolean>();
		result.setCode(200);
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}
	
	
	@GetMapping(value = "/delAssetType/{guid}")
	@SysRequestLog(description="删除资产大类", actionType = ActionType.DELETE,manually=false)
	@ApiOperation(value="删除资产",notes="")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产guid",required=true,dataType="String"))
	public Result<Boolean> delAssetType(@PathVariable("guid") String guid) {
		 
		AssetType assetType = assetTypeService.getOne(guid);
		
		if(assetType.getPredefine()!=null&&assetType.getPredefine()) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型为预定义数据，禁止删除！");
		}
		
		try {
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetType.getTreeCode()+"-"));
			long count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型分类下存在"+count+"个品牌型号，\r\n需先删除资产品牌型号才可继续删除资产类型！");
			}
			
			
			assetTypeService.delete(assetType);
			return getResultTrue();
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
	}
	
	/**
	 * 获得二级资产类型详细数据
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAssetType/{guid}")
	@ApiOperation(value="获得二级资产类型详细数据",notes="")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产guid",required=true,dataType="String"))
	@SysRequestLog(description="获得二级资产类型详细数据", actionType = ActionType.SELECT,manually=false)
	public Result<AssetTypeVO> getAssetTypeGroup(@PathVariable("guid") String guid) {
		Result<AssetTypeVO> result = new Result<>();
		AssetType assetType = assetTypeService.getOne(guid);
		AssetTypeVO assetTypeVO = mapper.map(assetType, AssetTypeVO.class);
		result.setCode(200);
		result.setData(assetTypeVO);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}


	/**
	 *根据二级资产类型查询资产
	 */
	@GetMapping(value = "/getAssetTypeByType/{uniqueCode}")
	@ApiOperation(value="据uniqueCode类型二级资产类型",notes="")
	@SysRequestLog(description="据uniqueCode类型二级资产类型", actionType = ActionType.SELECT,manually=false)
	public Result<AssetTypeVO> getAssetTypeByType(@PathVariable("uniqueCode") String uniqueCode){

		List<QueryCondition> conditions=new ArrayList<>();
		conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE,uniqueCode));
		List<AssetType> assetTypeList=assetTypeService.findAll(conditions);
		if(assetTypeList!=null&&!assetTypeList.isEmpty()) {
			AssetTypeVO assetTypeVO=mapper.map(assetTypeList.get(0),AssetTypeVO.class);
			return  ResultUtil.success(assetTypeVO);
		}
		 
		return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "找不到该uniqueCode的资产二级类型");
	}

	
	/**
	 * 获得资产类型CombobxTree
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeComboboxTree")
	@ApiOperation(value="获得资产类型树结构",notes="")
	@SysRequestLog(description="获得资产类型树结构", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeTreeVO>> getAssetTypeComboboxTree() {
		List<AssetTypeTreeVO> list = assetTypeService.getAssetTypeComboboxTree();
		return ResultUtil.success(list);
	}

	/**
	 * 获得资产类型CombobxTree(数据同步，资产待申表中用到) --2022-07-19
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeSynchTree")
	@ApiOperation(value="获得资产类型树结构",notes="")
	@SysRequestLog(description="获得资产类型树结构(数据同步，资产待申表中用)", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeTreeVO>> getAssetTypeSynchTree() {
		List<AssetTypeTreeVO> list = assetTypeService.getAllAssetTypeComboboxTree();
		return ResultUtil.success(list);
	}
	
	/**
	 * 改变状态
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/changeStatus")
	@ApiOperation(value="改变二级资产状态",notes="")
	@SysRequestLog(description="改变二级资产状态", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> changeStatus(@RequestBody AssetSearchVO assetSearchVO) {
		 
		String guid = assetSearchVO.getGuid();
		String status =assetSearchVO.getStatus();
		AssetType assetType = assetTypeService.getOne(guid);
		
		if(assetType==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作的原始数据不存在，可能被删除");
		}
		
	    if("1".equals(status)) {//禁用
	    	//判断是否存在资产  如果存在资产则禁止编辑
			int assetsCount = assetService.getAssetCountNotWithAuth(guid);
			if(assetsCount>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该类型下存在资产，需删除资产之后禁用");
			}
	    }
		
		
		assetType.setStatus(Integer.parseInt(status));
		try{
			assetTypeService.save(assetType);
			 
			return ResultUtil.success(true);
		}catch(AlarmDealException e) {
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
	}
	/**
	 * 根据资产类型Id获得监控协议
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getMonitorProtocols/{guid}")
	@ApiOperation(value="根据资产类型Id获得监控协议",notes="")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产guid",required=true,dataType="String"))
	@SysRequestLog(description="根据资产类型Id获得监控协议", actionType = ActionType.SELECT,manually=false)
	public Result<List<ValueTextVO>> getMonitorProtocols(@PathVariable String guid) {
		List<ValueTextVO> result = new ArrayList<>();
		AssetType at = assetTypeService.getOne(guid);
		String protocols = at.getMonitorProtocols();
		if (protocols != null && !protocols.isEmpty()) {
			String[] ptcArr = protocols.split(",");
			for (String str : ptcArr) {
				ValueTextVO vt = new ValueTextVO();
				vt.setValue(str);
				vt.setText(str);
				result.add(vt);
			}
		}
		return ResultUtil.success(result);
	}

	//--------------关保相关--------------------//
	/**
	 * 根据资产类型Id获得资产类型名称
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeNameById/{guid}")
	@ApiOperation(value="根据资产类型Id获得资产类型名称",notes="")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产guid",required=true,dataType="String"))
	public Result<String> getAssetTypeNameById(@PathVariable String guid) {
		AssetType at = assetTypeService.getOne(guid);
		if(at !=null){
			return ResultUtil.success(at.getName());
		}
		return ResultUtil.success(null);
	}
}
