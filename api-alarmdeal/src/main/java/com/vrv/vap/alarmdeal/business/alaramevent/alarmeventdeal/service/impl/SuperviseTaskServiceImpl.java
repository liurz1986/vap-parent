package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.NumericBooleanSerializer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.ExecutorConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.DisponseConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.IUpReportEventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.BinUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.SuperviseTaskService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository.SuperviseTaskRepository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.OperationLog;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskQueryVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskReceiveVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.DealVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.WorkDataVO;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * @author lps 2021/8/4
 */

@Service
public class SuperviseTaskServiceImpl extends BaseServiceImpl<SuperviseTask, String> implements SuperviseTaskService {

    private Logger log = LoggerFactory.getLogger(SuperviseTaskServiceImpl.class);

    private static String REPORT_TOPIC = "SuperviseDataSubmit";

    @Autowired
    private SuperviseTaskRepository superviseTaskRepository;

    @Autowired
    AdminFeign adminFeign;
    @Autowired
    private IUpReportCommonService upReportCommonService;
    @Autowired
    private FileConfiguration fileConfiguration;
    @Autowired
    AlarmEventManagementForESService alarmEventManagementForESService;
    @Autowired
    private BusinessTaskService businessTaskService;

    @Autowired
    private MyTicketService myTicketService;
    Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();

    @Autowired
    private MapperUtil mapper;
    private Executor executorService = ExecutorConfig.superviseExecutor();

    @Override
    public SuperviseTaskRepository getRepository() {
        return superviseTaskRepository;
    }

