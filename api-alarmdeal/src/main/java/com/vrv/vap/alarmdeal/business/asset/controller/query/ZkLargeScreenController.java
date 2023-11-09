package com.vrv.vap.alarmdeal.business.asset.controller.query;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.query.ZkLargeScreenService;
import com.vrv.vap.alarmdeal.business.asset.vo.query.ZkLargeSearchVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 中科综合大屏：
 * @author liurz
 * @date 2023-03-07
 */

@RestController
@RequestMapping(value = "/integratedlargeScreen")
public class ZkLargeScreenController {
    private static Logger logger = LoggerFactory.getLogger(ZkLargeScreenController.class);

    @Autowired
    private ZkLargeScreenService zkLargeScreenService;
    /**
     * 综合大屏概览：服务器、终端总数、应用系统统计
     *
     * @return Result
     */
    @GetMapping(value = "/queryOverview")
    @ApiOperation(value = "综合大屏概览：服务器、终端总数、应用系统统计", notes = "")
    @SysRequestLog(description = "综合大屏概览：服务器、终端总数、应用系统统计", actionType = ActionType.SELECT)
    public Result<ZkLargeSearchVO> queryOverview() {
        try {
            return ResultUtil.success(zkLargeScreenService.queryOverview());
        } catch (Exception e) {
            logger.error("综合大屏概览：服务器、终端总数、应用系统统计异常,{}", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "综合大屏概览：服务器、终端总数、应用系统统计异常");
        }
    }

}
