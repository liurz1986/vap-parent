package com.vrv.vap.amonitor.controller.statistic;

import com.vrv.vap.amonitor.entity.AssetStatistic;
import com.vrv.vap.amonitor.service.AssetMonitorInfoV2Service;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MonitorAssetStatisticController {

    @Autowired
    private AssetMonitorInfoV2Service service;

    @GetMapping("/asset/online/top")
    @ApiOperation("在线资产Top")
    public VData<List<Map<String, Object>>> selectAssetOnlineTop() {
        List<Map<String, Object>> maps = service.selectAssetOnlineTop();
        return VoBuilder.vd(maps);
    }

    @GetMapping("/asset/online/statistic")
    @ApiOperation("在线/离线资产统计")
    public VData<AssetStatistic> statisticAssetCount() {
        return service.statisticAssetCount();
    }

}
