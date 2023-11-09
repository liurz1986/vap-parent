package com.vrv.vap.alarmdeal.business.appsys.datasync.controller;

import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppStrategyConfigService;
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
 * 应用系统策略配置
 *
 * 2022-07
 */
@RestController
@RequestMapping("/appStrategyConfig")
public class AppStrategyConfigController {
    private static Logger logger = LoggerFactory.getLogger(AppStrategyConfigController.class);

    @Autowired
    private AppStrategyConfigService appStrategyConfigService;

    /**
     * 编辑应用系统的策略配置信息
     * @param tbConfS
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑应用系统的策略配置信息",notes="")
    @SysRequestLog(description="编辑应用系统的策略配置信息", actionType = ActionType.UPDATE,manually=false)
    public Result<String> updatesApp(@RequestBody List<TbConf> tbConfS){
        try{
            appStrategyConfigService.updateStrategyConfig(tbConfS);
            return ResultUtil.success("success");
        }catch (Exception e){
            logger.error("辑应用系统的策略配置信息异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"辑应用系统的策略配置信息异常");
        }
    }

    /**
     * 查询应用系统策略配置信息
     * @return
     */
    @PostMapping(value="/quey")
    @ApiOperation(value="查询应用系统策略配置信息",notes="")
    @SysRequestLog(description="查询应用系统策略配置信息", actionType = ActionType.SELECT,manually=false)
    public Result<List<TbConf>> queyConfigApps(){
        List<TbConf> lists = appStrategyConfigService.getStrategyConfigs();
        return ResultUtil.successList(lists);
    }
}
