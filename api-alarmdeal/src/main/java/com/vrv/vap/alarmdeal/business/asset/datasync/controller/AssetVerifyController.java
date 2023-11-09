package com.vrv.vap.alarmdeal.business.asset.datasync.controller;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetBookDetailService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetVerifyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifySearchVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyVO;
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

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/assetVerify")
public class AssetVerifyController {
    private static Logger logger = LoggerFactory.getLogger(AssetVerifyController.class);

    @Autowired
    private AssetVerifyService assetVerifyService;
    @Autowired
    private AssetBookDetailService assetBookDetailService;
    /**
     * 待申库编辑入库前校验
     * 校验ip、序列号、mac唯一性；ip格式有效性
     * @param asetVerifyVO
     * @return
     */
    @PostMapping(value="/validateData")
    @ApiOperation(value = "待申库编辑入库前校验", notes = "")
    @SysRequestLog(description = "待申库编辑入库前校验", actionType = ActionType.SELECT,manually = false)
    public Result<AssetVerifyVO> validateData(@RequestBody AssetVerifyVO asetVerifyVO){
        try{
            Result<String> result = assetVerifyService.validateData(asetVerifyVO);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),result.getMsg());
            }
            return ResultUtil.success(asetVerifyVO);
        }catch (Exception e){
            logger.error("数据校验异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"校验异常");
        }
    }

    /**
     * 待申库数据编辑
     * @param asetVerifyVO
     * @return
     */
    @PostMapping("")
    @ApiOperation(value = "待申库数据编辑", notes = "")
    @SysRequestLog(description = "待申库数据编辑", actionType = ActionType.UPDATE,manually = false)
    public Result<String> saveEditdData(@RequestBody AssetVerifyVO asetVerifyVO){
        try{
            return assetVerifyService.saveEditdData(asetVerifyVO);
        }catch (Exception e){
            logger.error("编辑异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑异常");
        }
    }

    /**
     * 执行忽略
     * @param asetVerifyVO
     * @return
     */
    @PostMapping(value="/neglect")
    @ApiOperation(value = "执行忽略", notes = "")
    @SysRequestLog(description = "执行忽略", actionType = ActionType.UPDATE,manually = false)
    public Result<String> neglect(@RequestBody AssetVerifyVO asetVerifyVO){
        try{
            if(StringUtils.isEmpty(asetVerifyVO.getGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"guid不能为空!");
            }
            return assetVerifyService.neglect(asetVerifyVO.getGuid());
        }catch (Exception e){
            logger.error("忽略异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"忽略异常");
        }
    }

    /**
     * 待申表查询
     * @param assetVerifySearchVO
     * @return
     */
    @PostMapping(value="/getPage")
    @ApiOperation(value = "待申表查询", notes = "")
    @SysRequestLog(description = "待申表查询", actionType = ActionType.SELECT,manually = false)
    public PageRes<AssetVerifyVO> query(@RequestBody AssetVerifySearchVO assetVerifySearchVO){
        return assetVerifyService.query(assetVerifySearchVO);
    }

    /**
     * 单条记录入库
     * @param assetVerifyVO
     * @return
     */
    @PostMapping(value="/saveAsset")
    @ApiOperation(value = "待申表单条记录入库", notes = "")
    @SysRequestLog(description = "待申表单条记录入库", actionType = ActionType.ADD,manually = false)
    public Result<String> saveAsset(@RequestBody AssetVerifyVO assetVerifyVO){
        try{
            if(StringUtils.isEmpty(assetVerifyVO.getGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"guid不能为空!");
            }
            return assetVerifyService.saveAsset(assetVerifyVO.getGuid());
        }catch (Exception e){
            logger.error("单条记录入库异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"单条记录入库异常");
        }
    }

    /**
     * 批量入库
     * @return
     */
    @PostMapping(value="/batchSaveAsset")
    @ApiOperation(value = "待申表批量入库", notes = "")
    @SysRequestLog(description = "待申表批量入库", actionType = ActionType.ADD,manually = false)
    public Result<String> batchSaveAsset (){
        try{
            return assetVerifyService.batchSaveAsset();
        }catch (Exception e){
            logger.error("批量入库异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"批量入库异常");
        }
    }

    /**
     * 生成导出excle文件
     * 涉及场景：
     * 1. 全量导出
     * 2. 筛选条件导出
     * 2023-4-14
     * @param assetVerifySearchVO
     * @return
     */
    @PostMapping(value="/exportAssetInfo")
    @ApiOperation(value="统一台账导出操作",notes="")
    @SysRequestLog(description="统一台账导出操作", actionType = ActionType.EXPORT,manually = false)
    public Result<String> exportAssetOnLineInfo(@RequestBody AssetVerifySearchVO assetVerifySearchVO){
        return  assetVerifyService.exportAssetInfo(assetVerifySearchVO);
    }

    /**
     * 下载文件
     *  2023-4-14
     * @param response
     */
    @GetMapping(value="/exportAssetile/{fileName}")
    @ApiOperation(value="统一台账导出下载文件",notes="")
    @SysRequestLog(description="统一台账导出下载文件", actionType = ActionType.EXPORT,manually = false)
    public void exportAssetOnLineFile(@PathVariable  String fileName, HttpServletResponse response){
        assetVerifyService.exportAssetFile(fileName,response);
    }

    /**
     手动入库开始比对逻辑：
     1. 根据手动策略配置中选择的数据源、查询这些数据源在台账明显表中的所有数据
     2. 按照手动策略中唯一标识进行分组
     3. 分组后数据处理
     4. 所有数据都要与正式库数据比较
     5. 正式库数据存在,比较没有差异，用正式库数据作为最终入统一台账数据
     6. 正式库数据不存在,比较没有差异，除了比对规则字段外其他字段随机取
     7. 比较有差异，入统一台账差异表中
     2023-4-14
     * @return
     */
    @PostMapping(value="/comparison")
    @ApiOperation(value = "手动入库开始比对", notes = "")
    @SysRequestLog(description = "手动入库开始比对", actionType = ActionType.ADD,manually = false)
    public Result<String> comparison(){
        assetBookDetailService.comparison();
        return ResultUtil.success("开始比对处理中，请稍等！");
    }

}