    @Override
    public PageRes<SuperviseTask> getSuperviseTaskPage(SuperviseTaskQueryVo superviseTaskQueryVo) {
        PageReq pager = mapper.map(superviseTaskQueryVo, PageReq.class);
        String noticeName = superviseTaskQueryVo.getNoticeName();
        String noticeType = superviseTaskQueryVo.getNoticeType();
        String timeField = SuperviseTask.ASSISTING_UP.equals(noticeType) ? "createTime" : "sendTime";
        pager.setBy("desc");
        pager.setOrder(timeField);
        String dealStatus = superviseTaskQueryVo.getDealStatus();
        String createStartTime = superviseTaskQueryVo.getCreateStartTime();
        String createEndTime = superviseTaskQueryVo.getCreateEndTime();
        String responseStartTime = superviseTaskQueryVo.getResponseStartTime();
        String responseEndTime = superviseTaskQueryVo.getResponseEndTime();
        String taskCreate = superviseTaskQueryVo.getTaskCreate();
        String noticeDesc = superviseTaskQueryVo.getNoticeDesc();
        List<QueryCondition> conditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(noticeType)) {
            conditions.add(QueryCondition.eq("noticeType", noticeType));
        }
        if (StringUtils.isNotBlank(noticeName)) {
            conditions.add(QueryCondition.like("noticeName", noticeName + "%"));
        }
        if (StringUtils.isNotBlank(dealStatus)) {
            conditions.add(QueryCondition.eq("dealStatus", dealStatus));
        }
        if (StringUtils.isNotBlank(taskCreate)) {
            conditions.add(QueryCondition.eq("taskCreate", taskCreate));
        }
        if (StringUtils.isNotBlank(noticeDesc)) {
            conditions.add(QueryCondition.like("noticeDesc", "%" + noticeDesc + "%"));
        }
        log.info("startTime: " + createStartTime);
        log.info("endTime: " + createEndTime);
        if (StringUtils.isNotBlank(createStartTime) && StringUtils.isNotBlank(createEndTime)) {
            if (createStartTime.length() < 20) {
                createStartTime = createStartTime + " 00:00:00";
                createEndTime = createEndTime + " 23:59:59";
            }
            log.info("startTime: " + createStartTime);
            log.info("endTime: " + createEndTime);
            conditions.add(QueryCondition.lt(timeField, createEndTime));
            conditions.add(QueryCondition.gt(timeField, createStartTime));
        }
        if (StringUtils.isNotBlank(responseStartTime) && StringUtils.isNotBlank(responseEndTime)) {
            if (responseStartTime.length() < 20) {
                responseStartTime = responseStartTime + " 00:00:00";
                responseEndTime = responseEndTime + " 23:59:59";
            }
            log.info("startTime: " + createStartTime);
            log.info("endTime: " + createEndTime);
            conditions.add(QueryCondition.lt("responseTime", responseEndTime));
            conditions.add(QueryCondition.gt("responseTime", responseStartTime));
        }
        Page<SuperviseTask> page = findAll(conditions, pager.getPageable());
        return PageRes.toRes(page);
    }

    @Override
    public Boolean dealSuperviseTask(String analysisId) {
        SuperviseTask superviseTask = getSuperviseTaskByAnalysisId(analysisId);
        superviseTask.setDealStatus(SuperviseTask.COMPLETE);
        save(superviseTask);
        return true;
    }

    private SuperviseTask getSuperviseTaskByAnalysisId(@PathVariable String analysisId) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("dealStatus", SuperviseTask.TODO));
        conditions.add(QueryCondition.eq("eventId", analysisId));
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        List<SuperviseTask> superviseTaskList = findAll(conditions, sort);
        if (superviseTaskList.size() > 0) {
            return superviseTaskList.get(0);
        } else {
            throw new RuntimeException("未查到相关督办");
        }

    }

    /**
     * 拉取下发任务
     * 1：下发协办申请
     * 2：下发协办反馈
     * 3：下发预警
     *
     * @param superviseTaskReceiveVo
     */
    @Override
    public void pullSuperviseTask(SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        //拉取的数据进行解析，这一步不可少
        SuperviseTask superviseTask = gson.fromJson(gson.toJson(superviseTaskReceiveVo), SuperviseTask.class);
        log.info("###############pullSuperviseTask noticeType={}", superviseTask.getNoticeType());
        switch (superviseTask.getNoticeType()) {
            //拉取协查反馈  20231020为了界面上展示协查id，流程id用notice_id换了。
            case SuperviseTask.ASSISTING_UP:
                //这个实际是我上报后协查申请后，上级监管平台给我的反馈
                 pullAssistUp(superviseTaskReceiveVo);
                //完成工单 协查  协查和协办是2个流程了，为了表单显示隐藏，协查流程id用的字段是Notice_id，协办流程用的流程id依然是guid，特别注意！！！，容易混淆
                completeSuperviseTaskTicket(superviseTaskReceiveVo);
                break;
            case SuperviseTask.WARNING:
                //拉取预警任务
                pullWarning(superviseTask, superviseTaskReceiveVo);
                //创建工单
                createSuperviseTaskTicket("eventWarn", superviseTask, "事件预警工单-" + DateUtil.format(new Date(), "yyyyMMddHHss"));
                break;
            case SuperviseTask.SUPERVISE:
                //拉取督办任务
                pullSupervise(superviseTask, superviseTaskReceiveVo);
                //处理督办
                dealSuperviseTask(superviseTaskReceiveVo);
                break;
            //拉取协办任务
            case SuperviseTask.ASSISTING_DOWN:
                //拉取协办任务
                pullAssistDown(superviseTask, superviseTaskReceiveVo);
                //创建工单
                createSuperviseTaskTicket("eventCo", superviseTask, "事件协办工单-" + DateUtil.format(new Date(), "yyyyMMddHHss"));
                break;
            default:
                break;
        }
    }

    /**
     * 完成工单
     */
    private void completeSuperviseTaskTicket(SuperviseTaskReceiveVo superviseTask) {
        try {
            if (null == superviseTask) {
                return;
            }
            Result<String> instanceIdResult = businessTaskService.getTaskIdByInstanceId(superviseTask.getNoticeId());
            if (instanceIdResult == null || StringUtils.isEmpty(instanceIdResult.getData())) {
                log.error("获取的流程结果为空，根据协办id找不到");
                return;
            }
            DealVO dealVO = new DealVO();
            dealVO.setTaskId(instanceIdResult.getData());
            dealVO.setAction("反馈");
            dealVO.setUserId("1");
            List<Map<String, Object>> params = new ArrayList<>();
            Map<String, Object> param = new HashMap<>();
            param.put("label", "是否发送消息");
            param.put("key", "sendMsg");
            param.put("value", "false");
            params.add(param);
            //20231020调整：协查结果新标准没有附件了
            addBusinessArgsParams(superviseTask.getBusiArgs(),params);
            log.info("##############params={}",new Gson().toJson(params));
            dealVO.setParams(params);
            businessTaskService.completeTask(dealVO, "1");
        } catch (Exception e) {
            log.error("完成工单异常", e);
        }
    }
    /**
     * 添加业务完成参数
     * @param businessArgs 业务参数
     * @param params 参数集合
     */
    private void addBusinessArgsParams(String businessArgs,List<Map<String,Object>> params){
        Map<String,Object> map=gson.fromJson(businessArgs,Map.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Map<String,Object> param=new HashMap<>();
            param.put("key",entry.getKey());
            param.put("label","反馈_"+entry.getKey());
            param.put("value",entry.getValue());
            params.add(param);
        }
    }

    // 增加附件反馈 2022-10-22
    private void addResponseAttachment(SuperviseTask superviseTask, List<Map<String, Object>> params) {
        List<Map<String, Object>> data = getAttachment(superviseTask.getResponseAttachment());
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("label", "协办反馈附件");
        param.put("key", "responseAttachment");
        param.put("value", data);
        params.add(param);
    }

    // 构建附件 2022-10-20
    private List<Map<String, Object>> getAttachment(String responseAttachment) {
        if (StringUtils.isEmpty(responseAttachment)) {
            return null;
        }
        List<Map<String, Object>> data = gson.fromJson(responseAttachment, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        return data;
    }

    /**
     * 获取流程id通过配置code
     */
    private String getProcessGuidByConfigCode(String configCode) {
        Result<String> ticketNameResult = myTicketService.getTicketName(configCode);
        Result<MyTicket> ticketByByName = myTicketService.getTicketByByName(ticketNameResult.getData());
        MyTicket myTicketResult = ticketByByName.getData();
        return myTicketResult.getGuid();
    }

    /**
     * 处理督办数据  处理督办数据需要上报吗？原来的上报是怎样的？todo 20230717
     * 处置督办这里
     */
    private void dealSuperviseTask(SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        if (StringUtils.isNotBlank(superviseTaskReceiveVo.getEventId())) {
            //更新es数据为已经督办了
            AlarmEventAttribute alarmEventAttribute = updateWarnSuperviseStatus(superviseTaskReceiveVo);
            //上报事件处置督办直接传,避免查询第二次,提高效率  这里是属于上报事件处置
            UpEventDTO eventDTO = new UpEventDTO();
            eventDTO.setDoc(alarmEventAttribute);
            eventDTO.setDisposeStatus(DisponseConstant.SUPER_SUPERVISE);
            //事件处置
            eventDTO.setUpReportBeanName(IUpReportEventService.UpReportDispose_BEAN_NAME);
            upReportCommonService.upReportEvent(eventDTO);
        }
    }


    /**
     * 拉取协办任务
     */
    private void pullAssistDown(SuperviseTask superviseTask, SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        binStrTransformFile(superviseTaskReceiveVo, superviseTask, 1);
        superviseTask.setDealStatus(SuperviseTask.TODO);
        superviseTask.setTaskCreate(SuperviseTask.TASK_DOWN);
        superviseTask.setNoticeType(SuperviseTask.ASSISTING_DOWN);
        if (StringUtils.isNotEmpty(superviseTaskReceiveVo.getId())) {
            superviseTask.setGuid(superviseTaskReceiveVo.getId());
        } else {
            superviseTask.setGuid(UUIDUtils.get32UUID());
        }
        superviseTask.setAssistId(superviseTask.getGuid());
        save(superviseTask);
    }

    /**
     * 拉取督办
     */
    private void pullSupervise(SuperviseTask superviseTask, SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        superviseTask.setDealStatus(SuperviseTask.TODO);
        superviseTask.setTaskCreate(SuperviseTask.TASK_DOWN);
        superviseTask.setNoticeType(SuperviseTask.SUPERVISE);
        binStrTransformFile(superviseTaskReceiveVo, superviseTask, 1);
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("eventId", superviseTask.getEventId()));
        queryConditions.add(QueryCondition.eq("noticeType", SuperviseTask.SUPERVISE));
        List<SuperviseTask> superviseTaskList = findAll(queryConditions);
        if (superviseTaskList.size() > 0) {
            //已经存在，只是更新，不会插入
            superviseTask.setGuid(superviseTaskList.get(0).getGuid());
        } else {
            superviseTask.setGuid(UUIDUtils.get32UUID());
        }
        save(superviseTask);
    }

    /**
     * 拉取预警
     */
    private void pullWarning(SuperviseTask superviseTask, SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        //1，入库
        superviseTask.setDealStatus(SuperviseTask.TODO);
        superviseTask.setTaskCreate(SuperviseTask.TASK_DOWN);
        superviseTask.setNoticeType(SuperviseTask.WARNING);
        superviseTask.setGuid(UUIDUtils.get32UUID());
        //notice_id就是下发数据中的warnning_id
        superviseTask.setWarnningId(superviseTask.getNoticeId());
        binStrTransformFile(superviseTaskReceiveVo, superviseTask, 1);
        String busiArgs = superviseTask.getBusiArgs();
        //附件转一下
        Map<String,Object> map = gson.fromJson(busiArgs, Map.class);
        map.put("warn_file",superviseTask.getApplyAttachment());
        superviseTask.setBusiArgs(gson.toJson(map));
        save(superviseTask);
    }


    /**
     * 创建工单：事件协办、事件预警
     *
     * @param configCode
     * @param superviseTask
     */
    private void createSuperviseTaskTicket(String configCode, SuperviseTask superviseTask, String name) {
        try {
            String processGuid = getProcessGuidByConfigCode(configCode);
            WorkDataVO workDataVO = new WorkDataVO();
            workDataVO.setProcessdefGuid(processGuid);
            workDataVO.setName(name);
            workDataVO.setCode(DateUtil.format(new Date(), "yyyyMMddHHss"));
            //用户人名称，不太确定，不为空即可
            workDataVO.setUserName("superviseTask");
            workDataVO.setUserId("2");
            Map<String, Object> forms = gson.fromJson(superviseTask.getBusiArgs(), Map.class);
            //这个字段的true或者false是控制的是否发送到队列里面。
            forms.put("sendMsg", "false");
            forms.put("businessId", superviseTask.getGuid());
            // 协办重新构建附件 2022-10-20，增加预警也构造附件
            addApplyAttachment(forms, superviseTask);
            workDataVO.setForms(forms);
            //创建工单
            businessTaskService.createTicket(workDataVO);
        } catch (Exception e) {
            log.error("创建工单：事件协办、事件预警异常", e);
        }
    }

    /**
     * 协办重新构建附件  2022-10-20
     *
     * @param forms
     * @param superviseTask
     */
    private void addApplyAttachment(Map<String, Object> forms, SuperviseTask superviseTask) {
        String applyAttachment = superviseTask.getApplyAttachment();
        if (StringUtils.isEmpty(applyAttachment)) {
            return;
        }
        List<Map<String, Object>> data = getAttachment(applyAttachment);
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        forms.put("applyAttachment", data);
    }

    /**
     * 拉取协办反馈
     */
    private SuperviseTask pullAssistUp(SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        SuperviseTask superviseTask = getOne(superviseTaskReceiveVo.getNoticeId());
        if (superviseTask == null) {
            log.error("########所传的协查id={}不正确，无法找到协查记录，从而不能够完成反馈操作############",superviseTaskReceiveVo.getNoticeId());
            return null;
        }
        superviseTask.setDealStatus(SuperviseTask.COMPLETE);
        superviseTask.setResponseNote(superviseTaskReceiveVo.getResponseNote());
        superviseTask.setSendTime(superviseTaskReceiveVo.getSendTime());
        superviseTask.setResponseTime(superviseTaskReceiveVo.getSendTime());
        //事件简要描述
        if (StringUtils.isNotEmpty(superviseTaskReceiveVo.getEventDescription())) {
            superviseTask.setTaskDesc(superviseTaskReceiveVo.getEventDescription());
        }
        //更新结果
        save(superviseTask);
        return superviseTask;
    }

    /**
     * 更新告警督办状态
     *
     * @param superviseTaskReceiveVo
     */
    private AlarmEventAttribute updateWarnSuperviseStatus(SuperviseTaskReceiveVo superviseTaskReceiveVo) {
        log.info("event id ={} is not null", superviseTaskReceiveVo.getEventId());
        // 修改告警状态
        log.info("change warn status!");
        AlarmEventAttribute event = alarmEventManagementForESService.getDocByEventId(superviseTaskReceiveVo.getEventId());
        // 补全   拉取事件督办任务  中notice_desc信息
        event.setNoticeDesc(superviseTaskReceiveVo.getNoticeDesc());
        try {
            event.setSuperviseTime(DateUtil.parseDate(superviseTaskReceiveVo.getSendTime(), DateUtil.DEFAULT_DATE_PATTERN));
        } catch (ParseException e) {
            log.error("时间解析错误{}", e);
        }
        if (AlarmDealStateEnum.PROCESSED.getCode().intValue() == event.getAlarmDealState().intValue()) {
            AuthorizationControl authorization = event.getAuthorization();
            List<OperationLog> operatorRecord = authorization.getOperatorRecord();
            OperationLog opLog = null;
            for (OperationLog log : operatorRecord) {
                if (opLog == null || opLog.getTime().after(log.getTime())) {
                    opLog = log;
                }
            }
            authorization.setCanOperateRole(new ArrayList<>());
            List<GuidNameVO> canOperateUser = new ArrayList<>();
            if (opLog != null) {
                canOperateUser.add(new GuidNameVO(opLog.getUserId().toString(), opLog.getUserName()));
            }
            authorization.setCanOperateUser(canOperateUser);
            event.setAlarmDealState(AlarmDealStateEnum.UNTREATED.getCode());
            //增加工单拼接时间戳字段，工单创建时候通过eventid+timestamp主键不重复
            event.setTicketJoinStamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        }
        //更新状态为已经督办
        event.setIsSupervise(true);
        alarmEventManagementForESService.saveAlarmEventData(event);
        return event;
    }


    /**
     * 二进制字符串转文件
     * todo 20231018 需要重点进行测试一下
     *
     * @param attachmentType 附件类型 1申请附件 2反馈附件
     */
    public void binStrTransformFile(SuperviseTaskReceiveVo superviseTaskReceiveVo, SuperviseTask superviseTask, Integer attachmentType) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<CoFile> coFileList = superviseTaskReceiveVo.getAttachment();
        String fileDir = fileConfiguration.getFilePath();
        for (CoFile coFile : coFileList) {
            Map<String, Object> map = new HashMap<>();
            //二进制字节数组转为文件 开始者里面的file_bin是文件路径
            String filePath = fileDir + "/" + coFile.getFile_name();
            File dir = new File(fileDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (StringUtils.isNotEmpty(coFile.getFile_bin())) {
                File file = BinUtil.binStrToFile(coFile.getFile_bin(), filePath);
                try {
                    uploadFileInfo(file, map);
                    list.add(map);
                } catch (Exception e) {
                    log.error("feign客户端调用api-common上传上级下发的文件失败了，失败的原因为{}", e);
                }
            }
        }
        if (attachmentType == 1) {
            superviseTask.setApplyAttachment(gson.toJson(list));
        } else {
            superviseTask.setResponseAttachment(gson.toJson(list));
        }
    }


    /**
     * 上传文件
     */
    private void uploadFileInfo(File file, Map<String, Object> map) {
        MultipartFile multipartFile = new CommonsMultipartFile(AlarmDealUtil.createFileItem(file, "file"));
        Map<String, Object> response = adminFeign.uploadFile(multipartFile, "superviseTask", "");
        if ("0".equals(response.get("code"))) {
            Object data = response.get("data");
            if (null == data) {
                return;
            }
            Map<String, Object> mapData = (Map<String, Object>) data;
            String fileName = mapData.get("fileName") != null ? mapData.get("fileName").toString() : "";
            map.put("name", fileName);
            map.put("status", "done");
            map.put("response", response);
        }
    }

    /**
     * 新增协办任务
     */
    @Override
    public SuperviseTask addAssistingTask(SuperviseTaskVo superviseTaskVo) {
        SuperviseTask superviseTask = mapper.map(superviseTaskVo, SuperviseTask.class);
        superviseTask.setGuid(UUIDUtils.get32UUID());
        superviseTask.setDealStatus(SuperviseTask.TODO);
        superviseTask.setTaskCreate(SuperviseTask.TASK_UP);
        superviseTask.setNoticeType(SuperviseTask.ASSISTING_UP);
        superviseTask.setSendTime(DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN));
        superviseTask.setCreateTime(superviseTask.getSendTime());
        save(superviseTask);
        User currentUser = SessionUtil.getCurrentUser();
        //更新协办
        updateEsAssist(superviseTaskVo.getEventId());
        return superviseTask;
    }


    /**
     * 更新状态为已经协办
     */
    @Override
    public void updateEsAssist(String eventId) {
        AlarmEventAttribute doc = alarmEventManagementForESService.getDocByEventId(eventId);
        if (doc != null) {
            doc.setIsAssist(true);
            alarmEventManagementForESService.saveAlarmEventData(doc);
        }
    }


    /**
     * 响应督办
     *
     * @param superviseTaskVo
     * @return
     */
    @Override
    public SuperviseTask responseSuperviseTask(@RequestBody SuperviseTaskVo superviseTaskVo) {
        SuperviseTask superviseTask = getOne(superviseTaskVo.getGuid());
        if (SuperviseTask.SUPERVISE.equals(superviseTask.getNoticeType())) {
            AlarmEventAttribute doc = alarmEventManagementForESService.getDocByEventId(superviseTask.getEventId());
            if (doc == null) {
                return null;
            }
            if (AlarmDealStateEnum.PROCESSED.getCode().intValue() != doc.getAlarmDealState().intValue()) {
                return null;
            }
        }
        String responseNote = superviseTaskVo.getResponseNote();
        superviseTask.setResponseNote(responseNote);
        if (StringUtils.isNotEmpty(superviseTaskVo.getTaskDesc())) {
            superviseTask.setTaskDesc(superviseTaskVo.getTaskDesc());
        }
        if (StringUtils.isNotEmpty(superviseTaskVo.getNoticeDesc())) {
            superviseTask.setNoticeDesc(superviseTaskVo.getNoticeDesc());
        }
        if (StringUtils.isNotEmpty(superviseTaskVo.getNoticeName())) {
            superviseTask.setNoticeName(superviseTaskVo.getNoticeName());
        }
        superviseTask.setResponseAttachment(superviseTaskVo.getResponseAttachment());
        String now = DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN);
        superviseTask.setResponseTime(now);
        //1,协办反馈   预警反馈
        superviseTask.setTaskCreate(SuperviseTask.TASK_DOWN);
        superviseTask.setDealStatus(SuperviseTask.COMPLETE);
        save(superviseTask);
        //预警处理  协办处理都用这个 预警和协办处理反馈 非督办,协办,走工单以后，该方法已经被舍弃了。现在使用在superviseTaskjob里面。
        if (!superviseTask.getNoticeType().equals(SuperviseTask.SUPERVISE)) {
            executorService.execute(() -> {
                UpEventDTO eventDTO = new UpEventDTO();
                //上报服务bean名称
                eventDTO.setUpReportBeanName(IUpReportEventService.UpReportWarn_BEAN_NAME);
                eventDTO.setSuperviseTask(superviseTask);
                upReportCommonService.upReportEvent(eventDTO);
            });
        }
        return superviseTask;
    }


    /**
     * 督办任务查询top
     */
    @Override
    public List<SuperviseTask> findSuperviseTaskTop(Integer count) {
        return getTop(count, SuperviseTask.SUPERVISE);
    }


    /**
     * 督办任务查询top
     */
    @Override
    public List<SuperviseTask> findWarningTop(Integer count) {
        return getTop(count, SuperviseTask.WARNING);
    }

    private List<SuperviseTask> getTop(Integer count, String s) {
        PageReq pageReq = new PageReq();
        // 查询数量与排序字段规则
        pageReq.setOrder("createTime");
        pageReq.setBy("desc");
        pageReq.setCount_(count);
        pageReq.setStart_(0);
        // 设置查询条件
        List<QueryCondition> conditionList = new ArrayList<>();
        conditionList.add(QueryCondition.eq("noticeType", s));
        Page<SuperviseTask> page = findAll(conditionList, pageReq.getPageable());
        return page.getContent();
    }


    /**
     * 统计督办数
     */
    @Override
    public Map<String, Object> countSuperviseTask() {
        Map<String, Object> result = new HashMap<>();
        result.put("superTaskCount", alarmEventManagementForESService.count(getQueryCondition(false)));
        result.put("warningCount", getTaskCount(SuperviseTask.WARNING, SuperviseTask.TASK_DOWN, null));
        result.put("todaySuperTaskCount", alarmEventManagementForESService.count(getQueryCondition(true)));
        result.put("todayWarningCount", getTodayTaskCount(SuperviseTask.WARNING));
        return result;

    }

    /**
     * @param isToday 是否是今日的
     */
    private List<QueryCondition_ES> getQueryCondition(boolean isToday) {
        User currentUser = SessionUtil.getCurrentUser();
        List<QueryCondition_ES> queryConditionEsList = new ArrayList<>();
        if (currentUser == null) {
            queryConditionEsList.add(QueryCondition_ES.eq("eventId", "@#$%&^*(%"));
            return queryConditionEsList;
        }
        //业务主管看用户  其他人看全部
        if (currentUser.getRoleCode().size() == 1 && currentUser.getRoleCode().contains("businessMgr")) {
            queryConditionEsList.add(QueryCondition_ES.eq(alarmEventManagementForESService.getBaseField() + "eventType", 3));
        }
        if (isToday) {
            String nowStr = DateUtil.format(new Date(), DateUtil.Year_Mouth_Day);
            queryConditionEsList.add(QueryCondition_ES.between("superviseTime", nowStr + " 00:00:00", nowStr + " 23:59:59"));
        }
        queryConditionEsList.addAll(alarmEventManagementForESService.getDataPermissions());
        queryConditionEsList.add(QueryCondition_ES.eq(alarmEventManagementForESService.getBaseField() + "isSupervise", true));
        return queryConditionEsList;
    }


    /**
     * 任务统计
     *
     * @param type   任务类型，
     * @param source 上报或下发，
     * @return
     */
    @Override
    public Integer getTaskCount(String type, String source, String dealStatus) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("noticeType", type));
        queryConditions.add(QueryCondition.eq("taskCreate", source));
        if (StringUtils.isNotBlank(dealStatus)) {
            queryConditions.add(QueryCondition.eq("dealStatus", dealStatus));
        }
        Long count = count(queryConditions);
        return count.intValue();
    }

    /**
     * 通过下发时间查询统计督办格式
     *
     * @param param 入参  beginTime开始时间-endTime 结束时间
     * @return
     */
    @Override
    public Integer getSuperviseCountForSendTime(EventDetailQueryVO param) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.ge("sendTime", param.getBeginTime()));
        queryConditions.add(QueryCondition.le("sendTime", param.getEndTime()));
        Long count = count(queryConditions);
        return count.intValue();
    }


    /**
     * 今日任务统计
     *
     * @param s
     * @return
     */
    private Integer getTodayTaskCount(String s) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("noticeType", s));
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = simpleDateFormat.format(now);
        queryConditions.add(QueryCondition.ge("sendTime", nowStr + " 00:00:00"));
        queryConditions.add(QueryCondition.le("sendTime", nowStr + " 23:59:59"));
        Long count = count(queryConditions);
        return count.intValue();
    }

    /**
     * 今日下发任务统计
     *
     * @param s
     * @return
     */
    @Override
    public Integer getTodayDownTaskCount(String s) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        if (StringUtils.isNotBlank(s)) {
            queryConditions.add(QueryCondition.eq("noticeType", s));
        }

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowStr = simpleDateFormat.format(now);
        queryConditions.add(QueryCondition.ge("sendTime", nowStr + " 00:00:00"));
        queryConditions.add(QueryCondition.le("sendTime", nowStr + " 23:59:59"));
        Long count = count(queryConditions);
        return count.intValue();
    }


}
