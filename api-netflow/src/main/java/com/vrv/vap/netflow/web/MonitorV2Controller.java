package com.vrv.vap.netflow.web;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.netflow.common.enums.MonitorTypeEnum;
import com.vrv.vap.netflow.vo.MonitorAppsInfoVO;
import com.vrv.vap.netflow.vo.MonitorDevicesUploadVO;
import com.vrv.vap.netflow.vo.MonitorReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动对象日志上传接口
 * 定时上传状态资产信息日志，每1h上传一次
 * 基础数据存储
 */
@RestController
@RequestMapping(path = "/V2")
@Api(value = "活动对象日志上传")
public class MonitorV2Controller {
    private final Logger logger = LoggerFactory.getLogger(MonitorV2Controller.class);

    @PostMapping(path = "/target_ident/devices")
    @ApiOperation("数据上报")
    public MonitorReturnVO monitorTargetDeviceData(@RequestBody MonitorDevicesUploadVO monitorDevicesUploadVO) {
        logger.info(String.format("数据上报：%s", JSON.toJSONString(monitorDevicesUploadVO)));
        //TODO 活动对象日志接口用处？
        return new MonitorReturnVO(MonitorTypeEnum.TYPE_SUCCESS.getType(), MonitorTypeEnum.TYPE_SUCCESS.getDesc());
    }

    @PostMapping(path = "/target_ident/apps")
    @ApiOperation("活动对象应用数据")
    public MonitorReturnVO monitorTargetAppsData(@RequestBody MonitorAppsInfoVO appsInfoVO) {
        logger.info(String.format("活动对象应用数据：%s", JSON.toJSONString(appsInfoVO)));
        //TODO 活动对象应用数据接口用处？
        return new MonitorReturnVO(MonitorTypeEnum.TYPE_SUCCESS.getType(), MonitorTypeEnum.TYPE_SUCCESS.getDesc());
    }

}
