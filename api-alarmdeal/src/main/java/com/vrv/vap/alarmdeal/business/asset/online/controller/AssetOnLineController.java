package com.vrv.vap.alarmdeal.business.asset.online.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.BaseSynchKafkaVO;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineService;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.SerachAssetOnLineV0;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 发现资产
 *
 * 2022-08
 */

@RestController
@RequestMapping(value="/assetOnline")
public class AssetOnLineController {
    private static Logger logger = LoggerFactory.getLogger(AssetOnLineController.class);
    @Autowired
    private AssetOnLineService assetOnLineService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 分页查询
     * @param serachAssetOnLineV0
     * 按IP地址、发现方式、在线状态
     * @return
     */
    @PostMapping(value="/getPage")
    @ApiOperation(value="发现资产分页查询",notes="")
    @SysRequestLog(description="发现资产分页查询", actionType = ActionType.SELECT,manually = false)
    public PageRes<AssetOnLineVO> query(@RequestBody SerachAssetOnLineV0 serachAssetOnLineV0){
        PageRes<AssetOnLineVO> res = new PageRes();
        try{
            return assetOnLineService.query(serachAssetOnLineV0);
        }catch (Exception e){
            logger.error("分页查询异常",e);
            res.setMessage("分页查询异常");
            res.setCode(ResultCodeEnum.UNKNOW_FAILED.toString());
            return  res;
        }

    }

    /**
     * 删除
     * @param serachAssetOnLineV0
     * @return
     */
    @PostMapping(value="/delete")
    @ApiOperation(value="发现资产删除操作",notes="")
    @SysRequestLog(description="发现资产删除操作", actionType = ActionType.DELETE,manually = false)
    public Result<String> delete(@RequestBody SerachAssetOnLineV0 serachAssetOnLineV0){
        try{
            if(StringUtils.isEmpty(serachAssetOnLineV0.getGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"guid不能为空!");
            }
            assetOnLineService.deleteByGuid(serachAssetOnLineV0.getGuid());
            return ResultUtil.success("success");
        }catch (Exception e){
            logger.error("删除异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"删除异常");
        }
    }

    /**
     * 批量设置
     * @param assetOnLineVO
     *
     * 资产类型(二级资产类型)， 操作系统，责任用户，责任部门
     * @return
     */
    @PostMapping(value="/batchSetting")
    @ApiOperation(value="发现资产批量设置",notes="")
    @SysRequestLog(description="发现资产批量设置", actionType = ActionType.UPDATE,manually = false)
    public Result<String> batchSetting(@RequestBody AssetOnLineVO assetOnLineVO){
        try{
            return assetOnLineService.batchSetting(assetOnLineVO);
        }catch (Exception e){
            logger.error("批量设置异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"批量设置异常");
        }
    }

    /**
     * 导入台账
     * @param assetOnLineVO
     * @return
     */
    @PostMapping(value="/writeAsset")
    @ApiOperation(value="发现资产导入台账",notes="")
    @SysRequestLog(description="发现资产导入台账", actionType = ActionType.ADD,manually = false)
    public Result<String> writeAsset(@RequestBody AssetOnLineVO assetOnLineVO){
        try{
            if(StringUtils.isEmpty(assetOnLineVO.getGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"guid不能为空!");
            }
            return assetOnLineService.writeAsset(assetOnLineVO);
        }catch (Exception e){
            logger.error("导入台账异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导入台账异常");
        }
    }

    /**
     * 生成导出excle文件
     * 涉及场景：
     * 1. 全量导出
     * 2. 筛选条件导出
     * @param serachAssetOnLineV0
     * @return
     */
    @PostMapping(value="/exportAssetOnLineInfo")
    @ApiOperation(value="发现资产导出操作",notes="")
    @SysRequestLog(description="发现资产导出操作", actionType = ActionType.EXPORT,manually = false)
    public Result<String> exportAssetOnLineInfo(@RequestBody SerachAssetOnLineV0 serachAssetOnLineV0){
        return  assetOnLineService.exportAssetOnLineInfo(serachAssetOnLineV0);
    }

    /**
     * 下载文件
     * @param response
     */
    @GetMapping(value="/exportAssetOnLineFile/{fileName}")
    @ApiOperation(value="发现资产导出下载文件",notes="")
    @SysRequestLog(description="发现资产导出下载文件", actionType = ActionType.EXPORT,manually = false)
    public void exportAssetOnLineFile(@PathVariable  String fileName, HttpServletResponse response){
        assetOnLineService.exportAssetOnLineFile(fileName,response);
    }


    /**
     * 模拟入库的接口
     *
     * @param assetKafkaVO
     */
    @PostMapping(value="/testKafka")
    public Result<String> testKafka(@RequestBody BaseSynchKafkaVO assetKafkaVO){
        kafkaTemplate.send("sync-base-data-asset-online", JSON.toJSONString(assetKafkaVO));
        return ResultUtil.success("success");
    }
}
