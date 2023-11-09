package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.job;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.DisponseConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.IUpReportEventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.SuperviseTaskService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.FlowMessageVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.util.FlowQueUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class SuperviseTaskJob implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(SuperviseTaskJob.class);
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();
    @Autowired
    private SuperviseTaskService superviseTaskService;

    @Autowired
    private IUpReportCommonService upReportCommonService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("----FlowQueUtil队列消费任务开始------");
        new Thread(this::dealTask).start();
    }

    /**
     * 处置任务
     */
    private void dealTask() {
        while (true) {
            try {
                FlowMessageVO flowMessageVO = FlowQueUtil.flowMessageTake();
                if (flowMessageVO != null) {
                    switch (flowMessageVO.getTicketName()) {
                        case SuperviseTask.TICKET_NAME_ASSIST:
                        case SuperviseTask.TICKET_NAME_CO:
                            dealAssistTask(flowMessageVO);
                            break;
                        case SuperviseTask.TICKET_NAME_WARING:
                            dealWarnTask(flowMessageVO);
                            break;
                        default:
                            logger.error("工单名称有误，非事件协办或者事件预警工单");
                            break;
                    }
                }
            } catch (Exception e) {
                logger.error("FlowQueUtil队列消费失败了，失败的原因为", e);
            }
        }
    }

    /**
     * 处置预警任务
     *
     * @param flowMessageVO
     */
    private void dealWarnTask(FlowMessageVO flowMessageVO) {
        if ("end".equals(flowMessageVO.getStatus())) {
            SuperviseTask superviseTask = constractSuperviseTask(flowMessageVO);
            //1,预警反馈
            SuperviseTask dbSuperviseTask = superviseTaskService.getOne(flowMessageVO.getInstanceId());
            if (dbSuperviseTask == null) {
                logger.error("流程id={}", flowMessageVO.getInstanceId() + "不是预警id，导致查询不出来数据");
                return;
            }
            if (SuperviseTask.COMPLETE.equals(dbSuperviseTask.getDealStatus())) {
                logger.error("预警id={}", flowMessageVO.getInstanceId() + "已经处理过了");
                return;
            }
            //1.1更新
            updateFeedbackData(dbSuperviseTask, superviseTask);
        }
    }

    /**
     * 处置协办任务
     * 协办申请
     * 协办反馈
     *
     * @param flowMessageVO
     */
    private void dealAssistTask(FlowMessageVO flowMessageVO) {
        boolean isCreate = "create".equals(flowMessageVO.getStatus());
        SuperviseTask superviseTask = constractSuperviseTask(flowMessageVO);
        //1，协查申请逻辑
        if (isCreate) {
            //1.1入库
            superviseTask.setNoticeType(SuperviseTask.ASSISTING_UP);
            superviseTask.setTaskCreate(SuperviseTask.TASK_UP);
            superviseTask.setDealStatus(SuperviseTask.TODO);
            superviseTask.setCreateTime(DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN));
            //事件id作为主键id，一个告警事件只可能协查一次。协查过后会隐藏处理
            superviseTask.setGuid(flowMessageVO.getInstanceId());
            superviseTask.setAssistId(superviseTask.getGuid());
            superviseTask.setNoticeId(superviseTask.getGuid());
            superviseTaskService.save(superviseTask);
            superviseTaskService.updateEsAssist(superviseTask.getEventId());


            //1 协查申请上报
            UpEventDTO eventDTO = new UpEventDTO();
            User user = flowMessageVO.getUser();
            if (user != null) {
                eventDTO.setName(user.getName());
                eventDTO.setRoleId(user.getRoleId());
            }
            eventDTO.setEventId(superviseTask.getEventId());
            eventDTO.setSuperviseTask(superviseTask);
            //赋值处置状态disposeStatus值
            eventDTO.setDisposeStatus(DisponseConstant.COO_DISPONSE);
            //协查申请
            eventDTO.setUpReportBeanName(IUpReportEventService.UpReportUpAssist_BEAN_NAME);
            logger.info("----协办申请的事件id={}", eventDTO.getEventId());
            upReportCommonService.upReportEvent(eventDTO);


            //2 事件处置上报（协查会有上报过程）
            eventDTO.setUpReportBeanName(IUpReportEventService.UpReportDispose_BEAN_NAME);
            upReportCommonService.upReportEvent(eventDTO);
            return;
        }
        //2，协办反馈（对拉取的协办任务进行反馈处置）
        //2.1更新
        SuperviseTask dbSuperviseTask = superviseTaskService.getOne(flowMessageVO.getInstanceId());
        if (dbSuperviseTask == null) {
            logger.error("流程id=" + flowMessageVO.getInstanceId() + "不是协办id，导致查询不出来数据");
            return;
        }
        if (SuperviseTask.COMPLETE.equals(dbSuperviseTask.getDealStatus())) {
            logger.error("协办id=" + flowMessageVO.getInstanceId() + "已经处理过了");
            return;
        }
        updateFeedbackData(dbSuperviseTask, superviseTask);
    }

    /**
     * @param flowMessageVO 工单消息
     *                      todo 这里需要关注一下协办单位名称
     */
    private SuperviseTask constractSuperviseTask(FlowMessageVO flowMessageVO) {
        Map map = gson.fromJson(flowMessageVO.getBusiArgs(), Map.class);
        logger.info("############ constractSuperviseTask busiArgs=={}", flowMessageVO.getBusiArgs());
        String applyAttachment = map.get("applyAttachment") != null ? gson.toJson(map.get("applyAttachment")) : "";
        map.remove("applyAttachment");
        String responseAttachment = map.get("responseAttachment") != null ? gson.toJson(map.get("responseAttachment")) : "";
        map.remove("responseAttachment");
        SuperviseTask superviseTask = gson.fromJson(gson.toJson(map), SuperviseTask.class);
        superviseTask.setApplyAttachment(applyAttachment);
        //表单中的业务数据存入
        superviseTask.setBusiArgs(flowMessageVO.getBusiArgs());
        superviseTask.setResponseAttachment(responseAttachment);
        return superviseTask;
    }

    /**
     * 更新反馈数据（对拉取的协办任务进行反馈处理）等于
     * @param dbSuperviseTask 数据库中的拉取的协办任务数据
     * @param feedbackData 反馈协办任务数据
     */
    private void updateFeedbackData(SuperviseTask dbSuperviseTask, SuperviseTask feedbackData) {
        dbSuperviseTask.setDealStatus(SuperviseTask.COMPLETE);
        //1，更新反馈时间
        dbSuperviseTask.setResponseTime(DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN));
        //2，更新反馈结果
        dbSuperviseTask.setResponseNote(feedbackData.getResponseNote());
        //3，更新反馈附件
        dbSuperviseTask.setResponseAttachment(feedbackData.getResponseAttachment());
        //4 表单业务数据
        dbSuperviseTask.setBusiArgs(feedbackData.getBusiArgs());
        //5 协办单位数据补充
        dbSuperviseTask.setAssistUnit(feedbackData.getAssistUnit());
        superviseTaskService.save(dbSuperviseTask);

        //4，协办结果反馈
        UpEventDTO eventDTO = new UpEventDTO();
        eventDTO.setSuperviseTask(dbSuperviseTask);
        String noticeType=dbSuperviseTask.getNoticeType();
        //notice_type 3是协办反馈  2是预警反馈，这里不是预警反馈就是协办反馈，用3元表达式进行分支逻辑处理。
        eventDTO.setUpReportBeanName(SuperviseTask.ASSISTING_DOWN.equals(noticeType)?IUpReportEventService.UPReportDownAssist_BEAN_NAME:IUpReportEventService.UpReportWarn_BEAN_NAME);
        upReportCommonService.upReportEvent(eventDTO);
    }
}
/**
 * 协办预警工单流程 2022/10/17日梳理
 * <p>
 * （一）需要放入队列
 * 1，协办、预警反馈需要上报  1.1工单审批流程，放入队列；1.2，监控队列，更新库中记录 1.3上报 uptokafak
 * 2，协办申请  2.1 创建工单，数据放入队列；2.2消费对列数据，申请入库；2.3，上报协办，发送kafka  uptokafak   2.4上报事件处置kafka  upToDisposeEventData type=4
 * <p>
 * <p>
 * （二）不需要放入队列
 * 3，拉取协办 pullSupervisetask  3.1 入库；3.2 创建工单
 * 4，拉取预警任务  4.1入库 4.2创建工单
 */