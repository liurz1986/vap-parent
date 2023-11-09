package com.vrv.vap.alarmdeal.business.asset.controller.query;

import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.query.AssetQueryService;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTypeTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.SafeDeviceListVO;
import com.vrv.vap.es.enums.ResultCodeEnum;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
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
 * 报表接口：资产分类统计
 *
 * 2022-01-14
 */
@RestController
@RequestMapping(value = "/assetClassifyQuery")
public class AssetQueryController {
    private static Logger logger = LoggerFactory.getLogger(AssetQueryController.class);

    @Autowired
    private AssetQueryService assetQueryService;

    @Autowired
    private AssetService assetService;

    /**
     * 1. 资产分类汇总统计：按资产类型统计
     *
     * 终端总数$，服务器总数$，网络设备总数$，安全设备总数$，其他设备数$。
     * @return Result
     */
    @PostMapping(value = "/queryAssetTypeTotal")
    @ApiOperation(value = "资产分类汇总统计", notes = "")
    @SysRequestLog(description="资产分类汇总统计", actionType = ActionType.SELECT,manually=false)
    public Result<AssetTypeTotalVO> queryAssetTypeTotal() {
        try {
            AssetTypeTotalVO assetTypeTotalVO = assetQueryService.queryAssetTypeTotal();
            return ResultUtil.success(assetTypeTotalVO);
        } catch (Exception e) {
            logger.error("资产分类汇总统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "资产分类汇总统计异常");
        }
    }

    /**
     * 2.按类型(一级资产类型)统计数量：终端数量按类型统计、服务器数量按类型统计、网络设备数量按类型统计、安全设备数量按类型统计
     *  按照二级资产类型、国产非国产进行分类统计
     *  终端设  ： assetHost
     *  服务器  ： assetService
     *  网络设备  ： assetNetworkDevice
     *  安全设备  ： assetSafeDevice
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetTypeTotalByTermType/{type}")
    @ApiOperation(value = "按类型统计资产数量", notes = "")
    @SysRequestLog(description="按类型统计资产数量", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetTypeTotalByTermType(@PathVariable("type") String type) {
        try {
            if(null == type){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "类型不能为空");
            }
            String[] assetTypeGroupyName={"assetHost","assetService","assetNetworkDevice","assetSafeDevice"};
            List<String> names = Arrays.asList(assetTypeGroupyName);
            if(!names.contains(type)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "请确认请求参数的有效性");
            }
            List<AssetStatisticsVO> asetStatisticsVOs = assetQueryService.queryAssetTypeTotalByTermType(type);
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error("按类型统计资产数量", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "按类型统计资产数量异常");
        }
    }

    /**
     * 3. 安全设备信息列表
     *
     * @return
     */
    @PostMapping(value = "/querySafeDeviceAssetList")
    @ApiOperation(value = "安全设备信息列表", notes = "")
    @SysRequestLog(description="安全设备信息列表", actionType = ActionType.SELECT,manually=false)
    public Result< List<SafeDeviceListVO>> querySafeDeviceAssetList() {
        try {

            List<SafeDeviceListVO> safeDeviceListVOs = assetQueryService.querySafeDeviceAssetList();
            return ResultUtil.successList(safeDeviceListVOs);
        } catch (Exception e) {
            logger.error("安全设备信息列表", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "安全设备信息列表异常");
        }
    }
    /**
     * 4. 其他设备数量按类型统计
     * 其他设备  ：刻录机、打印机、涉密专用介质
     * @return
     */
    @PostMapping(value = "/queryOtherAssetTotal")
    @ApiOperation(value = "其他设备数量按类型统计", notes = "")
    @SysRequestLog(description="其他设备数量按类型统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryOtherAssetTotal() {
        try {

            List<AssetStatisticsVO> asetStatisticsVOs = assetQueryService.queryOtherAssetNumber();
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error("其他设备数量按类型统计", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "其他设备数量按类型统计异常");
        }
    }

    /**
     * 5.终端数量按密级统计：
     * 密集等级：非密 、内部 、秘密 、机密 、绝密
     * @return
     */
    @PostMapping(value = "/queryAssetHostByLevelTotal")
    @ApiOperation(value = "终端数量按密级统计", notes = "")
    @SysRequestLog(description="终端数量按密级统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<AssetStatisticsVO>> queryAssetHostByLevelTotal() {
        try {
            List<AssetStatisticsVO> asetStatisticsVOs = assetQueryService.queryAssetByLevel("asset-Host");
            return ResultUtil.successList(asetStatisticsVOs);
        } catch (Exception e) {
            logger.error("终端数量按密级统计", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "终端数量按密级统计异常");
        }
    }

    /**
     * 6. 资产类型统计（按照一级类型进行统计）
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetTypeNumber")
    @ApiOperation(value = "统计不同资产类型下的资产数量", notes = "")
    @SysRequestLog(description = "统计不同资产类型下的资产数量", actionType = ActionType.SELECT)
    public Result<List<Map<String, Object>>> queryAssetTypeNumber() {
        try {
            return ResultUtil.successList(assetService.queryAssetTypeNumber());
        } catch (Exception e) {
            logger.error("统计不同资产类型下的资产数量,{}", e);
            return ResultUtil.error(com.vrv.vap.jpa.web.ResultCodeEnum.UNKNOW_FAILED.getCode(), "统计不同资产类型下的资产数量异常");
        }
    }

}
