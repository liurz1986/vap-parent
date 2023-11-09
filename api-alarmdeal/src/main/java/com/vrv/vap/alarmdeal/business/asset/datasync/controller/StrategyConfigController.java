package com.vrv.vap.alarmdeal.business.asset.datasync.controller;

import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetStrategyConfigService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 策略配置
 */
@RestController
@RequestMapping("/strategyConfig")
public class StrategyConfigController {
    private static Logger logger = LoggerFactory.getLogger(StrategyConfigController.class);
    @Autowired
    private AssetStrategyConfigService assetStrategyConfigService;



    /**
     * 编辑资产的策略配置信息
     * @param tbConfS
     * @return
     */
    @PostMapping(value="/updatesAsset")
    @ApiOperation(value = "编辑资产的策略配置信息", notes = "")
    @SysRequestLog(description = "编辑资产的策略配置信息", actionType = ActionType.UPDATE,manually = false)
    public Result<String> updateAsset(@RequestBody List<TbConf> tbConfS){
        try{
            assetStrategyConfigService.updateStrategyConfig(tbConfS);
            return ResultUtil.success("success");
        }catch (Exception e){
            logger.error("编辑资产的策略配置信息异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑资产的策略配置信息异常");
        }
    }

    /**
     * 查询资产策略配置信息
     * @return
     */
    @PostMapping(value="/queyConfigAsset")
    @ApiOperation(value = "查询资产策略配置信息", notes = "")
    @SysRequestLog(description = "查询资产策略配置信息", actionType = ActionType.SELECT,manually = false)
    public Result<List<TbConf>> queyConfigAssets(){
        List<TbConf> lists = assetStrategyConfigService.getAssetStrategyConfigs();
        return ResultUtil.successList(lists);
    }

}
