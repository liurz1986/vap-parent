package com.vrv.vap.alarmdeal.business.asset.controller.query;

import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.SafeSecretProduceService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
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
 * 对外基础数据接口
 *
 * @author var
 * @data 2021-08-20
 */
@RestController
@RequestMapping(value = "/api/basedata")
public class BaseDataController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(BaseDataController.class);

    @Autowired
    private AssetService assetService;

    @Autowired
    private SafeSecretProduceService safeSecretProduceService;

    /**
     * 获得终端设备信息(支持分页)
     *
     * @return PageRes
     */
    @PostMapping(value = "/queryAssetHosts")
    @ApiOperation(value = "终端设备信息(支持分页)", notes = "")
    @SysRequestLog(description = "终端设备信息(支持分页)", actionType = ActionType.SELECT)
    public PageRes<Map<String, Object>> queryAssetHosts(@RequestBody AssetSearchVO assetSearchVO) {
        PageRes<Map<String, Object>> pageRes = new PageRes<Map<String, Object>>();
        try {
            if (assetSearchVO == null) {
                assetSearchVO = new AssetSearchVO();
            }
            if (assetSearchVO.getCount_() == null || assetSearchVO.getCount_() < 0) {
                assetSearchVO.setCount_(10);
            }
            if (assetSearchVO.getStart_() == null || assetSearchVO.getStart_() < 0) {
                assetSearchVO.setStart_(0);
            }
            String responsibleCode = assetSearchVO.getResponsibleCode();
            if (StringUtils.isEmpty(responsibleCode)) {
                pageRes.setCode("-1");
                pageRes.setMessage("用户账号为空");
                return pageRes;
            }
            logger.info("current responsibleCode:{}" , responsibleCode);
            pageRes = assetService.queryAssetHostsPager(assetSearchVO);
            return pageRes;
        } catch (Exception e) {
            logger.error("获得终端设备信息异常{}", e);
            pageRes.setCode("-1");
            pageRes.setMessage("获得终端设备信息异常");
            return pageRes;
        }
    }

    /**
     * 查询usb设备存储介质（支持分页）
     *
     * @return PageRes
     */
    @PostMapping(value = "/queryUSBMemorys")
    @ApiOperation(value = "usb设备存储介质（支持分页）", notes = "")
    @SysRequestLog(description = "use设备存储介质（支持分页）", actionType = ActionType.SELECT)
    public PageRes<Map<String, Object>> queryUSBMemorys(@RequestBody AssetSearchVO assetSearchVO) {
        PageRes<Map<String, Object>> pageRes = new PageRes<Map<String, Object>>();
        try {
            if (assetSearchVO == null) {
                assetSearchVO = new AssetSearchVO();
            }
            if (assetSearchVO.getCount_() == null || assetSearchVO.getCount_() < 0) {
                assetSearchVO.setCount_(10);
            }

            if (assetSearchVO.getStart_() == null || assetSearchVO.getStart_() < 0) {
                assetSearchVO.setStart_(0);
            }
            String responsibleCode = assetSearchVO.getResponsibleCode();
            if (StringUtils.isEmpty(responsibleCode)) {
                pageRes.setCode("-1");
                pageRes.setMessage("用户账号为空");
                return pageRes;
            }
            logger.info("current responsibleCode:" + responsibleCode);
            return assetService.queryUSBMemorysPager(assetSearchVO);
        } catch (Exception e) {
            logger.error("查询usb设备存储介质（支持分页）异常", e);
            pageRes.setCode("-1");
            pageRes.setMessage("查询usb设备存储介质异常");
            return pageRes;
        }
    }

    /**
     * 查询USB外设（支持分页）
     *
     * @return PageRes
     */
    @PostMapping(value = "/queryUSBPeripherals")
    @ApiOperation(value = "USB外设（支持分页）", notes = "")
    @SysRequestLog(description = "USB外设（支持分页）", actionType = ActionType.SELECT)
    public PageRes<Map<String, Object>> queryUSBPeripherals(@RequestBody AssetSearchVO assetSearchVO) {
        PageRes<Map<String, Object>> pageRes = new PageRes<Map<String, Object>>();
        try {
            if (assetSearchVO == null) {
                assetSearchVO = new AssetSearchVO();
            }
            if (assetSearchVO.getCount_() == null || assetSearchVO.getCount_() < 0) {
                assetSearchVO.setCount_(10);
            }
            if (assetSearchVO.getStart_() == null || assetSearchVO.getStart_() < 0) {
                assetSearchVO.setStart_(0);
            }
            String responsibleCode = assetSearchVO.getResponsibleCode();
            if (StringUtils.isEmpty(responsibleCode)) {
                pageRes.setCode("-1");
                pageRes.setMessage("用户账号为空");
                return pageRes;
            }
            logger.info("current responsibleCode:{}" , responsibleCode);
            return assetService.queryUSBPeripheralsPager(assetSearchVO);
        } catch (Exception e) {
            logger.error("查询USB外设异常,{}", e);
            pageRes.setCode("-1");
            pageRes.setMessage("查询USB外设异常");
            return pageRes;
        }
    }


    /**
     * 获取终端上安装安全保密产品数量
     *
     * @return Result
     */
    @PostMapping(value = "/queryAssetHostSafeNums")
    @ApiOperation(value = "终端上安装安全保密产品数量", notes = "")
    @SysRequestLog(description = "终端上安装安全保密产品数量", actionType = ActionType.SELECT)
    public Result<Long> queryAssetHostSafeNums(@RequestBody AssetSearchVO assetSearchVO) {
        try {
            String responsibleCode = assetSearchVO.getResponsibleCode();
            if (StringUtils.isEmpty(responsibleCode)) {
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户账号为空");
            }
            logger.info("current responsibleCode:{}" , responsibleCode);
            long total = assetService.queryAssetHostSafeNums(responsibleCode);
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("获取终端上安装安全保密产品数量异常{}", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取终端上安装安全保密产品数量异常");
        }
    }

    /**
     * 获取安全保密产品安装情况
     *
     * @return Result
     */
    @PostMapping(value = "/querySafeProductInfo")
    @ApiOperation(value = "安全保密产品安装情况", notes = "")
    @SysRequestLog(description = "安全保密产品安装情况", actionType = ActionType.SELECT)
    public Result<List<Map<String, Object>>> querySafeProductInfo(@RequestBody AssetSearchVO assetSearchVO) {
        try {
            String responsibleCode = assetSearchVO.getResponsibleCode();
            if (StringUtils.isEmpty(responsibleCode)) {
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户账号为空");
            }
            logger.info("current responsibleCode:{}" , responsibleCode);
            List<Map<String, Object>> safes = assetService.querySafeProductInfo(responsibleCode);
            return ResultUtil.successList(safes);
        } catch (Exception e) {
            logger.error("获取安全保密产品安装情况异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取安全保密产品安装情况异常");
        }
    }

    // ===============工作台项相关接口=========================================2021-09-14
    // 工作台获取终端资产数量  user:用户终端  om：运维终端
    @GetMapping(value = "/workplatform/queryAssetHostNum")
    @ApiOperation(value = "工作台获取终端资产数量", notes = "")
    @SysRequestLog(description = "工作台获取终端资产数量", actionType = ActionType.SELECT)
    public Result<Long> queryAssetHostNum(@RequestParam("type") String type) {
        try {
            String typeData="";
            if(StringUtils.isEmpty(type)){
                typeData = "asset-Host"; // 不传的话默认为 用户终端
            }else{
                if ("user".equals(type)){
                    typeData = "asset-Host"; //用户终端
                }else{
                    typeData = "asset-MaintenHost"; //运维终端
                }
            }
            long total = assetService.queryWorkplatformnNum(typeData);
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("获取终端资产数量异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取终端资产数量异常");
        }
    }

    // 工作台获取服务器资产数量
    @GetMapping(value = "/workplatform/queryAssetServiceNum")
    @ApiOperation(value = "工作台获取服务器资产数量", notes = "")
    @SysRequestLog(description = "工作台获取服务器资产数量", actionType = ActionType.SELECT)
    public Result<Long> queryAssetServiceNum() {
        try {
            long total =assetService.queryWorkplatformnNum("asset-service");
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("获取服务器资产数量异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取服务器资产数量异常");
        }
    }

    // 工作台获取网络设备资产数量
    @GetMapping(value = "/workplatform/queryAssetNetworkDeviceNum")
    @ApiOperation(value = "工作台获取网络设备资产数量", notes = "")
    @SysRequestLog(description = "工作台获取网络设备资产数量", actionType = ActionType.SELECT)
    public Result<Long> queryAssetNetworkDeviceNum() {
        try {
            long total = assetService.queryWorkplatformnNum("asset-NetworkDevice");
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("获取网络设备资产数量异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取网络设备资产数量异常");
        }
    }

    // 工作台获取保密安全产品资产数量
    @GetMapping(value = "/workplatform/queryAssetSafeDeviceNum")
    @ApiOperation(value = "工作台获取保密安全产品资产数量", notes = "")
    @SysRequestLog(description = "工作台获取保密安全产品资产数量", actionType = ActionType.SELECT)
    public Result<Long> queryAssetSafeDeviceNum() {
        try {
            long total = assetService.queryWorkplatformnNum("asset-SafeDevice");
            return ResultUtil.success(total);
        } catch (Exception e) {
            logger.error("工作台获取保密安全产品资产数量异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取保密安全产品资产数量异常");
        }
    }
}
