package com.vrv.vap.line.fegin;

import com.vrv.vap.line.model.BaseLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("api-audit-xc")
public interface AuditXcClient {

    /**
     * 获取所有基线任务
     *
     * @return
     */
    @GetMapping("/base_line/findAllEnable")
    public List<BaseLine> findAllEnable();
}
