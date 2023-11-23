package com.vrv.vap.alarmdeal.business.baseauth.controller;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthOverviewService;
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
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 审批信息概览
 * 2023-09
 * @author liurz
 */
@RestController
@RequestMapping(value="/baseAuthOverview-delete")
@ApiOperation(value= "审批信息概览")
public class BaseAuthOverviewController {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthOverviewController.class);

    @Autowired
    private BaseAuthOverviewService baseAuthOverviewService;

    /**
     * 总数统计及近一个月趋势图
     * 审批信息总数：all
     * 打印权限总数：print
     * 刻录权限总数：burn
     * 系统访问权限总数：access
     * 网络互联权限总数：inter
     * 运维权限总数：maint
     * @return
     */
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
            return baseAuthOverviewService.getTotalStatistics(type);
        }catch (Exception e){
            logger.error("审批信息概览-总数统计及近一个月趋势统计异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-总数统计及近一个月趋势统计异常");
        }
    }
    /**
     * 打印权限统计
     *  1. 目的对象类型为文件
     *  2. 操作类型打印
     */
    @GetMapping(value="/getPrintStatistics")
    @SysRequestLog(description="打印权限统计", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="打印权限统计",notes="")
    public Result<Map<String,Object>> getPrintStatistics(){
        try{
            return baseAuthOverviewService.getPrintStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-打印权限统计异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-打印权限统计异常");
        }
    }
    /**
     * 刻录权限统计
     * 1. 目的对象类型为文件
     * 2. 操作类型刻录
     */
    @GetMapping(value="/getBurnStatistics")
    @SysRequestLog(description="刻录权限统计", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="刻录权限统计",notes="")
    public Result<Map<String,Object>> getBurnStatistics(){
        try{
            return baseAuthOverviewService.getBurnStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-刻录权限统计异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-刻录权限统计异常");
        }
    }

    /**
     * 系统访问权限统计(内部用户终端)
     *  1.目的对象是用户终端(终端)
     *  2.操作类型为访问
     */
    @GetMapping(value="/getAccessHostStatistics")
    @SysRequestLog(description="系统访问权限统计(内部用户终端)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="系统访问权限统计(内部用户终端)",notes="")
    public Result<Map<String,Object>> getAccessHostStatistics(){
        try{
            return baseAuthOverviewService.getAccessHostStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-系统访问权限统计(内部用户终端)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-系统访问权限统计(内部用户终端)异常");
        }
    }
    /**
     * 系统访问权限统计(外部Ip)
     * 1. 源对象是外部Ip
     * 2. 操作类型为访问
     */
    @GetMapping(value="/getExternalAssetStatistics")
    @SysRequestLog(description="系统访问权限统计(外部Ip)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="系统访问权限统计(外部Ip)",notes="")
    public Result<Map<String,Object>> getExternalAssetStatistics(){
        try{
            return baseAuthOverviewService.getExternalAssetStatistics();
        }catch (Exception e){
            logger.error("审批信息概览-系统访问权限统计(外部Ip)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"审批信息概览-系统访问权限统计(外部Ip)异常");
        }
    }

    /**
     * 运维权限统计(数量统计)
     * 1. 源对象是运维终端
     * 2. 操作类型为运维
     */
    @GetMapping(value="/getMaintenFlagCountStatistics")
    @SysRequestLog(description="运维权限统计(数量统计)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="运维权限统计(数量统计)",notes="")
    public Result<List<CoordinateVO>> getMaintenFlagCountStatistics(){
        try{
            return baseAuthOverviewService.getMaintenFlagCountStatistics();
        }catch (Exception e){
            logger.error("运维权限统计(数量统计)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"运维权限统计(数量统计)异常");
        }
    }

    /**
     * 运维权限统计(月度变化)
     * 1. 源对象是运维终端
     * 2. 操作类型为运维
     */
    @GetMapping(value="/getMaintenFlagMonthStatistics")
    @SysRequestLog(description="运维权限统计(月度变化)", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="运维权限统计(月度变化)",notes="")
    public Result<List<TrendResultVO>> getMaintenFlagMonthStatistics(){
        try{
            return ResultUtil.successList(baseAuthOverviewService.getMaintenFlagMonthStatistics());
        }catch (Exception e){
            logger.error("运维权限统计(月度变化)异常：{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"运维权限统计(月度变化)异常");
        }
    }
}
