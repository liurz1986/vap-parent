package com.vrv.vap.alarmdeal.business.evaluation.controller;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationConfigService;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
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
 * 自查自评策略配置
 *
 * @Date 2023-09
 * @author liurz
 */
@RestController
@RequestMapping(value="/evaluationConfig")
public class EvaluationConfigController {
    private static Logger logger = LoggerFactory.getLogger(EvaluationConfigController.class);

    @Autowired
    private SelfInspectionEvaluationConfigService selfInspectionEvaluationConfigService;
    /**
     * 展示列表
     *
     * {"checkType":"保密制度建设","start_":0,"count_":15}
     * @return
     */
    @PostMapping("")
    @ApiOperation(value = "自查自评策略配置展示列表分页查询", notes = "")
    @SysRequestLog(description="自查自评策略配置展示列表分页查询", actionType = ActionType.SELECT,manually = false)
    public PageRes<SelfInspectionEvaluationConfig> getPage(@RequestBody SelfInspectionEvaluationConfigSearchVO data) {
        return selfInspectionEvaluationConfigService.getPage(data);
    }

    /**
     * 编辑前判断
     *   当推荐策略下存在未处置完的自查自评项，请先处置完成后再修改该策略参数！
     * {"id":策略id}
     * @return
     */
    @PostMapping("editValidate")
    @ApiOperation(value = "自查自评策略配置编辑前判断", notes = "")
    @SysRequestLog(description="自查自评策略配置编辑前判断", actionType = ActionType.SELECT,manually = false)
    public Result<String> editValidate(@RequestBody SelfInspectionEvaluationConfigVO data) {
        try{
            return selfInspectionEvaluationConfigService.editValidate(data);
        }catch (Exception e){
            logger.error("自查自评策略配置编辑前判断异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评策略配置编辑前判断异常");
        }

    }
    /**
     * 编辑
     *
     *
     * @return
     */
    @PatchMapping("")
    @ApiOperation(value = "自查自评策略配置编辑", notes = "")
    @SysRequestLog(description="自查自评策略配置编辑", actionType = ActionType.UPDATE,manually = false)
    public  Result<SelfInspectionEvaluationConfig> edit(@RequestBody SelfInspectionEvaluationConfigVO data) {
        try{
            return selfInspectionEvaluationConfigService.edit(data);
        }catch (Exception e){
            logger.error("自查自评策略配置编辑异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评策略配置编辑异常");
        }
    }

    /**
     * 自查自评推荐策略配置左侧树
     *
     * @return
     */
    @GetMapping("getTree")
    @ApiOperation(value = "自查自评推荐策略配置检查大类列表", notes = "")
    @SysRequestLog(description="自查自评推荐策略配置检查大类列表", actionType = ActionType.SELECT,manually = false)
    public  Result<List<ConifgTreeVO>> getTree() {
        try{
            return selfInspectionEvaluationConfigService.getTree();
        }catch (Exception e){
            logger.error("自查自评推荐策略配置检查大类列表异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评推荐策略配置检查大类列表异常");
        }
    }

    /**
     * 推荐策略详情弹窗页或自查自评推荐策略信息
     * {"id":12}
     *
     * @return
     */
    @PostMapping("getDetail")
    @ApiOperation(value = "推荐策略详情弹窗页或自查自评推荐策略信息", notes = "")
    @SysRequestLog(description="推荐策略详情弹窗页或自查自评推荐策略信息", actionType = ActionType.SELECT,manually = false)
    public  Result<ConfigDeatilVO> getDetail(@RequestBody Map<String,Object> data) {
        try{
            String idStr = data.get("id")==null?"":String.valueOf(data.get("id"));
            if(StringUtils.isEmpty(idStr)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "id的值不能为空");
            }
            return selfInspectionEvaluationConfigService.getDetail(Integer.parseInt(idStr));
        }catch (Exception e){
            logger.error("获取推荐策略详情弹窗页或自查自评推荐策略信息异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取推荐策略详情弹窗页或自查自评推荐策略信息异常");
        }
    }

    @GetMapping("getAllCheckTypeAndGeneticType")
    @ApiOperation(value = "获取策略中所有检查大类和成因类型", notes = "")
    @SysRequestLog(description="获取策略中所有检查大类和成因类型", actionType = ActionType.SELECT,manually = false)
    public  Result<Map<String,Object>> getAllCheckTypeAndGeneticType() {
        try{
            return selfInspectionEvaluationConfigService.getAllCheckTypeAndGeneticType();
        }catch (Exception e){
            logger.error("获取策略中所有检查大类和成因类型异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取策略中所有检查大类和成因类型异常");
        }
    }
}
