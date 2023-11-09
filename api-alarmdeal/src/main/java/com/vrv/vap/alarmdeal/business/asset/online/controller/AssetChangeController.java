package com.vrv.vap.alarmdeal.business.asset.online.controller;

import com.vrv.vap.alarmdeal.business.asset.online.model.AssetChange;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetChangeService;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetChangeVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.SerachAssetChangeVO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资产变更
 *
 *  2022-08
 */
@RestController
@RequestMapping(value="/assetchange")
public class AssetChangeController {
    private static Logger logger = LoggerFactory.getLogger(AssetChangeController.class);
    @Autowired
    private AssetChangeService assetChangeService;
    /**
     * 分页查询
     * @param serachAssetChangeVO
     * 1.按时间，处理人，处理意见进行一个或多个条件的检索
     * @return
     */
    @PostMapping(value="/getPage")
    @ApiOperation(value="资产变更分页查询",notes="")
    @SysRequestLog(description="资产变更分页查询", actionType = ActionType.SELECT,manually = false)
    public PageRes<AssetChange> query(@RequestBody SerachAssetChangeVO serachAssetChangeVO){
        PageRes<AssetChange> res = new PageRes();
        try{
            return assetChangeService.query(serachAssetChangeVO);
        }catch (Exception e){
            logger.error("分页查询异常",e);
            res.setMessage("分页查询异常");
            res.setCode(ResultCodeEnum.UNKNOW_FAILED.toString());
            return  res;
        }
    }

    /**
     * 处理
     * @param assetChangeVO
     * @return
     */
    @PostMapping(value="/handle")
    @ApiOperation(value="资产变更处置操作",notes="")
    @SysRequestLog(description="资产变更处置操作", actionType = ActionType.UPDATE,manually = false)
    public Result<AssetChange> handle(@RequestBody AssetChangeVO assetChangeVO){
        try{
            return assetChangeService.handle(assetChangeVO);
        }catch (Exception e){
            logger.error("处理异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"处理异常");
        }
    }
}
