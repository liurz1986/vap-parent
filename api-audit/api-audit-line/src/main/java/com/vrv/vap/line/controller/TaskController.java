package com.vrv.vap.line.controller;

import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.model.BaseLine;
import com.vrv.vap.line.model.JobModel;
import com.vrv.vap.line.schedule.TaskLoader;
import com.vrv.vap.line.service.TaskService;
import com.vrv.vap.line.tools.LineTaskRun;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private BaseLineMapper baseLineMapper;

    @PostMapping("task_offline/add")
    @ApiOperation("增加任务")
    public void addJob(@RequestBody JobModel model) {
        TaskLoader.addJob(model,model.getParams());
    }

    @PostMapping("task_offline/remove")
    @ApiOperation("删除任务")
    public void removeJob(@RequestBody JobModel model) {
        TaskLoader.removeJob(model);
    }
    @GetMapping("/dolineByids")
    public Result dolineByids(String ids){
        List<BaseLine> all = baseLineMapper.selectBatchIds(Arrays.asList(ids.split(",")));
        LineTaskRun run = new LineTaskRun();
        for(BaseLine ln : all){
            //line.run("cs",m);
            try{
                run.runLine(ln);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
        return VoBuilder.success();
    }
}
