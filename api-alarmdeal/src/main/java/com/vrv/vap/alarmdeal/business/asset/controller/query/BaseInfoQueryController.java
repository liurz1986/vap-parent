package com.vrv.vap.alarmdeal.business.asset.controller.query;

import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.query.AssetQueryService;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTotalStatisticsVO;
import com.vrv.vap.es.enums.ResultCodeEnum;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * 报表接口：基础信息报表统计查询接口
 *
 * 2022 - 01 -14
 */
@RestController
@RequestMapping(value = "/baseAssetQuery")
public class BaseInfoQueryController {
    private static Logger logger = LoggerFactory.getLogger(BaseInfoQueryController.class);

    @Autowired
    private AssetQueryService assetQueryService;

    /**
     * 1. 资产总数统计
     *
     * 资产总数${资产总数}，其中绝密资产$台，机密资产$台，秘密资产$台，内部资产$台，非密资产$台；国产设备$台，非国产设备$台
     * @return Result
     */
    @PostMapping(value = "/queryAssetTotal")
    @ApiOperation(value = "资产总数统计", notes = "")
    @SysRequestLog(description="资产总数统计", actionType = ActionType.SELECT,manually=false)
    public Result<AssetTotalStatisticsVO> queryAssetTotalStatistics() {
        try {
            AssetTotalStatisticsVO assets = assetQueryService.queryAssetTotalStatistics();
            return ResultUtil.success(assets);
        } catch (Exception e) {
            logger.error("资产总数统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资资产总数统计异常");
        }
    }

        /**
         * 2. 资产数量按类型统计(二级资产类型)
         *
         *
         * @return Result
         */
        @PostMapping(value = "/queryAssetByAssetType")
        @ApiOperation(value = "资产数量按二级资产类型统计", notes = "")
        @SysRequestLog(description="资产数量按二级资产类型统计", actionType = ActionType.SELECT,manually=false)
        public Result<List<AssetStatisticsVO>> queryAssetStatistics () {
            try {
                List<AssetStatisticsVO> assets = assetQueryService.queryAssetByAssetType();
                return ResultUtil.successList(assets);
            } catch (Exception e) {
                logger.error("资产数量按二级资产类型统计异常", e);
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按二级资产类型统计异常");
            }
        }

