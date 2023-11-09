package com.vrv.vap.alarmdeal.business.asset.datasync.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDiff;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetBookDiffService;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetBookDiffDetailVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetBookDiffSearchVO;
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
import java.util.List;

/**
 * 差异数据处理
 *
 * date:2023-4
 */
@RestController
@RequestMapping("/assetBookDiff")
public class AssetBookDiffController {
    private static Logger logger = LoggerFactory.getLogger(AssetBookDiffController.class);
    @Autowired
    private AssetBookDiffService assetBookDiffService;
    /**
     * 差异页面展示接口
     *
     * @return
     */
    @PostMapping(value="/getPage")
    @ApiOperation(value = "差异页面展示接口", notes = "")
    @SysRequestLog(description = "差异页面展示接口", actionType = ActionType.SELECT,manually = false)
    public PageRes<AssetBookDiff> getPage(@RequestBody AssetBookDiffSearchVO assetBookDiffSearchVO){
        logger.info("差异页面展示接口,请求参数："+ JSON.toJSONString(assetBookDiffSearchVO));
        return assetBookDiffService.getPage(assetBookDiffSearchVO);
    }
    /**
     * 获取差异详细数据
     * @return
     */
    @GetMapping(value="/getDiffDetails/{guid}")
    @ApiOperation(value = "获取差异详细数据", notes = "")
    @SysRequestLog(description = "获取差异详细数据", actionType = ActionType.SELECT,manually = false)
    public Result<List<AssetBookDiffDetailVO>> getDiffDetails(@PathVariable("guid") String guid){
        return assetBookDiffService.getDiffDetails(guid);
    }
    /**
     * 差异编辑详情确认处理
     *  1. 生成一条待入统一台账数据
     *  2. 异常记录更新已经处理
     *  3. 入统一台账库
     * @return
     */
    @PostMapping(value="/handle")
    @ApiOperation(value = "差异编辑详情确认处理", notes = "")
    @SysRequestLog(description = "差异编辑详情确认处理", actionType = ActionType.UPDATE,manually = false)
    public Result<String> handle(@RequestBody AssetBookDiffDetailVO assetBookDiffDetailVO){
        try{
            return assetBookDiffService.handle(assetBookDiffDetailVO);
        }catch (Exception e){
            logger.error("差异编辑详情确认处理异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"差异编辑详情确认处理异常");
        }
    }
}
