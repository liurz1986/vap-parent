package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.controller;

import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.SuperviseTaskService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskQueryVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskVo;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/4
 */

@Api(description = "督办或预警任务")
@RestController
@RequestMapping(value = "/superviseTask")
public class SuperviseTaskController extends BaseController {

    @Autowired
    private SuperviseTaskService superviseTaskService;

    /**
     * 分页查询  保密主管查看1:协办申请记录2:协办任务记录3:预警记录 （3个分页查询的接口） todo 3个接口
     *
     * @param superviseTaskQueryVo
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="分页查询",notes="")
    @SysRequestLog(description="督办或预警任务-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<SuperviseTask> getPage(@RequestBody SuperviseTaskQueryVo superviseTaskQueryVo){
        return superviseTaskService.getSuperviseTaskPage(superviseTaskQueryVo);
    }

    /**
     * 更新督办任务状态为“已处置”
     * @param analysisId  告警id
     * @return
     */
    @PostMapping("dealSuperviseTask/{analysisId}")
    @ApiOperation(value="更新督办任务状态为已处置",notes="")
    @SysRequestLog(description="督办或预警任务-更新督办任务状态为已处置", actionType = ActionType.UPDATE,manually=false)
    public Result<Boolean> dealSuperviseTask(@PathVariable String analysisId){
        Boolean bool=superviseTaskService.dealSuperviseTask(analysisId);
        return ResultUtil.success(bool);
    }

    /**
     * 响应督办/预警/协办  todo 预警和协办任务反馈(这个是)（2个接口）
     * 注意了：
     *
     * @param superviseTaskVo
     * @return
     */
    @PostMapping("completeSuperviseTask")
    @ApiOperation(value = "响应督办/预警", notes = "")
    @SysRequestLog(description="督办或预警任务-响应督办/预警", actionType = ActionType.UPDATE,manually=false)
    public Result<SuperviseTask> completeSuperviseTask(@RequestBody SuperviseTaskVo superviseTaskVo) {

        SuperviseTask superviseTask = superviseTaskService.responseSuperviseTask(superviseTaskVo);
        if(superviseTask==null){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"事件没有处置完成，不能够反馈督办！");
        }
        return ResultUtil.success(superviseTask);
    }

    /**
     * todo 待测试 协办申请 （1个接口）
     * 注意了：这里协办申请后，从上级来取反馈的结果，它的taskCreate都是 up；--->我主动发起的
     * 协办任务：从上级拉取协办任务，已经对协办任务处理后上报，它的taskCreate都是down--->别人发给我的
     */
    @PutMapping("addAssistingTask")
    @ApiOperation(value = "创建协办任务", notes = "")
    @SysRequestLog(description="督办或预警任务-创建协办任务", actionType = ActionType.ADD,manually=false)
    public Result<SuperviseTask> addAssistingTask(@RequestBody SuperviseTaskVo superviseTaskVo) {
        SuperviseTask superviseTask = superviseTaskService.addAssistingTask(superviseTaskVo);
        return ResultUtil.success(superviseTask);
    }


    @GetMapping("getSuperviseTaskTop/{count}")
    @ApiOperation(value = "督办任务top", notes = "")
    @SysRequestLog(description="督办或预警任务-督办任务top", actionType = ActionType.SELECT,manually=false)
    public Result<List<SuperviseTask>> getSuperviseTaskTop(@PathVariable("count") Integer count) {
        List<SuperviseTask> list = superviseTaskService.findSuperviseTaskTop(count);
        return ResultUtil.success(list);

    }

    @GetMapping("getWarningTop/{count}")
    @ApiOperation(value="预警top",notes="")
    @SysRequestLog(description="督办或预警任务-预警top", actionType = ActionType.SELECT,manually=false)
    public Result<List<SuperviseTask>>  getWarningTop(@PathVariable("count") Integer count){
        List<SuperviseTask> list=superviseTaskService.findWarningTop(count);
        return ResultUtil.success(list);
    }

    /**
     * 督办、预警统计
     * @return
     */
    @GetMapping("countSuperviseTask")
    @ApiOperation(value="督办、预警统计",notes="")
    @SysRequestLog(description="督办或预警任务-督办、预警统计", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String,Object>> countSuperviseTask(){
        Map<String,Object> result=superviseTaskService.countSuperviseTask();
        return ResultUtil.success(result);
    }

    @GetMapping("countTodayTask")
    @ApiOperation(value="今日督办、预警、协办统计",notes="")
    @SysRequestLog(description="督办或预警任务-今日督办、预警、协办统计", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String,Integer>> countTodayTask(){
        Map<String,Integer> map=new HashedMap();
        map.put("todaySuperTaskCount",superviseTaskService.getTodayDownTaskCount(SuperviseTask.SUPERVISE));
        map.put("todayWarningCount",superviseTaskService.getTodayDownTaskCount(SuperviseTask.WARNING));
        map.put("todayAssistingCount",superviseTaskService.getTodayDownTaskCount(SuperviseTask.ASSISTING_DOWN));
        return ResultUtil.success(map);
    }
    @GetMapping("sumTodayTask")
    @ApiOperation(value="今日下发任务总数",notes="")
    @SysRequestLog(description="督办或预警任务-今日下发任务总数", actionType = ActionType.SELECT,manually=false)
    public Result<Integer> sumTodayTask(){
        Integer count=superviseTaskService.getTodayDownTaskCount(null);
        return ResultUtil.success(count);
    }

    @GetMapping("countToDoTask")
    @ApiOperation(value="待办协办、待办预警、协办申请",notes="")
    @SysRequestLog(description="督办或预警任务-待办协办、待办预警、协办申请", actionType = ActionType.SELECT,manually=false)
    public  Result<Map<String,Integer>> countToDoTask(){
        Map<String, Integer> map = new HashMap<>();
        //展示下拉的数量
        map.put("todoAssistDownCount", superviseTaskService.getTaskCount(SuperviseTask.ASSISTING_DOWN, SuperviseTask.TASK_DOWN, SuperviseTask.TODO));
        map.put("todoWarningCount", superviseTaskService.getTaskCount(SuperviseTask.WARNING, SuperviseTask.TASK_DOWN, SuperviseTask.TODO));
        //展示我们申请的
        map.put("todoAssistUpCount", superviseTaskService.getTaskCount(SuperviseTask.ASSISTING_UP, SuperviseTask.TASK_UP, SuperviseTask.TODO));
        return ResultUtil.success(map);
    }

    @PostMapping("getSuperviseCountForSendTime")
    @ApiOperation(value="通过下发时间查询统计督办格式",notes="")
    @SysRequestLog(description="督办或预警任务-通过下发时间查询统计督办格式", actionType = ActionType.SELECT,manually=false)
    public  Result<Integer> getSuperviseCountForSendTime(@RequestBody EventDetailQueryVO query){
        Integer count=superviseTaskService.getSuperviseCountForSendTime(query);
        return ResultUtil.success(count);
    }











}
