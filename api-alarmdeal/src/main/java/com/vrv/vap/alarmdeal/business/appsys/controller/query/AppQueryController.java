package com.vrv.vap.alarmdeal.business.appsys.controller.query;

import com.vrv.vap.alarmdeal.business.appsys.service.query.AppQueryService;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoNewVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppQueryTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.es.enums.ResultCodeEnum;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用系统报表接口
 * @author liurz
 */
@RestController
@RequestMapping(value="/appQuery")
public class AppQueryController {
    private static Logger logger = LoggerFactory.getLogger(AppQueryController.class);
    @Autowired
    private AppQueryService appQueryService;

    /**
     * 应用汇总统计
     *
     * 应用总数$，其中涉密应用数$，非涉密应用数$
     * @return
     */
    @PostMapping(value = "/queryAppTotal")
    @ApiOperation(value = "应用汇总统计", notes = "")
    public Result<AppQueryTotalVO> queryAppTotal(){
        try {
            AppQueryTotalVO data = appQueryService.appQueryTotal();
            return ResultUtil.success(data);
        } catch (Exception e) {
            logger.error("应用汇总统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用汇总统计异常");
        }
    }

    /**
     * 应用数量按类型统计：改为按密集等级统计
     * @return
     */
    @PostMapping(value = "/queryAppSecretlevelTotal")
    @ApiOperation(value = " 应用密集等级统计", notes = "")
    public Result<List<AssetStatisticsVO>> queryAppSecretlevelTotal(){
        try {
            List<AssetStatisticsVO> asetStatisticsVOs = appQueryService.queryAppSecretlevelTotal();
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error(" 应用数量按类型统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用密集等级统计异常");
        }
    }

    /**
     * 应用信息统计 ：
     * @return
     */
    @PostMapping(value = "/queryAppInfoTotal")
    @ApiOperation(value = "应用信息统计", notes = "")
    public Result< List<AppInfoVO>> queryAppInfoTotal(){
        try {
            List<AppInfoVO> appInfoVos = appQueryService.queryAppInfoTotal();
            return ResultUtil.successList(appInfoVos);
        } catch (Exception e) {
            logger.error("应用信息统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用信息统计异常");
        }
    }

    /**
     * 应用信息统计列表
     * 2023-07-04
     * *@return
     */
    @PostMapping(value = "/queryAppTabulation")
    @ApiOperation(value = "应用信息统计列表", notes = "")
    public Result< List<AppInfoNewVO>> queryAppTabulation(){
        try {
            List<AppInfoNewVO> appInfoVos = appQueryService.queryAppTabulation();
            return ResultUtil.successList(appInfoVos);
        } catch (Exception e) {
            logger.error("应用信息统计列表异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用信息统计列表异常");
        }
    }
    /**
     * 应用数量按密集等级统计
     * @return
     */
    @GetMapping(value = "/countAppSecretStatistics")
    @ApiOperation(value = " 应用密集等级统计", notes = "")
    public Result<List<NameValue>> countAppSecretStatistics(){
        try {
            List<NameValue> asetStatisticsVOs = appQueryService.countAppSecretStatistics();
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error(" 应用数量按类型统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用密集等级统计异常");
        }
    }
    /**
     * 应用按部门统计排名
     * @return
     */
    @GetMapping(value = "/countAppOgrStatistics")
    @ApiOperation(value = " 应用按部门统计排名 ", notes = "")
    public Result<List<NameValue>> countAppOgrStatistics(){
        try {
            List<NameValue> asetStatisticsVOs = appQueryService.countAppOgrStatistics();
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error("应用按部门统计排名异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用按部门统计排名异常");
        }
    }
    /**
     * 应用数量按角色统计
     * @return
     */
    @GetMapping(value = "/countAppRoleStatistics")
    @ApiOperation(value = " 应用数量按角色统计 ", notes = "")
    public Result<List<NameValue>> countAppRoleStatistics(){
        try {
            List<NameValue> asetStatisticsVOs = appQueryService.countAppRoleStatistics();
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error("应用数量按角色统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用数量按角色统计异常");
        }
    }
}
