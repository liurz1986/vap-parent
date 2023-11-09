package com.vrv.vap.xc.controller.es;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.service.XgsDataChannelService;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class XgsDataChannelController {

    @Autowired
    private XgsDataChannelService xgsDataChannelService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @GetMapping("/cascade/summary")
    @ApiOperation("es数据概览")
    public Map<String, Object> esDataSummary() {
        return xgsDataChannelService.esDataSummary();
    }

    @GetMapping("/xgs/index/data/count")
    @ApiOperation("当天与前一天入库量")
    public VData<Map<String, Object>> dataCount() {
        return VoBuilder.vd(xgsDataChannelService.dataCount());
    }

    @PostMapping("/xgs/index/data/level")
    @ApiOperation("当天日志风险级别 传时间 不传默认为当天")
    public VData<List<Map<String, Object>>> dataLevel(@RequestBody PageModel model) {
        return VoBuilder.vd(xgsDataChannelService.dataLevel(model));
    }

    @PostMapping("/xgs/index/data/kind")
    @ApiOperation("当天日志行为类别 传时间 不传默认为当天")
    public VData<List<Map<String, Object>>> dataKind(@RequestBody PageModel model) {
        return VoBuilder.vd(xgsDataChannelService.dataKind(model));
    }

    @PostMapping("/xgs/index/data/alert")
    @ApiOperation("当天日志 告警事件 传时间 不传默认为当天")
    public VData<List<Map<String, Object>>> dataAlert(@RequestBody PageModel model) {
        return VoBuilder.vd(xgsDataChannelService.dataAlert(model));
    }

    @GetMapping("/xgs/index/data/top")
    @ApiOperation("当天设备日志排行")
    public VData<Map<String, Object>> dataTop() {
        return VoBuilder.vd(xgsDataChannelService.dataTop());
    }

    @GetMapping("/xgs/index/data/trend")
    @ApiOperation("24小时入库趋势图")
    public VData<List<Map<String, Object>>> dataTrend() {
        return VoBuilder.vd(xgsDataChannelService.dataTrend());
    }

    @GetMapping("/xgs/data/upload")
    @ApiOperation("es数据上报kafka")
    public void esDataSendKafka() {
        xgsDataChannelService.esDataSendKafka();
    }

    @GetMapping("/xgs/data/upload/test")
    @ApiOperation("test")
    public void test() {
        xgsDataChannelService.test();
    }

    @PostMapping("/xgs/index/data/alert/last")
    @ApiOperation("最新告警信息 count 多少条")
    public VData<List<Map<String, String>>> dataAlertLastInfo(@RequestBody PageModel model) {
        return VoBuilder.vd(xgsDataChannelService.dataAlertLastInfo(model));
    }
}
