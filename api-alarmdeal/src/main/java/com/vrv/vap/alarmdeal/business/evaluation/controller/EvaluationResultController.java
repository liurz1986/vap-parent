package com.vrv.vap.alarmdeal.business.evaluation.controller;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EventQueUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 自查自评结果
 *
 * @Date 2023-09
 * @author liurz
 */
@RestController
@RequestMapping(value="/evaluation")
public class EvaluationResultController {
    private static Logger logger = LoggerFactory.getLogger(EvaluationResultController.class);

    @Autowired
    private SelfInspectionEvaluationService selfInspectionEvaluationService;

    /**
     * 展示列表
     * 检查大类、成因类型、自查自评状态、自查自评项产生时间及自查自评开展时间的时间范围进行条件查询；同时支持根据待查部门名称进行模糊匹配查询
     * {"checkType":"保密制度建设","start_":0,"count_":15}
     * @return
     */
    @PostMapping("")
    @ApiOperation(value = "自查自评结果展示列表分页查询", notes = "")
    @SysRequestLog(description="自查自评结果展示列表分页查询", actionType = ActionType.SELECT,manually = false)
    public PageRes<SelfInspectionEvaluation> getPage(@RequestBody SelfInspectionEvaluationSearchVO data) {
        return selfInspectionEvaluationService.getPage(data);
    }

    /**
     * 执行自查自评
     *  {"id": "55b5122331bb418dbcd4464048ed1f80","evResult": "自查自评结果","rectification": "整改情况说明"}
     * @param data
     * @return
     */
    @PostMapping("execute")
    @ApiOperation(value = "执行自查自评", notes = "")
    @SysRequestLog(description="执行自查自评", actionType = ActionType.UPDATE,manually = false)
    public Result<String> execute(@RequestBody SelfInspectionEvaluationVO data) {
        try{
            return selfInspectionEvaluationService.execute(data);
        }catch (Exception e){
            logger.error("执行自查自评异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "执行自查自评异常");
        }
    }

    /**
     * 自查自评结果状态统计
     * 环形图展示所有机构的自查自评项总数、已自查自评项总数、未进行自查自评项总数
     * @return
     */
    @GetMapping("statusStatistics")
    @ApiOperation(value = "自查自评结果状态统计", notes = "")
    @SysRequestLog(description="自查自评结果状态统计", actionType = ActionType.SELECT,manually = false)
    public Result<Map<String,Object>> statusStatistics() {
        try{
            return selfInspectionEvaluationService.statusStatistics();
        }catch (Exception e){
            logger.error("自查自评结果状态统计异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评结果状态统计异常");
        }
    }

    /**
     * 自查自评结果按成因类型统计分类，统计每个成因类型下的每个待查部门的数据
     * @return
     */
    @GetMapping("depAndGeneticStatistics")
    @ApiOperation(value = "自查自评结果按待查部门、成因类型统计", notes = "")
    @SysRequestLog(description="自查自评结果按待查部门、成因类型统计", actionType = ActionType.SELECT,manually = false)
    public Result<Map<String,Object>> depAndGeneticStatistics() {
        try{
            return selfInspectionEvaluationService.depAndGeneticStatistics();
        }catch (Exception e){
            logger.error("自查自评结果按待查部门、成因类型统计异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评结果按待查部门、成因类型统计异常");
        }
    }


    /**
     * 自查自评结果页面详情
     * @return
     */
    @GetMapping("getDetailById/{id}")
    @ApiOperation(value = "自查自评结果详情", notes = "")
    @SysRequestLog(description="自查自评结果详情", actionType = ActionType.SELECT,manually = false)
    public Result<EvaluationResultDeatilVO> getDetailById(@PathVariable("id") String id) {
        try{
            return selfInspectionEvaluationService.getDetailById(id);
        }catch (Exception e){
            logger.error("自查自评结果状态统计异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评结果状态统计异常");
        }
    }

    /**
     * 通过结果id获取关联的事件ID
     * @return
     */
    @GetMapping("getEventIds/{id}")
    @ApiOperation(value = "通过结果id获取关联的事件ID", notes = "")
    @SysRequestLog(description="通过结果id获取关联的事件ID", actionType = ActionType.SELECT,manually = false)
    public Result<List<String>> getEventIds(@PathVariable("id") String id) {
        try{
            SelfInspectionEvaluation beean = selfInspectionEvaluationService.getOne(id);
            String eventIds = beean.getEventIds();
            String[] eventIdStr = eventIds.split(",");
            List<String> eids = Arrays.asList(eventIdStr);
            return ResultUtil.successList(eids);
        }catch (Exception e){
            logger.error("通过结果id获取关联的事件ID异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "通过结果id获取关联的事件ID异常");
        }
    }

    /**
    /**
     * 测试
     *
     * @return
     */
    @PostMapping("/testData")
    @ApiOperation(value = "测试", notes = "")
    @SysRequestLog(description="测试", actionType = ActionType.SELECT,manually = false)
    public Result<String> getTree(@RequestBody EventResultVO data) {
        EventQueUtil.put(data);
        return ResultUtil.success("success");
    }
}
