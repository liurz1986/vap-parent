package com.vrv.vap.alarmdeal.business.flow.work.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.*;
import com.vrv.vap.alarmdeal.business.flow.core.service.*;
import com.vrv.vap.alarmdeal.business.flow.core.vo.SearBusinessVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.alarmdeal.business.flow.processdef.util.ExecutorServiceVrvUtil;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.TransferFlowByCallBackVo;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.locks.Condition;

/**
 * 具体流程流转的controller
 * @author lijihong
 *
 */
@RestController
@RequestMapping("flowworkrest")
public class FlowWorkRestController extends BaseController {

    private static final String PARAMS = "params";
    private static Logger logger = LoggerFactory.getLogger(FlowWorkRestController.class);


    @Autowired
    private FlowService flowService;
    @Autowired
    private MyTicketService myTicketService;
    @Autowired
    private BusinessIntanceService businessIntanceService;
    @Autowired
    private BusinessTaskLogService businessTaskLogService;
    @Autowired
    private BusinessTaskService businessTaskService;
    @Autowired
    private ListenerConfigService listenerConfigService;


    @PostMapping("create")
    @ApiOperation(value="处理工单的历史记录",notes="")
    @SysRequestLog(description="处理工单的历史记录", actionType = ActionType.ADD,manually=false)
    public Result<BusinessIntance> create(@RequestBody WorkDataVO datas) {
        return businessTaskService.createTicket(datas);
    }

    /**
     * 批量创建流程： 2021-08-30
     * 1.异步处理
     * 2.创建方法加同步锁
     * 3.创建之间间隔一秒钟
     * @param datas
     * @return
     * @throws InterruptedException
     */
    @PostMapping("/batchCreate")
    @ApiOperation(value="批量创建流程",notes="")
    @SysRequestLog(description="批量创建流程", actionType = ActionType.ADD,manually=false)
    public Result<Boolean> batchCreate(@RequestBody List<WorkDataVO> datas) {
        Result<Boolean> result = new Result<Boolean>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setData(true);
        if (null == datas || datas.size() == 0) {
            result.setMsg("没有批量创建数据！");
            return result;
        }
        // 异步线程池处理
        ExecutorServiceVrvUtil.getThreddPool().submit(new Runnable() {
            @Override
            public void run() {
                logger.info("批量创建流程开始");
                logger.info("批量创建流程信息数量：" + datas.size());
                long start = System.currentTimeMillis();
                try {
                    for (WorkDataVO data : datas) {
                        businessTaskService.createTicket(data);
                        Thread.sleep(1000);
                    }
                    logger.info("批量创建流程成功");
                    logger.info("批量创建流程总时间：" + (System.currentTimeMillis()-start));
                } catch (Exception e) {
                    logger.error("批量创建流程异常", e);
                }
            }
        });
        result.setMsg("正在处理过程中，请稍后查看！");
        return result;
    }

