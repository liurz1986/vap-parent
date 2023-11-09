package com.vrv.vap.alarmdeal.business.asset.controller.query;


import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 大屏接口：对外大屏接口
 *
 * @author var
 * @data 2021-08-19
 */
@RestController
@RequestMapping(value = "/api/largescreen")
public class LargeScreenController {
    private static Logger logger = LoggerFactory.getLogger(LargeScreenController.class);

    @Autowired
    private AssetService assetService;

    /**
     * 获得资产总数
     *
     * @return Result
     */
    @GetMapping(value = "/queryAssetTotal")
    @ApiOperation(value = "获得资产总数", notes = "")
    @SysRequestLog(description = "获得资产总数", actionType = ActionType.SELECT)
    public Result<Long> queryAssetTotal() {
        try {
            long total = assetService.count();
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("获得资产总数异常,{}", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获得资产总数异常");
        }
    }

    /**
     * 统计不同资产类型下的资产数量（按照一级类型进行统计）
     *
     * @return Result
     */
    @GetMapping(value = "/queryAssetTypeNumber")
    @ApiOperation(value = "统计不同资产类型下的资产数量", notes = "")
    @SysRequestLog(description = "统计不同资产类型下的资产数量", actionType = ActionType.SELECT)
    public Result<List<Map<String, Object>>> queryAssetTypeNumber() {
        try {
            return ResultUtil.successList(assetService.queryAssetTypeNumber());
        } catch (Exception e) {
            logger.error("统计不同资产类型下的资产数量,{}", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "统计不同资产类型下的资产数量异常");
        }
    }

    /**
     * 资产数量按部门统计，统计不同部门下的资产数量
     *
     * @return Result
     */
    @GetMapping(value = "/queryDepartmentNumber")
    @ApiOperation(value = "统计不同部门下的资产数量", notes = "")
    @SysRequestLog(description = "统计不同部门下的资产数量", actionType = ActionType.SELECT)
    public Result<List<Map<String, Object>>> queryDepartmentNumber() {
        try {
            return ResultUtil.successList(assetService.queryDepartmentNumber());
        } catch (Exception e) {
            logger.error("统计不同部门下的资产数量异常,{}", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "统计不同部门下的资产数量异常");
        }
    }


    /**
     * 获得资产总数:带开始时间和结束时间
     * 条件
     * startTime：开始时间, endTime：结束时间
     * 2023-4-20
     * @return Result
     */
    @PostMapping(value = "/queryAssetCountFilter")
    @ApiOperation(value = "获得资产总数(带开始时间和结束时间条件)", notes = "")
    @SysRequestLog(description = "获得资产总数(带开始时间和结束时间条件)", actionType = ActionType.SELECT)
    public Result<Long> queryAssetTotalFilter(@RequestBody Map<String,Object> timeFilter) {
        try {
            Object startTimeObj = timeFilter.get("startTime");
            if(StringUtils.isEmpty(startTimeObj)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "开始时间不能为空！");
            }
            Date startTime  = DateUtil.parseDate(String.valueOf(startTimeObj),DateUtil.DEFAULT_DATE_PATTERN);
            Object endTimeObj = timeFilter.get("endTime");
            if(StringUtils.isEmpty(endTimeObj)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "结束时间不能为空！");
            }
            Date endTime  = DateUtil.parseDate(String.valueOf(endTimeObj),DateUtil.DEFAULT_DATE_PATTERN);
            long total = assetService.queryAssetTotalFilter(startTime,endTime);
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("获得资产总数(带开始时间和结束时间条件)异常,{}", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获得资产总数(带开始时间和结束时间条件)异常");
        }
    }

}
