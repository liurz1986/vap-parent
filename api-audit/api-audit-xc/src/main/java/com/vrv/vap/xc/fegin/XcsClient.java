package com.vrv.vap.xc.fegin;

import com.vrv.vap.xc.model.JobModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("api-audit-line")
public interface XcsClient {
    @PostMapping("task_offline/add")
    public void addTask(@RequestBody JobModel model);

    @PostMapping("task_offline/remove")
    public void removeTask(@RequestBody JobModel model);
}