    @GetMapping("/getTaskFormKey/{taskId}")
    @ApiOperation(value="获得任务表单信息",notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId",value="任务Id",required=true,dataType="String")
    })
    @SysRequestLog(description="获得任务表单信息", actionType = ActionType.SELECT,manually=false)
    public Result<String> getTaskFormKey(@PathVariable String taskId) throws UnsupportedEncodingException {
        String formKey = flowService.getTaskFormKey(taskId);
        if(StringUtils.isNotEmpty(formKey)){
            formKey = URLDecoder.decode(formKey,"utf-8"); // 新版要解密
        }
        Result<String> result = ResultUtil.success(formKey);
        return result;
    }


    @GetMapping("/getStartFormKey/{deployId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deployId",value="发布Id",required=true,dataType="String")
    })
    @ApiOperation(value="获得开始表单信息",notes="")
    @SysRequestLog(description="获得开始表单信息", actionType = ActionType.SELECT,manually=false)
    public Result<String> getStartFormKey(@PathVariable String deployId) throws UnsupportedEncodingException {
        String formKey = flowService.getStartFormKey(deployId);
        Map<String,Object> map=new HashMap<>();
        if(StringUtils.isNotEmpty(formKey)){
            formKey = URLDecoder.decode(formKey,"utf-8");
            map= JSON.parseObject(formKey);
        }
        MyTicket myTicket=new MyTicket();
        myTicket.setDeployId(deployId);
        List<MyTicket> myTickets=myTicketService.findAll(myTicket);
        myTicket=myTickets.get(0);
        if(myTicket.getDeadlineTime()!=null){
            Calendar now = Calendar.getInstance();
            now.set(Calendar.DATE,now.get(Calendar.DATE)+myTicket.getDeadlineTime());
            map.put("deadlineDate",now.getTime());
        }
        map.put("caneditDeadline",myTicket.getCaneditDeadline());

        Result<String> result = ResultUtil.success(JSON.toJSONString(map));
        return result;
    }


    @PostMapping("createByName")
    @ApiOperation(value="处理工单的历史记录",notes="")
    @SysRequestLog(description="处理工单的历史记录", actionType = ActionType.ADD,manually=false)
    public Result<BusinessIntance> create(@RequestBody WorkDataVOByName datas) {
        return flowService.createTicket(datas);
    }

    /**
     * create创建流程后台
     * @param datas
     * @return
     */
    @PostMapping("createTicketByKey")
    @ApiOperation(value="create创建流程实例",notes="")
    @SysRequestLog(description="create创建流程实例", actionType = ActionType.ADD,manually=false)
    public Result<BusinessIntance> createTest(@RequestBody WorkDataVO datas) {
        // 根据流程定义的内容，和表单内容。
        MyTicket MyTicket = myTicketService.getOne(datas.getProcessdefGuid());
        String processKey = datas.getForms().get("processKey").toString();
        String userId = datas.getUserId();
        // 增加了告警事件id创建流程，事件id是否重复的判断 2022-06-30
        Result<BusinessIntance> result = businessIntanceService.createProcessInstance(userId, datas,MyTicket);
        BusinessIntance instance = result.getData();
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return  result;
        }
        ProcessInstance processInstance = flowService.startProcessByKey(processKey, userId, datas, instance);
        instance.setProcessInstanceId(processInstance.getId());
        businessIntanceService.save(instance);
        businessTaskLogService.saveStartLog(MyTicket.getGuid(),datas.getUserName(),userId, processInstance);
        return ResultUtil.success(instance);
    }

    @PostMapping("setVariables")
    @ApiOperation(value="流程设置流程变量",notes="")
    @SysRequestLog(description="流程设置流程变量", actionType = ActionType.ADD,manually=false)
    public Result<Boolean> setVariables(@RequestBody DealVO deals) {
        List<Map<String,Object>> params = deals.getParams();
        String processInstanceId = deals.getProcessInstanceId();
        String taskId = deals.getTaskId();
        BusinessTask task = businessTaskService.getOne(taskId);
        BusinessIntance instance = task.getInstance();
        String busiArgs = instance.getBusiArgs();
        Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
        if(params!=null){
            for (Map<String, Object> paramsMap : params) {
                if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null){
                    Map<String,Object> hashMap = new HashMap<>();
                    String key = paramsMap.get("key").toString();
                    hashMap.put(key, paramsMap.get("value"));
                    map.putAll(hashMap);
                }
            }
        }
        businessTaskService.setVariables(processInstanceId, map);
        businessTaskService.setVariables(processInstanceId, FlowConstant.PARAMS, params);
        return ResultUtil.success(true);
    }


    @PostMapping("completeTaskByThirdBusiness")
    @ApiOperation(value="第三方系统触发工单流程流转",notes="")
    @SysRequestLog(description="第三方系统触发工单流程流转", actionType = ActionType.UPDATE,manually=false)
    public Result<Boolean> completeTaskByThirdBusiness(@RequestBody BusinessVO businessVO) {
        try{
            boolean result = businessTaskService.completeTaskByThirdBusiness(businessVO);
            return ResultUtil.success(result);
        }catch(Exception e) {
            logger.error("流程审批异常",e);
            throw new RuntimeException("流程流转失败",e);
        }
    }



    @PostMapping("completeTask")
    @ApiOperation(value="流程审批处理",notes="")
    @SysRequestLog(description="流程审批处理", actionType = ActionType.UPDATE,manually=false)
    public Result<String> completeTask(@RequestBody DealVO deals) {
        try{
            String userId = deals.getUserId();  // 当前处理人id
            businessTaskService.completeTask(deals,userId);
            return ResultUtil.success("true");
        }catch(Exception e) {
            logger.error("流程审批处理",e);
            throw new RuntimeException("流程审批处理失败",e);
        }
    }

    /**
     * 批量审批： 2021-08-30
     * 1.异步处理
     * 2.审批方法加同步所
     * 3.审批之间间隔一秒钟
     * @param deals
     * @return
     * @throws InterruptedException
     */
    @PostMapping("batchCompleteTask")
    @ApiOperation(value = "批量流程审批处理", notes = "")
    @SysRequestLog(description="批量流程审批处理", actionType = ActionType.UPDATE,manually=false)
    public Result<Boolean> batchCompleteTask(@RequestBody List<DealVO> deals) {
        Result<Boolean> result = new Result<Boolean>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setData(true);
        if (null == deals || deals.size() == 0) {
            result.setMsg("没有审批数据！");
            return result;
        }
        // 异步线程池处理
        ExecutorServiceVrvUtil.getThreddPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (DealVO deal : deals) {
                        String userId = deal.getUserId();
                        businessTaskService.completeTask(deal, userId);
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    logger.error("批量流程审批处理异常", e);
                }
            }
        });
        result.setMsg("正在审批过程当中，请稍后查看！");
        return result;
    }
    @PostMapping("completeTaskByBusinessInstance")
    @ApiOperation(value="流程流转",notes="")
    @SysRequestLog(description="流程流转", actionType = ActionType.UPDATE,manually=false)
    public Result<BusinessIntance> completeTaskByBusinessInstance(@RequestBody DealVO deals) {
        String userId = deals.getUserId();
        BusinessIntance byBusinessInstance = businessTaskService.completeTaskByBusinessInstance(deals,userId);
        return ResultUtil.success(byBusinessInstance);
    }


    @PostMapping("/transferFlowByCallBack")
    @ApiOperation(value="外派工单调用接口",notes="")
    @SysRequestLog(description="外派工单调用接口", actionType = ActionType.UPDATE,manually=false)
    public  Boolean transferFlowByCallBack(@RequestBody TransferFlowByCallBackVo transferFlowByCallBackVo){
        Boolean bool=true;
        try {
            BusinessIntance instance=businessIntanceService.getOne(transferFlowByCallBackVo.getTicketId());
            BusinessTask businessTask=new BusinessTask();
            businessTask.setBusiId(instance.getProcessInstanceId());
            List<BusinessTask> businessTaskList=businessTaskService.findAll(businessTask);
            if(businessTaskList.size()>0){
                businessTask=businessTaskList.get(0);
            }
            if(businessTask.getActions().contains(transferFlowByCallBackVo.getAction())){
                DealVO dealVO=new DealVO();
                List<Map<String,Object>> params=new ArrayList<>();
                params.add(transferFlowByCallBackVo.getBusiarg());
                dealVO.setAction(transferFlowByCallBackVo.getAction());
                dealVO.setParams(params);
                dealVO.setUserId(transferFlowByCallBackVo.getUserId());
                dealVO.setTaskId(businessTask.getId());

                String busiArgs = instance.getBusiArgs();
                Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
                if(params!=null){
                    for (Map<String, Object> paramsMap : params) {
                        if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null){
                            Map<String,Object> hashMap = new HashMap<>();
                            String key = paramsMap.get("key").toString();
                            hashMap.put(key, paramsMap.get("value"));
                            map.putAll(hashMap);
                        }
                    }
                }
                businessTaskService.setVariables(businessTask.getBusiId(), map);
                businessTaskService.setVariables(businessTask.getBusiId(), FlowConstant.PARAMS, params);
                businessTaskService.completeTask(dealVO,transferFlowByCallBackVo.getUserId());
            }else{
                bool=false;
            }
        } catch (Exception e) {
            bool=false;
            logger.info(e.getMessage());
        }
        return  bool;
    }


    /**
     * 通过事件的id、用户id获取任务节点id  userId
     * 2021-08-30
     *
     * 改通过事件的id获取当前任务id，不需要用户id 2022-09-06
     */
    @PostMapping("/getTaskIdByEventId")
    @ApiOperation(value = "通过事件的id获取对应当前任务id", notes = "")
    @SysRequestLog(description="通过事件的id获取对应当前任务id", actionType = ActionType.SELECT,manually=false)
    public Result<String> getTaskIdByEventId(@RequestBody SearBusinessVO searBusinessVO) {
        Result<String> result = new Result<String>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        if (null == searBusinessVO) {
            result.setMsg("参数不为空！");
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
            return result;
        }
        if (StringUtils.isEmpty(searBusinessVO.getEventId())) {
            result.setMsg("事件的id不能为空！");
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
            return result;
        }
        try {
            logger.info("eventId:" + searBusinessVO.getEventId());
            return businessTaskService.getTaskIdByInstanceId(searBusinessVO.getEventId());
        } catch (Exception e) {
            logger.error("通过事件的id获取对应当前任务id异常", e);
            result.setMsg("获取任务id异常！");
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
            return result;
        }
    }
    /**
     * 获取自定义监听器
     *
     * 2022-11-7
     *
     * type: process：过程监听器
     *      task : 任务监听器
     *
     */
    @GetMapping("/getListenerConfigs/{type}")
    @ApiOperation(value ="获取自定义监听器", notes = "")
    @SysRequestLog(description="获取自定义监听器", actionType = ActionType.SELECT,manually=false)
    public Result<List<ListenerConfig>> getListenerConfigs(@PathVariable("type") String type) {
        Result<List<ListenerConfig>> result = new Result<List<ListenerConfig>>();
        try {
            if(StringUtils.isEmpty(type)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"类型type不能为空！");
            }
            List<QueryCondition> conditions=new ArrayList<>();
            conditions.add(QueryCondition.eq("status", "0"));
            conditions.add(QueryCondition.eq("type", type));
            List<ListenerConfig> datas = listenerConfigService.findAll(conditions);
            return ResultUtil.successList(datas);
        } catch (Exception e) {
            logger.error("获取自定义监听器异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取自定义监听器异常");
        }
    }
}
