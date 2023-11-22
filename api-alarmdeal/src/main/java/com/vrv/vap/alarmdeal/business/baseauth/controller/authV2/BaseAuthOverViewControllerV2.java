package com.vrv.vap.alarmdeal.business.baseauth.controller.authV2;

import com.vrv.vap.alarmdeal.business.baseauth.controller.BaseAuthOverviewController;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthOverviewService;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthService;
import com.vrv.vap.alarmdeal.business.baseauth.util.BaseAuthUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.CoordinateVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendResultVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/baseAuthOverview")
@ApiOperation(value= "审批信息概览")
public class BaseAuthOverViewControllerV2 {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthOverViewControllerV2.class);

    @Autowired
    private BaseAuthService baseAuthService;
    @GetMapping(value="/getTotalStatistics/{type}")
    @SysRequestLog(description="总数统计及近一个月趋势统计", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="总数统计及近一个月趋势统计",notes="")
    public Result<Map<String, Object>> getTotalStatistics(@PathVariable("type") String type){
        try{
            if(StringUtils.isEmpty(type)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"type的值不能为空！");
            }
            if(!BaseAuthUtil.isExistType(type)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"确认type的值是不是正确！");
            }
            return baseAuthService.getTotalStatisticsV2(type);
        }catch (Exception e){
            logger.error("审批信息概览-总数统计及近一个月趋势统计异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-总数统计及近一个月趋势统计异常");
        }
    }
    @GetMapping(value="/getPrintStatistics")
    @SysRequestLog(description="打印权限统计", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="打印权限统计",notes="")
    public Result<Map<String,Object>> getPrintStatistics(){
        try{
            return baseAuthService.getPrintStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-打印权限统计异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-打印权限统计异常");
        }
    }
    @GetMapping(value="/getBurnStatistics")
    @SysRequestLog(description="刻录权限统计", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="刻录权限统计",notes="")
    public Result<Map<String,Object>> getBurnStatistics(){
        try{
            return baseAuthService.getBurnStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-刻录权限统计异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-刻录权限统计异常");
        }
    }
    @GetMapping(value="/getAccessHostStatistics")
    @SysRequestLog(description="系统访问权限统计(内部用户终端)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="系统访问权限统计(内部用户终端)",notes="")
    public Result<Map<String,Object>> getAccessHostStatistics(){
        try{
            return baseAuthService.getAccessHostStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-系统访问权限统计(内部用户终端)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-系统访问权限统计(内部用户终端)异常");
        }
    }
    @GetMapping(value="/getExternalAssetStatistics")
    @SysRequestLog(description="系统访问权限统计(外部Ip)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="系统访问权限统计(外部Ip)",notes="")
    public Result<Map<String,Object>> getExternalAssetStatistics(){
        try{
            return baseAuthService.getExternalAssetStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-系统访问权限统计(外部Ip)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-系统访问权限统计(外部Ip)异常");
        }
    }
    @GetMapping(value="/getMaintenFlagCountStatistics")
    @SysRequestLog(description="运维权限统计(数量统计)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="运维权限统计(数量统计)",notes="")
    public Result<List<CoordinateVO>> getMaintenFlagCountStatistics(){
        try{
            return baseAuthService.getMaintenFlagCountStatistics();
        }catch (Exception e){
            logger.error("运维权限统计(数量统计)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"运维权限统计(数量统计)异常");
        }
    }
    @GetMapping(value="/getMaintenFlagMonthStatistics")
    @SysRequestLog(description="运维权限统计(月度变化)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="运维权限统计(月度变化)",notes="")
    public Result<List<TrendResultVO>> getMaintenFlagMonthStatistics(){
        try{
            return ResultUtil.successList(baseAuthService.getMaintenFlagMonthStatistics());
        }catch (Exception e){
            logger.error("运维权限统计(月度变化)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"运维权限统计(月度变化)异常");
        }
    }
}
