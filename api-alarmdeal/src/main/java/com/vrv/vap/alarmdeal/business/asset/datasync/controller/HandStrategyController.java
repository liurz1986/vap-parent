package com.vrv.vap.alarmdeal.business.asset.datasync.controller;

import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetBookDetailService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.HandStrategyService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 手动入库策略配置
 * date:2023-4
 */
@RestController
@RequestMapping("/handStrategyConfig")
public class HandStrategyController {

    private static Logger logger = LoggerFactory.getLogger(StrategyConfigController.class);

    @Autowired
    private HandStrategyService handStrategyService;
    @Autowired
    private AssetBookDetailService assetBookDetailService;
    /**
     * 编辑资产的手动入库策略配置信息
     * @param tbConfS
     * @return
     */

    @PostMapping(value="/updatesAsset")
    @ApiOperation(value = "编辑资产的手动入库策略配置信息", notes = "")
    @SysRequestLog(description = "编辑资产的手动入库策略配置信息", actionType = ActionType.UPDATE,manually = false)
    public Result<String> updateAsset(@RequestBody List<TbConf> tbConfS){
        try{
            handStrategyService.updateAsset(tbConfS);
            return ResultUtil.success("success");
        }catch (Exception e){
            logger.error("编辑资产的手动入库策略配置信息异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑资产的手动入库策略配置信息异常");
        }
    }

    /**
     * 查询资产手动入库策略配置信息
     *
     * @return
     */
    @PostMapping(value="/queyConfigAsset")
    @ApiOperation(value = "查询资资产的手动入库策略配置信息", notes = "")
    @SysRequestLog(description = "查询资资产的手动入库策略配置信息", actionType = ActionType.SELECT,manually = false)
    public Result<List<TbConf>> queyConfigAssets(){
        return ResultUtil.successList(handStrategyService.queyConfigAssets());
    }

    /**
     * 查询资产数据同步入库方式
     * 1手动入库，2自动入库
     * tb_conf表conf_id为sync_asset_data_import_type
     * @return
     */
    @GetMapping(value="/queryImportType")
    @ApiOperation(value = "查询资产数据同步入库方式", notes = "")
    @SysRequestLog(description = "查询资产数据同步入库方式", actionType = ActionType.SELECT,manually = false)
    public Result<String> queryImportType(){
        return handStrategyService.queryImportType();
    }

    /**
     * 查询现有明细表所有数据源
     *
     * @return
     */
    @GetMapping(value="/queryDataSources")
    @ApiOperation(value = "查询现有明细表所有数据源", notes = "")
    @SysRequestLog(description = "查询现有明细表所有数据源", actionType = ActionType.SELECT,manually = false)
    public Result<List<String>> queryDataSources(){
        return assetBookDetailService.queryDataSources();
    }

    /**
     * 获取配置的对比列详情
     *
     * @return
     */
    @GetMapping(value="/queryShowColumns")
    @ApiOperation(value = "获取配置的对比列详情", notes = "")
    @SysRequestLog(description = "获取配置的对比列详情", actionType = ActionType.SELECT,manually = false)
    public Result<Map<String,Object>> queryShowColumns(){
        return handStrategyService.queyConfigAssets("sync_asset_data_diff_json");
    }
}
