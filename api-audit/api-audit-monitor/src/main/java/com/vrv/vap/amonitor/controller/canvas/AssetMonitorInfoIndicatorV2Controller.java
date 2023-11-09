package com.vrv.vap.amonitor.controller.canvas;

import com.vrv.vap.amonitor.entity.Monitor2Indicator;
import com.vrv.vap.amonitor.entity.Monitor2IndicatorView;
import com.vrv.vap.amonitor.service.MonitorV2AssetIndicatorViewHistoryService;
import com.vrv.vap.amonitor.service.MonitorV2IndicatorService;
import com.vrv.vap.amonitor.service.MonitorV2IndicatorViewService;
import com.vrv.vap.amonitor.vo.DeleteModel;
import com.vrv.vap.amonitor.vo.Monitor2IndicatorQuery;
import com.vrv.vap.amonitor.vo.Monitor2IndicatorViewQuery;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
public class AssetMonitorInfoIndicatorV2Controller {

    private static Log log = LogFactory.getLog(AssetMonitorInfoIndicatorV2Controller.class);

    @Autowired
    private MonitorV2IndicatorService service;

    @Autowired
    private MonitorV2IndicatorViewService monitorV2IndicatorViewService;

    @Autowired
    private MonitorV2AssetIndicatorViewHistoryService assetIndicatorViewHistoryService;

    @PostMapping("/v2/asset_monitor/indicator")
    @ApiOperation("分页查询指标")
    public VList<Monitor2Indicator> queryIndicatorByPage(@RequestBody Monitor2IndicatorQuery record) {
        VList<Monitor2Indicator> recordList = null;
        try {
            recordList = service.queryByPage(record);
        } catch (Exception e) {
            log.error("", e);
            recordList = new VList<Monitor2Indicator>(0, Collections.emptyList());
        }
        return recordList;
    }

    @PutMapping("/v2/asset_monitor/indicator")
    @ApiOperation("新增指标")
    public Result addIndicator(@RequestBody Monitor2Indicator record) {
        Result res = null;
        try {
            service.addItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @PatchMapping("/v2/asset_monitor/indicator")
    @ApiOperation("修改指标v2")
    public Result updateIndicator(@RequestBody Monitor2Indicator record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            record.setUpdateTime(LocalDateTime.now());
            service.updateItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @DeleteMapping("/v2/asset_monitor/indicator")
    @ApiOperation("删除指标")
    public Result deleteIndicatorItem(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            Monitor2Indicator param = new Monitor2Indicator();
            record.getIntegerIdList().forEach(r -> {
                param.setId(r);
                service.deleteItem(param);
            });
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }


    @PostMapping("/v2/asset_monitor/indicator_view")
    @ApiOperation("分页查询展示面板")
    public VList<Monitor2IndicatorView> queryIndicatorViewByPage(@RequestBody Monitor2IndicatorViewQuery record) {
        VList<Monitor2IndicatorView> recordList = null;
        try {
            recordList = monitorV2IndicatorViewService.queryByPage(record);
        } catch (Exception e) {
            log.error("", e);
            recordList = new VList<>(0, Collections.emptyList());
        }
        return recordList;
    }

    @PutMapping("/v2/asset_monitor/indicator_view")
    @ApiOperation("新增展示面板")
    public Result addIndicatorView(@RequestBody Monitor2IndicatorView record) {
        Result res = null;
        try {
            monitorV2IndicatorViewService.addItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @PatchMapping("/v2/asset_monitor/indicator_view")
    @ApiOperation("修改展示面板v2")
    public Result updateIndicatorView(@RequestBody Monitor2IndicatorView record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            monitorV2IndicatorViewService.updateItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @DeleteMapping("/v2/asset_monitor/indicator_view")
    @ApiOperation("删除展示面板")
    public Result deleteIndicatorViewItem(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            Monitor2IndicatorView param = new Monitor2IndicatorView();
            record.getIntegerIdList().forEach(r -> {
                param.setId(r);
                monitorV2IndicatorViewService.deleteItem(param);
            });
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

}
