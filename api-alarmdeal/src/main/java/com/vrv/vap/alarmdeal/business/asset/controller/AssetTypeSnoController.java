package com.vrv.vap.alarmdeal.business.asset.controller;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeTemplateService;
import com.vrv.vap.alarmdeal.business.asset.util.ResultEnum;
import com.vrv.vap.alarmdeal.business.asset.util.StaticKey;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeSnoEnsembleVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeSnoVO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/assettypesno")
public class AssetTypeSnoController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AssetTypeSnoController.class);
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	
	@Autowired
	private AssetTypeTemplateService assetTypeTemplateService;
	
	
	@Autowired
	private AssetService assetService;
	
	@Autowired
	private MapperUtil mapper;
 
	
	
	
	/**
	 * 三级资产类型查询
	 * @param assetSearchVO
	 * @return
	 */
	@PostMapping(value="/getAssetTypeSnoList")
	@ApiOperation(value="三级资产类型查询")
	@SysRequestLog(description="三级资产类型查询", actionType = ActionType.SELECT,manually = false)
	public Result<List<AssetTypeSnoVO>> getAssetTypeSnoList(@RequestBody AssetSearchVO assetSearchVO){
		List<AssetTypeSnoVO> list = assetTypeSnoService.getAssetTypeSnoList(assetSearchVO);
		return  ResultUtil.success(list) ;
	}
	
	@PostMapping(value="/getAssetTypeSnoListPage")
	@ApiOperation(value="三级资产类型查询")
	@SysRequestLog(description="三级资产类型查询", actionType = ActionType.SELECT,manually = false)
	public PageRes<AssetTypeSnoVO> getAssetTypeSnoListPage(@RequestBody AssetSearchVO assetSearchVO){
		List<AssetTypeSnoVO> list = assetTypeSnoService.getAssetTypeSnoList(assetSearchVO);
 
		PageRes<AssetTypeSnoVO> page=new PageRes<>();
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
	 * 添加三级资产
	 * @param assetTypeGroupVO
	 * @return
	 */
	@PostMapping(value = "/saveAddAssetTypeSno")
	@ApiOperation(value="添加三级资产")
	@SysRequestLog(description="添加三级资产", actionType = ActionType.ADD,manually = false)
	public Result<Boolean> saveAddAssetTypeSno(@RequestBody AssetTypeSnoVO assetTypeSnoVO) {
		Result<Boolean> result = new Result<>();
		assetTypeSnoVO.setGuid(UUIDUtils.get32UUID());
		AssetTypeSno assetTypeSno = mapper.map(assetTypeSnoVO, AssetTypeSno.class);
		try {
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeSno.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeSno.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getTreeCode().split("-").length!=4) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为4部分");
			}
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeSnoVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME, assetTypeSno.getName()));
			long count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_NAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeSnoVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetTypeSno.getNameEn()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_ENGLISH_NAME_REPEAT.getMsg());
			}
			
			conditions.clear(); 
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetTypeSno.getUniqueCode()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_UNIQUECODE_REPEAT.getMsg() );
			}
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeSnoVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeSno.getTreeCode()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),ResultEnum.ASSET_SNO_TREECODE_REPEAT.getMsg());
			}
			
			
			assetTypeSnoService.save(assetTypeSno);
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
	 * 编辑三级类型资产
	 * @param assetTypeGroupVO
	 * @return
	 */
	@PostMapping(value = "/saveEditAssetTypeSno")
	@ApiOperation(value="编辑三级类型资产")
	@SysRequestLog(description="编辑三级类型资产", actionType = ActionType.UPDATE,manually = false)
	public Result<Boolean> saveEditAssetTypeSno(@RequestBody AssetTypeSnoVO assetTypeSnoVO) {
		Result<Boolean> result = new Result<>();
		AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(assetTypeSnoVO.getGuid());
		
		
		if(assetTypeSno==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作的原始数据不存在，可能被删除");
		}
		
		if(assetTypeSno.getPredefine()!=null&&assetTypeSno.getPredefine()) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型为预定义数据，禁止编辑！");
		}
		
	    if(assetTypeSno.getStatus()!=null&&assetTypeSno.getStatus().intValue()==1) {//禁用
	    	//判断是否存在资产  如果存在资产则禁止编辑
			int assetsCount = assetService.getAssetCountNotWithAuth(assetTypeSno.getGuid());
			if(assetsCount>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该类型下存在资产，需删除资产之后禁用");
			}
	    }
		
		
		mapper.copy(assetTypeSnoVO, assetTypeSno);
		try {
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeSno.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeSno.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getTreeCode().split("-").length!=4) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为4部分");
			}
			
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeSnoVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME, assetTypeSno.getName()));
			conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
			long count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_NAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeSnoVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetTypeSno.getNameEn()));
			conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_ENGLISH_NAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetTypeSno.getUniqueCode()));
			conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_UNIQUECODE_REPEAT.getMsg());
			}
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, assetTypeSnoVO.getTreeCodeHead()));
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeSno.getTreeCode()));
			conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_TREECODE_REPEAT.getMsg());
			}

			
			assetTypeSnoService.save(assetTypeSno);
			result.setCode(200);
			result.setData(true);
			result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
		}
		return result;
	}
	
	
	@GetMapping(value = "/delAssetTypeSno/{guid}")
	@SysRequestLog(description="删除三级类型资产", actionType = ActionType.DELETE,manually = false)
	@ApiOperation(value="删除三级类型资产")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产类型guid",required=true,dataType="String"))
	public Result<Boolean> delAssetType(@PathVariable("guid") String guid) {
		Result<Boolean> result = new Result<>();
		AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(guid);
		try {
			
			if(assetTypeSno.getPredefine()!=null&&assetTypeSno.getPredefine()) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产类型为预定义数据，禁止删除！");
			}
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.eq("assetTypeSnoGuid", guid));
			long count = assetService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该资产品牌型号下存在"+count+"个资产，\r\n需先删除资产才可继续删除品牌型号！");
			}

			assetTypeSnoService.delete(assetTypeSno);
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
	 * 获得三级资产类型详情
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAssetTypeSno/{guid}")
	@ApiOperation(value="获得三级资产类型详情")
	@ApiImplicitParams(@ApiImplicitParam(name="guid",value="资产类型guid",required=true,dataType="String"))
	@SysRequestLog(description="获得三级资产类型详情", actionType = ActionType.SELECT,manually = false)
	public Result<AssetTypeSnoVO> getAssetTypeGroup(@PathVariable("guid") String guid) {
		Result<AssetTypeSnoVO> result = new Result<>();
		AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(guid);
		AssetTypeSnoVO assetTypeSnopVO = mapper.map(assetTypeSno, AssetTypeSnoVO.class);
		result.setCode(200);
		result.setData(assetTypeSnopVO);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}
	
	
	@PostMapping(value = "/changeStatus")
	@ApiOperation(value="改变三级资产状态")
	@SysRequestLog(description="改变三级资产状态", actionType = ActionType.UPDATE,manually = false)
	public Result<Boolean> changeStatus(@RequestBody AssetSearchVO assetSearchVO) {
	
		String guid = assetSearchVO.getGuid();
		String status =assetSearchVO.getStatus();
		AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(guid);
		if(assetTypeSno==null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作的原始数据不存在，可能被删除");
		}
		
	    if("1".equals(status)) {//禁用
	    	//判断是否存在资产  如果存在资产则禁止编辑
			int assetsCount = assetService.getAssetCountNotWithAuth(guid);
			if(assetsCount>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该类型下存在资产，需删除资产之后禁用");
			}
	    }


		assetTypeSno.setStatus(Integer.parseInt(status));
		assetTypeSnoService.save(assetTypeSno);
	 
		return ResultUtil.success(true);
	}
	
	@PostMapping(value = "/addTypeSnoEnsemble")
	@ApiOperation(value = "新增品牌型号集合体")
	@SysRequestLog(description="新增品牌型号集合体", actionType = ActionType.ADD,manually = false)
	@Transactional
	public Result<AssetTypeSnoEnsembleVO> addTypeSnoEnsemble(@RequestBody AssetTypeSnoEnsembleVO assetTypeSnoEnsembleVO) {
		String guid = UUIDUtils.get32UUID();
		try {
			AssetTypeSno assetTypeSno = assetTypeSnoEnsembleVO.getAssetTypeSno();
			assetTypeSno.setGuid(guid);
			
			String treeCodeHead=assetTypeSno.getTreeCode();
			
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeSno.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeSno.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getTreeCode().split("-").length!=4) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为4部分");
			}
			
			
			List<QueryCondition> conditions=new ArrayList<>();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, treeCodeHead));
			conditions.add(QueryCondition.eq(StaticKey.NAME, assetTypeSno.getName()));
			long count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_NAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, treeCodeHead));
			conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetTypeSno.getNameEn()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_ENGLISH_NAME_REPEAT.getMsg());
			}
			
			conditions.clear();
			conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetTypeSno.getUniqueCode()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_UNIQUECODE_REPEAT.getMsg());
			}
			conditions.clear();
			conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, treeCodeHead));
			conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeSno.getTreeCode()));
			count = assetTypeSnoService.count(conditions);
			if(count>0) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_TREECODE_REPEAT.getMsg());
			}
			
			
			

			AssetTypeTemplate assetTypeTemplate = assetTypeSnoEnsembleVO.getAssetTypeTemplate();
			assetTypeTemplate.setGuid(guid);
			
			if(StringUtils.isEmpty(assetTypeTemplate.getFormdata())) {
				assetTypeTemplate.setFormdata("{}");
			}
			
			if(StringUtils.isEmpty(assetTypeTemplate.getKeyData())) {
				assetTypeTemplate.setFormdata("[]");
			}
			
			
			assetTypeSnoService.save(assetTypeSno);
			assetTypeTemplateService.save(assetTypeTemplate);
			return ResultUtil.success(assetTypeSnoEnsembleVO);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "数据保存异常：" + ex.getMessage());
		}
	}
	
	
	
	@PostMapping(value = "/editTypeSnoEnsemble")
	@ApiOperation(value = "编辑品牌型号集合体")
	@SysRequestLog(description="编辑品牌型号集合体", actionType = ActionType.UPDATE,manually = false)

	@Transactional
	public Result<Boolean> editTypeSnoEnsemble(@RequestBody AssetTypeSnoEnsembleVO assetTypeSnoEnsembleVO) {
		String guid = UUIDUtils.get32UUID();
		try {
			AssetTypeSno assetTypeSnoVO = assetTypeSnoEnsembleVO.getAssetTypeSno();
			
			Result<Boolean> result = new Result<>();
			AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(assetTypeSnoVO.getGuid());
			mapper.copy(assetTypeSnoVO, assetTypeSno);
			
			
			//数据校验
			if(StringUtils.isEmpty(assetTypeSno.getUniqueCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getUniqueCode().contains("-")) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_UNIQUECODE_HORIZONTAL_LINE.getMsg());
			}
			
			if(StringUtils.isEmpty(assetTypeSno.getTreeCode())) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.TREECODE_EMPTY.getMsg());
			}
			if(assetTypeSno.getTreeCode().split("-").length!=4) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "树结构编码结构不正确，横线分割后应为4部分");
			}
			
			
			
			String treeCodeHead=assetTypeSnoVO.getTreeCode();
			
	 
				
				treeCodeHead = treeCodeHead.substring(0, treeCodeHead.lastIndexOf('-'));
				
				List<QueryCondition> conditions=new ArrayList<>();
				conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, treeCodeHead));
				conditions.add(QueryCondition.eq(StaticKey.NAME, assetTypeSno.getName()));
				conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
				long count = assetTypeSnoService.count(conditions);
				if(count>0) {
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_NAME_REPEAT.getMsg());
				}
				
				conditions.clear();
				conditions.add(QueryCondition.likeBegin(StaticKey.TREE_CODE, treeCodeHead));
				conditions.add(QueryCondition.eq(StaticKey.NAME_EN, assetTypeSno.getNameEn()));
				conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
				count = assetTypeSnoService.count(conditions);
				if(count>0) {
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_ENGLISH_NAME_REPEAT.getMsg());
				}
				
				conditions.clear();
				conditions.add(QueryCondition.eq(StaticKey.UNIQUE_CODE, assetTypeSno.getUniqueCode()));
				conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
				count = assetTypeSnoService.count(conditions);
				if(count>0) {
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_UNIQUECODE_REPEAT.getMsg());
				}
				conditions.clear();
				conditions.add(QueryCondition.eq(StaticKey.TREE_CODE, assetTypeSno.getTreeCode()));
				conditions.add(QueryCondition.notEq(StaticKey.GUID, assetTypeSnoVO.getGuid()));
				count = assetTypeSnoService.count(conditions);
				if(count>0) {
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), ResultEnum.ASSET_SNO_TREECODE_REPEAT.getMsg());
				}
				
				
				assetTypeSnoService.save(assetTypeSno);
				result.setCode(200);
				result.setData(true);
				result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
 
			
			assetTypeSnoService.save(assetTypeSno);

			AssetTypeTemplate assetTypeTemplate = assetTypeSnoEnsembleVO.getAssetTypeTemplate();
			if(assetTypeTemplate!=null&&assetTypeTemplate.getKeyData()!=null) {//表示当前操作只更新了品牌信息
				assetTypeTemplate.setGuid(guid);
				assetTypeTemplateService.save(assetTypeTemplate);
			}
			
			return ResultUtil.success(true);
		} catch (Exception ex) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "数据保存异常：" + ex.getMessage());
		}
	}
	
	 
}
