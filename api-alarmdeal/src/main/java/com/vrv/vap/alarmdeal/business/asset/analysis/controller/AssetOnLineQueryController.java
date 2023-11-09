package com.vrv.vap.alarmdeal.business.asset.analysis.controller;

import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetOnLineQueryService;
import com.vrv.vap.alarmdeal.business.asset.analysis.vo.QueryAssetCountChangeTrendVO;
import com.vrv.vap.alarmdeal.business.asset.analysis.vo.QueryAssetLineTypeVO;
import com.vrv.vap.alarmdeal.business.asset.analysis.vo.QueryAssetQuantityVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 资产分析查询
 *
 */
@RestController
@RequestMapping(value="/assetOnline/query")
public class AssetOnLineQueryController {
    private static Logger logger = LoggerFactory.getLogger(AssetOnLineQueryController.class);
    @Autowired
    private AssetOnLineQueryService assetOnLineService;


    /**
     * 资产分析中资产相关统计
     *
     * 发现资产、在线资产、资产在线比例、台账资产、未处理告警数
     *
     * @return
     */
    @GetMapping(value="/quantity")
    @ApiOperation(value="资产分析中资产相关统计",notes="")
    @SysRequestLog(description="资产分析中资产相关统计", actionType = ActionType.SELECT,manually = false)
    public Result<QueryAssetQuantityVO> quantity(){
        try{
            return ResultUtil.success(assetOnLineService.quantity());
        }catch (Exception e){
            logger.error(" 资产分析中资产相关统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode()," 资产分析中资产相关统计异常");
        }

    }

    /**
     * 资产产品分布：按资产大类分类统计
     *
     * @return
     */
    @GetMapping(value="/getCountByAssetTypeGroup")
    @ApiOperation(value="按资产大类分类统计",notes="")
    @SysRequestLog(description="资产分析中资产产品分布", actionType = ActionType.SELECT,manually = false)
    public Result<List<QueryAssetLineTypeVO>> getCountByAssetTypeGroup(){
        try{
            return ResultUtil.successList(assetOnLineService.getCountByAssetTypeGroup());
        }catch (Exception e){
            logger.error("获取资产产品分布异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取资产产品分布异常");
        }
    }
    /**
     * 资产类型分布：按资产小类分类统计
     *
     * @return
     */
    @GetMapping(value="/getCountByAssetType")
    @ApiOperation(value="按资产小类分类统计",notes="")
    @SysRequestLog(description="资产分析中资产类型分布", actionType = ActionType.SELECT,manually = false)
    public Result<List<QueryAssetLineTypeVO>> getCountByAssetType(){
        try{
            return ResultUtil.successList(assetOnLineService.getCountByAssetType());
        }catch (Exception e){
            logger.error("获取资产类型分布异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取资产类型分布异常");
        }
    }

    /**
     * 台账资产总数变化趋势：
     * type:表示周、月、季  week、month、quarter
     * @return
     */
    @GetMapping(value="/getTotalChangeTrend/{type}")
    @ApiOperation(value="台账资产总数变化趋势",notes="")
    @SysRequestLog(description="资产分析中台账资产总数变化趋势", actionType = ActionType.SELECT,manually = false)
    public Result<List<QueryAssetLineTypeVO>> getTotalChangeTrend(@PathVariable("type") String type){
        try{
            return ResultUtil.successList(assetOnLineService.getTotalChangeTrend(type));
        }catch (Exception e){
            logger.error("获取资产总数变化趋势异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取资产总数变化趋势异常");
        }
    }
    /**
     * 发现资产数量：
     * type:表示周、月、季  week、month、quarter
     * @return
     */
    @GetMapping(value="/getCountChange/{type}")
    @ApiOperation(value="发现资产数量",notes="")
    @SysRequestLog(description="资产分析中发现资产数量", actionType = ActionType.SELECT,manually = false)
    public Result<List<QueryAssetLineTypeVO>> getCountChange(@PathVariable("type") String type){
        try{
            return ResultUtil.successList(assetOnLineService.getCountChange(type));
        }catch (Exception e){
            logger.error("获取在线资产数量异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取在线资产数量异常");
        }
    }

    /**
     * 台账资产分类变化趋势：
     * type:表示周、月、季  week、month、quarter
     * @return
     */
    @GetMapping(value="/getCountChangeTrend/{type}")
    @ApiOperation(value="台账资产分类变化趋势",notes="")
    @SysRequestLog(description="资产分析中台账资产分类变化趋势", actionType = ActionType.SELECT,manually = false)
    public Result<QueryAssetCountChangeTrendVO> getCountChangeTrend(@PathVariable("type") String type){
        try{
            return ResultUtil.successList(assetOnLineService.getCountChangeTrend(type));
        }catch (Exception e){
            logger.error("获取资产数量变化趋势异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取资产数量变化趋势异常");
        }
    }
}
