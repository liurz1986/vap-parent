package com.vrv.vap.alarmdeal.business.asset.controller;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.util.ResultEnum;
import com.vrv.vap.alarmdeal.business.asset.util.StaticKey;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeGroupVO;
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
@RequestMapping(value="/assettypegroup")
public class AssetTypeGroupController extends BaseController {

	private static Logger logger  = LoggerFactory.getLogger(AssetTypeGroupController.class);
	@Autowired
	private AssetTypeGroupService assetTypeGroupService;
	@Autowired
	private MapperUtil mapper;
 
	@Autowired
	private AssetTypeService assetTypeService;
	
	@Autowired
	private AssetService assetService;
	
	/**
	 * 获得一级资产类型列表
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/getAssetTypeGroupList")
	@ApiOperation(value="获得一级资产类型列表",notes="")
	@SysRequestLog(description="获得一级资产类型列表", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetTypeGroupVO>> getAssetTypeGroupList(@RequestBody AssetSearchVO assetSearchVO){
		List<AssetTypeGroupVO> list = assetTypeGroupService.getAssetTypeGroupList(assetSearchVO);
		return ResultUtil.success(list) ;
	}
	
	@PostMapping(value="/getAssetTypeGroupListPage")
	@ApiOperation(value="获得一级资产类型列表",notes="")
	@SysRequestLog(description="获得一级资产类型列表", actionType = ActionType.SELECT,manually=false)
	public PageRes<AssetTypeGroupVO> getAssetTypeGroupListPage(@RequestBody AssetSearchVO assetSearchVO){
		List<AssetTypeGroupVO> list = assetTypeGroupService.getAssetTypeGroupList(assetSearchVO);
		PageRes<AssetTypeGroupVO> page=new PageRes<>();
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
	 * 添加一级资产
	 * @param assetTypeGroupVO
	 * @return
	 */
	@PostMapping(value = "/saveAddAssetTypeGroup")
	@SysRequestLog(description="添加一级资产", actionType = ActionType.ADD,manually=false)
	@ApiOperation(value="添加一级资产",notes="")
	public 
	Result<Boolean> saveAddAssetTypeGroup(@RequestBody AssetTypeGroupVO assetTypeGroupVO) {
		Result<Boolean> result = new Result<>();
		assetTypeGroupVO.setGuid(UUIDUtils.get32UUID());
		AssetTypeGroup assetTypeGroup = mapper.map(assetTypeGroupVO, AssetTypeGroup.class);
		try {
			
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeGroupVO.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeGroupVO.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeGroupVO.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeGroupVO.getTreeCode().split("-").length!=2) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为2部分");
			}
			
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq("name", assetTypeGroup.getName()));
			long count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_GROUPNAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetTypeGroup.getNameEn()));
			count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "一级类型英文名称重复");
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetTypeGroup.getUniqueCode()));
			count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "一级类型唯一编码重复");
			}
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCode()));
			count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "一级类型树结构编码重复");
			}

			
			assetTypeGroupService.save(assetTypeGroup);
			result.setCode(200);
			result.setData(true);
			result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
			return result;
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
	@PostMapping(value = "/saveEditAssetTypeGroup")
	@SysRequestLog(description="编辑一级类型资产", actionType = ActionType.UPDATE,manually = false)
	@ApiOperation(value="编辑一级类型资产",notes="")
	public Result<Boolean> saveEditAssetTypeGroup(@RequestBody AssetTypeGroupVO assetTypeGroupVO) {
		Result<Boolean> result = new Result<>();
		AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(assetTypeGroupVO.getGuid());
		
		if(assetTypeGroup==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作的原始数据不存在，可能被删除");
		}
		
		if(assetTypeGroup.getPredefine()!=null&&assetTypeGroup.getPredefine()) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型为预定义数据，禁止编辑！");
		}

		
	    if(assetTypeGroup.getStatus()!=null&&assetTypeGroup.getStatus().intValue()==1) {//禁用
	    	//判断是否存在资产  如果存在资产则禁止编辑
			int assetsCount = assetService.getAssetCountNotWithAuth(assetTypeGroup.getGuid());
			if(assetsCount>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该类型下存在资产，需删除资产之后禁用");
			}
	    }
	    
	    
		List<QueryCondition> coos=new ArrayList<>();

		coos.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroup.getTreeCode()+"-"));
		long countt = assetTypeService.count(coos);
 
			
		if(countt>0&&!assetTypeGroup.getTreeCode().equals(assetTypeGroupVO.getTreeCode())) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产大类下存在"+countt+"个资产类型，\r\n需先删除资产类型才可继续修改资产大类！");
		}
			
 
		
	    
	    
		
		
		mapper.copy(assetTypeGroupVO, assetTypeGroup);
		try {
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeGroupVO.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeGroupVO.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeGroupVO.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeGroupVO.getTreeCode().split("-").length!=2) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为2部分");
			}
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq("name", assetTypeGroup.getName()));
			conditions.add(QueryCondition.notEq("guid", assetTypeGroupVO.getGuid()));
			long count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),ResultEnum.ASSET_GROUPNAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetTypeGroup.getNameEn()));
			conditions.add(QueryCondition.notEq("guid", assetTypeGroupVO.getGuid()));
			count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "一级类型英文名称重复");
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetTypeGroup.getUniqueCode()));
			conditions.add(QueryCondition.notEq("guid", assetTypeGroupVO.getGuid()));
			count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "一级类型唯一编码重复");
			}
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeGroupVO.getTreeCode()));
			conditions.add(QueryCondition.notEq("guid", assetTypeGroupVO.getGuid()));
			count = assetTypeGroupService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "一级类型树结构编码重复");
			}

			assetTypeGroupService.save(assetTypeGroup);
			result.setCode(200);
			result.setData(true);
			result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
	}
	
	
	@GetMapping(value = "/delAssetTypeGroup/{guid}")
	@SysRequestLog(description="删除一级类型资产", actionType = ActionType.DELETE,manually = false)
	@ApiOperation(value="删除一级类型资产",notes="")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产类型guid",required=true,dataType="String"))
	public Result<Boolean> delAssetTypeGroup(@PathVariable("guid") String guid) {
		Result<Boolean> result = new Result<>();
		AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(guid);
		try {
			
			if(assetTypeGroup.getPredefine()!=null&&assetTypeGroup.getPredefine()) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型为预定义数据，禁止删除！");
			}
			
			List<QueryCondition> conditions=new ArrayList<>();

			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeGroup.getTreeCode()+"-"));
			long count = assetTypeService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产大类下存在"+count+"个资产类型，\r\n需先删除资产类型才可继续删除资产大类！");
			}
			
			
			assetTypeGroupService.delete(assetTypeGroup);
			result.setCode(200);
			result.setData(true);
			result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
			return result;
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
	}
	
	/**
	 * 获得一级资产类型详细数据
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeGroup/{guid}")
	@ApiOperation(value="获得一级资产类型详细数据",notes="")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="获得一级资产类型详细数据",required=true,dataType="String"))
	@SysRequestLog(description="获得一级资产类型详细数据", actionType = ActionType.SELECT,manually = false)
	public Result<AssetTypeGroupVO> getAssetTypeGroup(@PathVariable("guid") String guid) {
		Result<AssetTypeGroupVO> result = new Result<>();
		AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(guid);
		AssetTypeGroupVO assetTypeGroupVO = mapper.map(assetTypeGroup, AssetTypeGroupVO.class);
		result.setCode(200);
		result.setData(assetTypeGroupVO);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}
	
	/**
	 * 改变状态
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value = "/changeStatus")
	@ApiOperation(value="改变一级资产状态",notes="")
	@SysRequestLog(description="改变一级资产状态", actionType = ActionType.UPDATE,manually = false)
	public Result<Boolean> changeStatus(@RequestBody AssetSearchVO assetSearchVO) {
		Result<Boolean> result = new Result<>();
		String guid = assetSearchVO.getGuid();
		String status =assetSearchVO.getStatus();
		AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(guid);
		
		if(assetTypeGroup==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作的原始数据不存在，可能被删除");
		}
		
	    if("1".equals(status)) {//禁用
	    	//判断是否存在资产  如果存在资产则禁止编辑
			int assetsCount = assetService.getAssetCountNotWithAuth(guid);
			if(assetsCount>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该类型下存在资产，需删除资产之后禁用");
			}
	    }
		
		
		assetTypeGroup.setStatus(Integer.parseInt(status));
		assetTypeGroupService.save(assetTypeGroup);
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		return result;
	}

	//--------------关保相关--------------------//
	/**
	 * 获得一级资产类型所有数据
	 * 2022-08-25
	 * @param
	 * @return
	 */
	@GetMapping(value="/getAssetTypeGroupAll")
	@ApiOperation(value="获得一级资产类型列表",notes="")
	public Result<List<AssetTypeGroup>> getAssetTypeGroupAll(){
		List<AssetTypeGroup> list = assetTypeGroupService.findAll();
		return ResultUtil.success(list) ;
	}


}