    /**
     * 3. 资产数量按部门统计
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetByDepartment")
    @ApiOperation(value = "资产数量按部门统计", notes = "")
    @SysRequestLog(description="资产数量按部门统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByDepartment () {
        try {
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByDepartment();
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按部门统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按部门统计异常");
        }
    }

    /**
     * 4. 资产数量按密级统计
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetByLevel")
    @ApiOperation(value = "资产数量按密级统计", notes = "")
    @SysRequestLog(description="资产数量按密级统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByLevel () {
        try {
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByLevel(null);
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按密级统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按密级统计异常");
        }
    }

    /**
     * 资产数量按密级统计,资产一级类型筛选
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetByLevelType")
    @ApiOperation(value = "资产数量按密级统计,资产一级类型筛选", notes = "")
    @SysRequestLog(description="资产数量按密级统计,资产一级类型筛选", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByLevelType (@RequestBody Map<String,String> map) {
        try {
            String type = map.get("type");
            if (StringUtils.isBlank(type)){
                return ResultUtil.error(ResultCodeEnum.ERROR.getCode(), "资产数量按密级统计参数异常");
            }
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByLevelType(type);

            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按密级统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按密级统计异常");
        }
    }
    /**
     * 资产数量按资产二级类型统计
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetNumByAssetType")
    @ApiOperation(value = "资产数量按资产二级类型统计", notes = "")
    @SysRequestLog(description="资产数量按资产二级类型统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByAssetType (@RequestBody Map<String,String> map) {
        try {
            String type = map.get("type");
            if (StringUtils.isBlank(type)){
                return ResultUtil.error(ResultCodeEnum.ERROR.getCode(), "资产数量按密级统计参数异常");
            }
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetNumByAssetType(type);
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按密级统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按密级统计异常");
        }
    }
    /**
     * 3. 资产数量按部门统计,一级类型筛选
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetByDepartmentType")
    @ApiOperation(value = "资产数量按类型筛选部门统计", notes = "")
    @SysRequestLog(description="资产数量按类型筛选部门统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByDepartmentType (@RequestBody Map<String,String> map) {
        try {
            String type = map.get("type");
            if (StringUtils.isBlank(type)){
                return ResultUtil.error(ResultCodeEnum.ERROR.getCode(), "资产数量按类型筛选部门统计参数异常");
            }
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByDepartmentType(type);
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按部门统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按类型筛选部门统计异常");
        }
    }
    /**
     * 5. 资产数量按价值统计
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetByWorth")
    @ApiOperation(value = "资产数量按价值统计", notes = "")
    @SysRequestLog(description="资产数量按价值统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByWorth() {
        try {
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByWorth();
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按价值统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按价值统计异常");
        }
    }

    @Autowired
    private AssetService assetService;
    /**
     * 6统计不同资产类型下的资产数量（按照一级类型进行统计）
     *
     * @return Result
     */
    @GetMapping(value = "/queryAssetType")
    @ApiOperation(value = "统计不同资产类型下的资产数量", notes = "")
    @SysRequestLog(description = "统计不同资产类型下的资产数量", actionType = ActionType.SELECT)
    public Result<List<Map<String, Object>>> queryAssetType() {
        try {
            List<Map<String, Object>> mapList = assetService.queryAssetTypeNumber();
            Long number = mapList.stream().mapToLong(s -> (Long) s.get("number")).sum();
            BigDecimal bigDecimal = new BigDecimal("0");
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, Object> map = mapList.get(i);
                Long number1 = (Long)map.get("number");
                if (number>0){
                    if (i<mapList.size()+1){
                        BigDecimal divide = new BigDecimal(number1).divide(new BigDecimal(number), 2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                        map.put("ratio",divide);
                        bigDecimal= bigDecimal.add(divide);
                    }else {
                        map.put("ratio",new BigDecimal(100).subtract(bigDecimal));
                    }
                }
            }
            return ResultUtil.successList(mapList);
        } catch (Exception e) {
            logger.error("统计不同资产类型下的资产数量,{}", e);
            return ResultUtil.error(com.vrv.vap.jpa.web.ResultCodeEnum.UNKNOW_FAILED.getCode(), "统计不同资产类型下的资产数量异常");
        }
    }

    /**
     * 7. 资产数量按安全域统计
     *
     * 2023-07-04
     * @return Result
     */
    @PostMapping(value = "/queryAssetByDomain")
    @ApiOperation(value = "资产数量按安全域统计", notes = "")
    @SysRequestLog(description="资产数量按安全域统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByDomain () {
        try {
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByDomain();
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按安全域统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按安全域统计异常");
        }
    }
    /**
     *8. 其他设备类型统计（除终端、服务器、网络设备、安全设备一级资产类型外的设备）
     *
     *2023-07-04
     * @return Result
     */
    @PostMapping(value = "/queryAssetByOther")
    @ApiOperation(value = "其他设备类型统计", notes = "")
    @SysRequestLog(description="其他设备类型统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetByOther () {
        try {
            List<AssetStatisticsVO> assets = assetQueryService.queryAssetByOther();
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("其他设备类型统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "其他设备类型统计异常");
        }
    }
    /**
     * 5. 资产数量按安全域统计
     *
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetByArea")
    @ApiOperation(value = "资产数量按安全域统计", notes = "")
    @SysRequestLog(description="资产数量按安全域统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> queryAssetByArea() {
        try {
            List<NameValue> assets = assetQueryService.queryAssetByArea();
            return ResultUtil.successList(assets);
        } catch (Exception e) {
            logger.error("资产数量按安全域统计", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产数量按安全域统计");
        }
    }
}