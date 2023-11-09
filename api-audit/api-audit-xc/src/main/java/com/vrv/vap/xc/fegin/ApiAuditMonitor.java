package com.vrv.vap.xc.fegin;


import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.vo.Monitor2DataQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("api-audit-monitor")
public interface ApiAuditMonitor {
    @PostMapping("/v2/monitor/lastest")
    VData<Map<String, Object>> queryCpuInfo(@RequestBody Monitor2DataQuery query);
}
