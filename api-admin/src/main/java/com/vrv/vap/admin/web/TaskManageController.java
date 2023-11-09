package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.common.manager.TaskManager;
import com.vrv.vap.admin.model.JobModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 更新周期任务
 */
@RestController
public class TaskManageController extends ApiController {
    @PostMapping("/updateTask")
    @ApiOperation("更新周期任务")
    public Result updateTask() {
        JobModel jobModel = new JobModel();
        jobModel.setJobName("serviceMonitor");
        TaskManager.removeJob(jobModel);
        TaskManager.addServiceMonitorTask();
        TaskManager.resumeJob("serviceMonitor");
        return result(true);
    }
}
