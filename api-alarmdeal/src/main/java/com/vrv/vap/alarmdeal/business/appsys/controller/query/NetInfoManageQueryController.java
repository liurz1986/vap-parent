package com.vrv.vap.alarmdeal.business.appsys.controller.query;

import com.vrv.vap.alarmdeal.business.appsys.service.query.NetInfoManageQueryService;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoNewVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.es.enums.ResultCodeEnum;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 网路信息报表接口
 *
 * 2023-7-4
 * @author liurz
 */
@RestController
@RequestMapping(value="/netInfoManageQuery")
public class NetInfoManageQueryController {
    private static Logger logger = LoggerFactory.getLogger(NetInfoManageQueryController.class);

    @Autowired
    private NetInfoManageQueryService netInfoManageQueryService;

    /**
     * 网络基本情况汇总
     * 2023-07-04
     * *@return
     */
    @PostMapping(value = "/baseInfo")
    @ApiOperation(value = "网络基本情况汇总", notes = "")
    public Result<Map<String, Object>> baseInfo() {
        try {
            Map<String, Object> baseInfo = netInfoManageQueryService.baseInfo();
            return ResultUtil.success(baseInfo);
        } catch (Exception e) {
            logger.error("网络基本情况汇总异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络基本情况汇总异常");
        }
    }

    /**
     * 网络类型划分(局域网、广域网)
     * 2023-07-04
     * *@return
     */
    @PostMapping(value = "/netInfoType")
    @ApiOperation(value = "网络类型划分", notes = "")
    public Result<List<AssetStatisticsVO>> netInfoType() {
        try {
            List<AssetStatisticsVO> netInfoType = netInfoManageQueryService.netInfoType();
            return ResultUtil.successList(netInfoType);
        } catch (Exception e) {
            logger.error("网络类型划分异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络类型划分异常");
        }
    }

    /**
     * 网络密级统计
     * 2023-07-04
     * *@return
     */
    @PostMapping(value = "/secretlevelTotal")
    @ApiOperation(value = "网络密级统计", notes = "")
    public Result<List<AssetStatisticsVO>> secretlevelTotal() {
        try {
            List<AssetStatisticsVO> secretlevelTotal = netInfoManageQueryService.secretlevelTotal();
            return ResultUtil.successList(secretlevelTotal);
        } catch (Exception e) {
            logger.error("网络密级统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络密级统计异常");
        }
    }

    /**
     * 网络防护等级统计
     * 2023-07-04
     * *@return
     */
    @PostMapping(value = "/protectLevelTotal")
    @ApiOperation(value = "网络防护等级统计", notes = "")
    public Result<List<AssetStatisticsVO>> protectLevelTotal() {
        try {
            List<AssetStatisticsVO> protectLevelTotal = netInfoManageQueryService.protectLevelTotal();
            return ResultUtil.successList(protectLevelTotal);
        } catch (Exception e) {
            logger.error("网络防护等级统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络防护等级统计异常");
        }
    }

    /**
     * 网络按安全域统计
     * 2023-07-04
     * *@return
     */
    @PostMapping(value = "/domainTotal")
    @ApiOperation(value = "网络按安全域统计", notes = "")
    public Result<List<AssetStatisticsVO>> domainTotal() {
        try {
            List<AssetStatisticsVO> domainTotal = netInfoManageQueryService.domainTotal();
            return ResultUtil.successList(domainTotal);
        } catch (Exception e) {
            logger.error("网络按安全域统计异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络按安全域统计异常");
        }
    }
}