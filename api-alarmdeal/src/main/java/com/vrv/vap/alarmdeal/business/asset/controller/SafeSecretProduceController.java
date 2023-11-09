package com.vrv.vap.alarmdeal.business.asset.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.SafeSecretProduce;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.SafeSecretProduceService;
import com.vrv.vap.alarmdeal.business.asset.vo.SafeSecretProduceVO;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 保密产品编排处理
 *
 * @author vrv
 * @date 2021-08-12
 */
@RestController
@RequestMapping(value = "/safeSecretProduce")
public class SafeSecretProduceController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(SafeSecretProduceController.class);

    @Autowired
    private SafeSecretProduceService safeSecretProduceService;
    @Autowired
    private AssetService assetService;


    @Autowired
    private MapperUtil mapper;

    /**
     * 保存保密产品
     *
     * @param safeSecretProduceVO
     * @return
     */
    @PostMapping(value = "/saveSafeSecretProduce")
    @ApiOperation(value = "保存保密产品", notes = "")
    @SysRequestLog(description = "保存保密产品", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> saveSafeSecretProduce(@RequestBody SafeSecretProduceVO safeSecretProduceVO) {
        if(null == safeSecretProduceVO ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        String guid =UUIDUtils.get32UUID();
        safeSecretProduceVO.setGuid(guid);
        logger.info("saveSafeSecretProduce:{}", JSON.toJSONString(safeSecretProduceVO));
        SafeSecretProduce data = mapper.map(safeSecretProduceVO, SafeSecretProduce.class);
        safeSecretProduceService.save(data);
        addSecreteProductNum(safeSecretProduceVO);

        return ResultUtil.success(true);
    }


    /**
     * 更新安全产品数据量
     * @param safeSecretProduceVO
     */
	private void addSecreteProductNum(SafeSecretProduceVO safeSecretProduceVO) {
		String assetGuid = safeSecretProduceVO.getAssetGuid();
        if(StringUtils.isNotEmpty(assetGuid)) {
        	Asset asset = assetService.getOne(assetGuid);
        	int safeSecretProduceNum = asset.getSafeSecretProduceNum();
        	safeSecretProduceNum++;
        	asset.setSafeSecretProduceNum(safeSecretProduceNum);
        	assetService.save(asset);
        }
	}

    /**
     * 删除保密产品
     *
     * @param safeSecretProduceVO
     * @return
     */
    @PostMapping(value = "/deleteSafeSecretProduce")
    @ApiOperation(value = "删除保密产品", notes = "")
    @SysRequestLog(description = "删除保密产品", actionType = ActionType.DELETE,manually = false)
    public Result<Boolean> deleteSafeSecretProduce(@RequestBody SafeSecretProduceVO safeSecretProduceVO) {
        if(null == safeSecretProduceVO || StringUtils.isEmpty(safeSecretProduceVO.getGuid())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        logger.info("deleteSafeSecretProduce guid:{}" , safeSecretProduceVO.getGuid());
        SafeSecretProduce safe = safeSecretProduceService.getOne(safeSecretProduceVO.getGuid());
        if(null == safe){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "保密产品不存在");
        }
        safeSecretProduceService.delete(safe.getGuid());
        logger.info("asset guid:{}",safe.getAssetGuid());
        deleteSecreteProductNum(safe);
        return ResultUtil.success(true);
    }

    private void deleteSecreteProductNum(SafeSecretProduce safeSecretProduceVO) {
		String assetGuid = safeSecretProduceVO.getAssetGuid();
        if(StringUtils.isNotEmpty(assetGuid)) {
        	Asset asset = assetService.getOne(assetGuid);
        	int safeSecretProduceNum = asset.getSafeSecretProduceNum();
        	safeSecretProduceNum--;
        	if(safeSecretProduceNum<0) {
        		safeSecretProduceNum = 0;
        	}
        	asset.setSafeSecretProduceNum(safeSecretProduceNum);
        	assetService.save(asset);
        }
	}



    /**
     * 修改保密产品
     *
     * @param safeSecretProduceVO
     * @return
     */
    @PostMapping(value = "/updateSafeSecretProduce")
    @ApiOperation(value = "修改保密产品", notes = "")
    @SysRequestLog(description = "修改保密产品", actionType = ActionType.UPDATE,manually = false)
    public Result<Boolean> updateSafeSecretProduce(@RequestBody SafeSecretProduceVO safeSecretProduceVO) {
        if(null == safeSecretProduceVO ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        logger.info("updateSafeSecretProduce:{}" , JSON.toJSONString(safeSecretProduceVO));
        SafeSecretProduce data = mapper.map(safeSecretProduceVO, SafeSecretProduce.class);
        safeSecretProduceService.save(data);
        return ResultUtil.success(true);
    }

    /**
     * 查询保密产品带分页
     *
     * @param safeSecretProduceVO
     * @return
     */
    @PostMapping(value = "/querySecretProducePage")
    @ApiOperation(value = "查询保密产品带分页", notes = "")
    @SysRequestLog(description = "查询保密产品带分页", actionType = ActionType.SELECT,manually = false)
    public Result<List<SafeSecretProduce>> querySecretProducePage(@RequestBody SafeSecretProduceVO safeSecretProduceVO) {
        if(null == safeSecretProduceVO ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        logger.info("querySecretProducePage:{}" , JSON.toJSONString(safeSecretProduceVO));
        if (safeSecretProduceVO == null) {
            safeSecretProduceVO = new SafeSecretProduceVO();
        }
        if (safeSecretProduceVO.getCount_() == null || safeSecretProduceVO.getCount_() < 0) {
            safeSecretProduceVO.setCount_(10);
        }
        if (safeSecretProduceVO.getStart_() == null || safeSecretProduceVO.getStart_() < 0) {
            safeSecretProduceVO.setStart_(0);
        }
        PageReq pageReq = new PageReq();
        pageReq.setCount(safeSecretProduceVO.getCount_());
        pageReq.setStart(safeSecretProduceVO.getStart_());
        List<QueryCondition> conditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(safeSecretProduceVO.getGuid())) {
            conditions.add(QueryCondition.eq("guid", safeSecretProduceVO.getGuid()));
        }
        if (StringUtils.isNotEmpty(safeSecretProduceVO.getAssetGuid())) {
            conditions.add(QueryCondition.eq("assetGuid", safeSecretProduceVO.getAssetGuid()));
        }
        Page<SafeSecretProduce> safes = safeSecretProduceService.findAll(conditions, pageReq.getPageable());
        return ResultUtil.success(safes.getContent());
    }

    /**
     *查询保密产品
     *
     * @param safeSecretProduceVO
     * @return
     */
    @PostMapping(value = "/querySecretProduce")
    @ApiOperation(value = "查询保密产品", notes = "")
    @SysRequestLog(description = "查询保密产品", actionType = ActionType.SELECT,manually = false)
    public Result<List<SafeSecretProduce>> querySecretProduce(@RequestBody SafeSecretProduceVO safeSecretProduceVO) {
        if(null == safeSecretProduceVO ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        logger.info("querySecretProduce:{}" , JSON.toJSONString(safeSecretProduceVO));
        List<QueryCondition> conditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(safeSecretProduceVO.getGuid())) {
            conditions.add(QueryCondition.eq("guid", safeSecretProduceVO.getGuid()));
        }
        if (StringUtils.isNotEmpty(safeSecretProduceVO.getAssetGuid())) {
            conditions.add(QueryCondition.eq("assetGuid", safeSecretProduceVO.getAssetGuid()));
        }
        List<SafeSecretProduce> safes = safeSecretProduceService.findAll(conditions);
        return ResultUtil.successList(safes);
    }

    /**
     * 查询设备对应保密产品数量
     *
     * @param assetGuid
     * @return
     */
    @GetMapping(value = "/querySecretProduceCountByAssetGuid")
    @ApiOperation(value = "查询设备对应保密产品数量", notes = "")
    @SysRequestLog(description = "查询设备对应保密产品数量", actionType = ActionType.SELECT,manually = false)
    public Result<Long> querySecretProduceCountByAssetGuid(@RequestParam("assetGuid") String assetGuid) {
        if (StringUtils.isEmpty(assetGuid)) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("assetGuid", assetGuid));
        long count = safeSecretProduceService.count(conditions);
        return ResultUtil.success(count);
    }

    /**
     * 保密产品增加关联设备guid
     *
     * @param safeSecretProduceVO
     * @return
     */
    @PostMapping(value = "/updateSafeSecretProduceAssetGuid")
    @ApiOperation(value = "保密产品增加关联设备guid", notes = "")
    @SysRequestLog(description = "保密产品增加关联设备guid", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> updateSafeSecretProduceAssetGuid(@RequestBody SafeSecretProduceVO safeSecretProduceVO) {
        if(null == safeSecretProduceVO ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        logger.info("updateSafeSecretProduceAssetGuid:{}" , JSON.toJSONString(safeSecretProduceVO));
        String guid = safeSecretProduceVO.getGuid();
        if (StringUtils.isEmpty(guid)) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        String assetGuid = safeSecretProduceVO.getAssetGuid();
        if (StringUtils.isEmpty(assetGuid)) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常");
        }
        SafeSecretProduce safe = safeSecretProduceService.getOne(guid);
        if (null == safe) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "不存在对应保密产品");
        }
        safe.setAssetGuid(assetGuid);
        safeSecretProduceService.save(safe);
        return ResultUtil.success(true);
    }
}
