package com.vrv.vap.alarmdeal.business.evaluation.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.evaluation.service.EvaluationReportService;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
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
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 自查自评报告
 *
 * @Date 2023-09
 * @author liurz
 */
@RestController
@RequestMapping(value="/evaluationReport")
public class EvaluationReportController {
   private static final Logger logger = LoggerFactory.getLogger(EvaluationReportController.class);

   @Autowired
   private EvaluationReportService evaluationReportService;

    /**
     * 自查自评结果
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     * 返回结果
     * {"code": 0,"msg": "成功","data": { "totalCount": 46, "finshCount": 4,"depCount": 8,"checkTypeCount": 6,"geneticTypeCount": 11,"eventCount": 4},"list": null}
     */
    @PostMapping("queryEvaluationResult")
    @ApiOperation(value="自查自评结果",notes="")
    @SysRequestLog(description = "自查自评结果", actionType = ActionType.SELECT, manually = false)
    public Result<SummaryVO> queryEvaluationResult(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("自查自评结果:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryEvaluationResult(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("自查自评结果异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"自查自评结果异常");
        }
    }
    /**
     * 自查自评状态统计
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     * 返回值：
     * {"code": 0,"msg": "成功","data": null,"list": [{"number": 42,"name": "未完成"},{"number": 4,"name": "已完成"}]}
     */
    @PostMapping("queryStatusStatistics")
    @ApiOperation(value="自查自评状态统计",notes="")
    @SysRequestLog(description = "自查自评状态统计", actionType = ActionType.SELECT, manually = false)
    public Result<Map<String,Object>> queryStatusStatistics(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("自查自评状态统计:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryStatusStatistics(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("自查自评状态统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"自查自评状态统计异常");
        }
    }
    /**
     * 自查自评状态统计列表
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     * 返回值：
     * {"code": 0,"msg": "成功","data": null,"list": [{"number": 42,"name": "未完成"},{"number": 4,"name": "已完成"}]}
     */
    @PostMapping("queryStatusList")
    @ApiOperation(value="自查自评状态统计列表",notes="")
    @SysRequestLog(description = "自查自评状态统计列表", actionType = ActionType.SELECT, manually = false)
    public Result<List<KeyValueVO>> queryStatusList(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("自查自评状态统计列表:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryStatusList(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("自查自评状态统计列表异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"自查自评状态统计列表异常");
        }
    }
    /**
     * 成因类型分类统计
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @PostMapping("queryGeneticTypeStatistics")
    @ApiOperation(value="成因类型分类统计",notes="")
    @SysRequestLog(description = "成因类型分类统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<KeyValueVO>> queryGeneticTypeStatistics(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("成因类型分类统计:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryGeneticTypeStatistics(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("成因类型分类统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"成因类型分类统计异常");
        }
    }

    /**
     * 处置问题分类统计(代码中写死数据，目前没有取值的地方)
     *
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @PostMapping("queryHandleIssuesStatistics")
    @ApiOperation(value="处置问题分类统计",notes="")
    @SysRequestLog(description = "处置问题分类统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<KeyValueVO>> queryHandlIssuesStatistics(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("处置问题分类统计:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryHandlIssuesStatistics(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("处置问题分类统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"处置问题分类统计异常");
        }
    }

    /**
     * 自查自评分类统计
     *  检查大类监管事件统计:checktype
     *  成因类型监管事件统计:geneticType
     *  信息化工作机构成因类型事件统计: inforOrg
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @PostMapping("queryStatisticsByType/{type}")
    @ApiOperation(value="自查自评分类统计",notes="")
    @SysRequestLog(description = "自查自评分类统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<KeyValueVO>> queryByTypeStatistics(@PathVariable("type") String type, @RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("自查自评分类统计:"+ JSON.toJSONString(evaluationReportSearchVO)+"; type的值："+type);
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryStatisticsByType(evaluationReportSearchVO,type);
        }catch (Exception e){
            logger.error("自查自评分类统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"自查自评分类统计异常");
        }
    }
    /**
     * 自查自评分类统计
     *  非信息化工作机构成因类型事件统计
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @PostMapping("queryNoInforOrgStatistic")
    @ApiOperation(value="非信息化工作机构成因类型事件统计",notes="")
    @SysRequestLog(description = "非信息化工作机构成因类型事件统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<NoInforOrgResultVO>> queryNoInforOrgStatistic(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("非信息化工作机构成因类型事件统计:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryNoInforOrgStatistic(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("非信息化工作机构成因类型事件统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"非信息化工作机构成因类型事件统计异常");
        }
    }

    /**
     * 自查自评项汇总统计
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @PostMapping("querySummaryStatistics")
    @ApiOperation(value="自查自评项汇总统计",notes="")
    @SysRequestLog(description = "自查自评项汇总统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<SummaryStatisticVO>> querySummaryStatistics(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("自查自评项汇总统计:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.querySummaryStatistics(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("自查自评项汇总统计异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"自查自评项汇总统计异常");
        }
    }

    /**
     * 各类成因类型监管事件详情
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     *
     * 备注：目前报表功能不支持，该接口暂时不用 2023-09-21
     */
    @PostMapping("queryGeneticTypeDetail")
    @ApiOperation(value="各类成因类型监管事件详情",notes="")
    @SysRequestLog(description = "各类成因类型监管事件详情", actionType = ActionType.SELECT, manually = false)
    public Result<List<GeneticTypeDetailVO>> queryGeneticTypeDetail(@RequestBody EvaluationReportSearchVO evaluationReportSearchVO){
        logger.debug("各类成因类型监管事件详情:"+ JSON.toJSONString(evaluationReportSearchVO));
        try{
            Result<String> validateRes = timeValidate(evaluationReportSearchVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateRes.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),validateRes.getMsg());
            }
            return evaluationReportService.queryGeneticTypeDetail(evaluationReportSearchVO);
        }catch (Exception e){
            logger.error("各类成因类型监管事件详情异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"各类成因类型监管事件详情异常");
        }
    }


    private Result<String> timeValidate(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        if(null == startTime){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"开始时间不能为空");
        }
        if(null == endTime){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"结束时间不能为空");
        }
        return ResultUtil.successList(null);
    }
}
