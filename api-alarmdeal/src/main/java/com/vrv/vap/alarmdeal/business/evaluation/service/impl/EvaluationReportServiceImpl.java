package com.vrv.vap.alarmdeal.business.evaluation.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.evaluation.dao.EvluationDao;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.alarmdeal.business.evaluation.service.EvaluationReportService;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EvaluationUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自查自评报告
 *
 * @author liurz
 * @Date 202309
 */
@Service
public class EvaluationReportServiceImpl implements EvaluationReportService {

    private Logger logger = LoggerFactory.getLogger(EvaluationReportServiceImpl.class);

    @Autowired
    private EvluationDao evluationDao;
    @Autowired
    private SelfInspectionEvaluationService selfInspectionEvaluationService;
    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;
    @Autowired
    private BusinessIntanceService businessIntanceService;
    @Autowired
    private EventCategoryService eventCategoryService;
    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private MapperUtil mapperUtil;
    /**
     * 报告：自查自评结果
     *   统计周期内，总共产生推荐自查自评项XX项，已自查自评XX项，推荐的自查自评项中涉及问题部门 XX个，涉及XX个检查大类，XX个成因类型；涉及监管事件合计XXX条
     * @param evaluationReportSearchVO
     * @return
     */
    @Override
    public Result<SummaryVO> queryEvaluationResult(EvaluationReportSearchVO evaluationReportSearchVO) {
        SummaryVO result = new SummaryVO();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.between("createTime",evaluationReportSearchVO.getStartTime(),evaluationReportSearchVO.getEndTime()));
        List<SelfInspectionEvaluation> evaluations = selfInspectionEvaluationService.findAll(conditions);
        if(CollectionUtils.isEmpty(evaluations)){
            return ResultUtil.success(result);
        }
        // 总共产生推荐自查自评项
        int totalSize = evaluations.size();
        result.setTotalCount(totalSize);
        // 已自查自评项
        List<SelfInspectionEvaluation> filterStatus = evaluations.stream().filter(item -> item.getStatus() == 1).collect(Collectors.toList());
        int finishStatus = filterStatus.size();
        result.setFinshCount(finishStatus);
        // 推荐的自查自评项中涉及问题部门 XX个，涉及XX个检查大类，XX个成因类型；涉及监管事件合计XXX条
        Set<String> depNames = new HashSet<>();
        Set<String> checkTypes = new HashSet<>();
        Set<String> geneticTypes = new HashSet<>();
        Set<String> eventIds = new HashSet<>();
        for(SelfInspectionEvaluation data : evaluations){
            depNames.add(data.getOrgName());
            checkTypes.add(data.getCheckType());
            geneticTypes.add(data.getGeneticType());
            String eventIdStr = data.getEventIds();
            if(StringUtils.isEmpty(eventIdStr)){
                continue;
            }
           String[]  eid= eventIdStr.split(",");
            for(String eventId : eid){
                eventIds.add(eventId);
            }
        }
        result.setDepCount(depNames.size());
        result.setCheckTypeCount(checkTypes.size());
        result.setGeneticTypeCount(geneticTypes.size());
        result.setEventCount(eventIds.size());
        result.setStartTime(evaluationReportSearchVO.getStartTime());
        result.setEndTime(evaluationReportSearchVO.getEndTime());
        return ResultUtil.success(result);
    }

    @Override
    public Result<Map<String, Object>> queryStatusStatistics(EvaluationReportSearchVO evaluationReportSearchVO) {
        Map<String, Object> result = new HashMap<>();
        List<KeyValueVO> list =  evluationDao.queryStatusStatistics(evaluationReportSearchVO);
        for(KeyValueVO vo : list){
           String name=  vo.getName();
           if("未完成".equals(name)){
               result.put("noFinish",vo.getNum());
           }else{ // 已完成
               result.put("finish",vo.getNum());
           }
        }
        return ResultUtil.success(result);
    }


    /**
     * 报告： 自查自评状态统计
     * @param evaluationReportSearchVO
     * @return
     */
    @Override
    public Result<List<KeyValueVO>> queryStatusList(EvaluationReportSearchVO evaluationReportSearchVO) {
        return ResultUtil.successList(evluationDao.queryStatusStatistics(evaluationReportSearchVO));
    }

    /**
     * 报告：成因类型分类统计
     * @param evaluationReportSearchVO
     * @return
     */
    @Override
    public Result<List<KeyValueVO>> queryGeneticTypeStatistics(EvaluationReportSearchVO evaluationReportSearchVO) {
        return ResultUtil.successList(evluationDao.queryGeneticTypeStatistics(evaluationReportSearchVO));
    }

    /**
     * 处置问题分类统计(代码中写死数据，目前没有取值的地方)
     *
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @Override
    public Result<List<KeyValueVO>> queryHandlIssuesStatistics(EvaluationReportSearchVO evaluationReportSearchVO) {
        List<KeyValueVO> result = new ArrayList<>();
        KeyValueVO vo = new KeyValueVO();
        vo.setName("处理不及时");
        vo.setNum(10);
        result.add(vo);
        vo = new KeyValueVO();
        vo.setName("填写不规范");
        vo.setNum(14);
        result.add(vo);
        return ResultUtil.successList(result);
    }
    /**
     * 自查自评分类统计
     *  检查大类监管事件统计:checktype
     *  成因类型监管事件统计:geneticType
     *  信息化工作机构成因类型事件统计: inforOrg
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @Override
    public Result<List<KeyValueVO>> queryStatisticsByType(EvaluationReportSearchVO evaluationReportSearchVO, String type) {
        List<KeyValueVO> result = null;
        switch (type){
            // 检查大类监管事件统计
            case EvaluationUtil.statistics_checktype:
                result = evluationDao.queryEventByCheckType(evaluationReportSearchVO);
                break;
            // 成因类型监管事件统计
            case EvaluationUtil.statistics_geneticType:
                result = evluationDao.queryEventByGeneticType(evaluationReportSearchVO);
                break;
            //信息化工作机构成因类型事件统计
            case EvaluationUtil.statistics_inforOrg:
                result = evluationDao.queryEventByInforOrg(evaluationReportSearchVO);
                break;
            default:
                break;
        }
        return ResultUtil.successList(result);
    }

    /**
     * 非信息化工作机构成因类型事件统计
     * 未履行审批手续、人员误操作、人员故意违规、人员不熟悉相关规定、违反保密法律法规行为查处
     * @param evaluationReportSearchVO
     * @return
     */
    @Override
    public Result<List<NoInforOrgResultVO>> queryNoInforOrgStatistic(EvaluationReportSearchVO evaluationReportSearchVO) {
        List<NoInforOrgVO> result = evluationDao.queryEventByNoInforOrg(evaluationReportSearchVO);
        if(CollectionUtils.isEmpty(result)){
            return ResultUtil.successList(null);
        }
        Map<String,List<NoInforOrgVO>> groupMaps = result.stream().collect(Collectors.groupingBy(item -> item.getOrgName()));
        List<NoInforOrgResultVO> list = new ArrayList<>();
        Set<String> orgNames = groupMaps.keySet();
        for(String orgName : orgNames){
            List<NoInforOrgVO> datas=  groupMaps.get(orgName);
            NoInforOrgResultVO data=  getResultVO(datas,orgName);
            list.add(data);
        }
        return ResultUtil.successList(list);
    }
    private NoInforOrgResultVO getResultVO(List<NoInforOrgVO> datas, String orgName) {
        NoInforOrgResultVO data = new  NoInforOrgResultVO();
        data.setOrgName(orgName);
        for(NoInforOrgVO vo : datas){
            String genType =  vo.getGeneticType();
            int num =  vo.getNum();
            addNum(genType,num,data);
        }
        return data;
    }

    private void addNum(String genType, int num, NoInforOrgResultVO data) {
        switch (genType){
            case EvaluationUtil.GEN_1:
                data.setNum1(num);
                break;
            case EvaluationUtil.GEN_2:
                data.setNum2(num);
                break;
            case EvaluationUtil.GEN_3:
                data.setNum3(num);
                break;
            case EvaluationUtil.GEN_4:
                data.setNum4(num);
                break;
            case EvaluationUtil.GEN_5:
                data.setNum5(num);
                break;
        }
    }

    @Override
    public Result<List<SummaryStatisticVO>> querySummaryStatistics(EvaluationReportSearchVO evaluationReportSearchVO) {
        List<SummaryStatisticVO> result = new ArrayList<>();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.between("createTime",evaluationReportSearchVO.getStartTime(),evaluationReportSearchVO.getEndTime()));
        List<SelfInspectionEvaluation> evaluations = selfInspectionEvaluationService.findAll(conditions);
        for(SelfInspectionEvaluation data : evaluations){
            SummaryStatisticVO vo = mapperUtil.map(data,SummaryStatisticVO.class);
            int status = data.getStatus();
            if(EvaluationUtil.EV_RESULT_END == status){
                vo.setCurStatus("已自查自评");
            }else{
                vo.setCurStatus("未开始");
            }
            result.add(vo);
        }
        return ResultUtil.successList(result);
    }
    /**
     * 各类成因类型监管事件详情
     *  1. 自查自评结果表所有数据
     *  2. 成因类型分类统计
     * 请求参数
     * {"startTime":"2023-09-10 15:50:37","endTime":"2023-09-19 15:50:37"}
     */
    @Override
    public Result<List<GeneticTypeDetailVO>> queryGeneticTypeDetail(EvaluationReportSearchVO evaluationReportSearchVO) {
        logger.debug("各类成因类型监管事件详情开始");
        List<GeneticTypeDetailVO> result = new ArrayList<>();
        // 获取时间范围内数据
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.between("createTime",evaluationReportSearchVO.getStartTime(),evaluationReportSearchVO.getEndTime()));
        List<SelfInspectionEvaluation> evaluations = selfInspectionEvaluationService.findAll(conditions);
        if(CollectionUtils.isEmpty(evaluations)){
            return ResultUtil.successList(null);
        }
        // 成因类型分组
        Map<String,List<SelfInspectionEvaluation>> geneticTypes = evaluations.stream().collect(Collectors.groupingBy(item -> item.getGeneticType()));
        Set<String> geneticTypeKeys = geneticTypes.keySet();
        // 获取现有所有事件分类信息
        List<EventCategory> eventCategores= getEventCategories();
        // 获取所有用户信息:获取处理人名称用到
        List<User> allUsers = getAllUser();
        // 成因类型处理
        for(String geneticType: geneticTypeKeys){
            List<SelfInspectionEvaluation> datas = geneticTypes.get(geneticType);
            GeneticTypeDetailVO detail = geneticTypeHandle(datas,geneticType,eventCategores,allUsers);
            result.add(detail);
        }
        return ResultUtil.successList(result);
    }

    /**
     * 单个成因类型下数据的处理
     * @param datas
     * @param geneticType
     * @return
     */
    private GeneticTypeDetailVO geneticTypeHandle(List<SelfInspectionEvaluation> datas, String geneticType,List<EventCategory> eventCategores,List<User> allUsers) {
        GeneticTypeDetailVO geneticTypeDetailVO = new GeneticTypeDetailVO();
        geneticTypeDetailVO.setGeneticType(geneticType);
        // 构造自查自评结果信息数据及获取所有事件id
        List<String> eventIds = getEvaluationResult(datas,geneticTypeDetailVO);
        // 监管事件详情处理
        eventDetailHandle(eventIds,geneticTypeDetailVO,eventCategores,allUsers);
        return geneticTypeDetailVO;
    }



    /**
     * 构造自查自评结果信息数据及获取所有事件id
     * @param datas
     * @return
     */
    private List<String> getEvaluationResult(List<SelfInspectionEvaluation> datas,GeneticTypeDetailVO geneticTypeDetailVO) {
        List<EvaluationResultVO> evaluations = new ArrayList<>();
        Set<String> eventIds = new HashSet<>();
        for(SelfInspectionEvaluation data : datas){
            EvaluationResultVO vo = mapperUtil.map(data,EvaluationResultVO.class);
            evaluations.add(vo);
            String evIds = data.getEventIds();
            String[]  eid= evIds.split(",");
            for(String eventId : eid){
                eventIds.add(eventId);
            }
        }
        if(CollectionUtils.isNotEmpty(evaluations)){
            geneticTypeDetailVO.setEvaluations(evaluations);
        }
        if(eventIds.size() > 0){
            return new ArrayList<>(eventIds);
        }
        return new ArrayList<>();
    }

    /**
     * 监管事件详细处理
     * @param eventIds
     * @param geneticTypeDetailVO
     */
    private void eventDetailHandle(List<String> eventIds, GeneticTypeDetailVO geneticTypeDetailVO,List<EventCategory> eventCategores,List<User> allUsers) {
        if(CollectionUtils.isEmpty(eventIds)){
            return;
        }
        List<EventDetailVO> eventDetails = new ArrayList<>();
        // 根据事件GUID查询es事件数据
        List<AlarmEventAttribute> alarmEvents = getAlarmEvents(eventIds);

        if(CollectionUtils.isEmpty(alarmEvents)){
            return;
        }
        // 获取对应工单信息
        List<BusinessIntance> businessIntances = getBusInstances(eventIds);
        // 循环构造数据
        for(String evenId : eventIds){
            EventDetailVO vo = getEventDetailVO(alarmEvents,businessIntances,evenId,eventCategores,allUsers);
            if(null == vo){
                continue;
            }
            eventDetails.add(vo);
        }
        geneticTypeDetailVO.setEventDetails(eventDetails);
    }




    /**
     * 根据事件id获取对应事件信息
     * @param eventIds
     * @return
     */
    private List<AlarmEventAttribute> getAlarmEvents(List<String> eventIds){
        // 根据事件GUID查询es事件数据
        List<QueryCondition_ES> param = new ArrayList<>();
        param.add(QueryCondition_ES.in("eventId",eventIds));
        return alarmEventManagementForESService.findAll(param);
    }
    /**
     * 根据事件id获取对应工单西悉尼
     * @param eventIds
     * @return
     */
    private List<BusinessIntance> getBusInstances(List<String> eventIds) {
        List<QueryCondition> param = new ArrayList<>();
        param.add(QueryCondition.in("guid",eventIds));
        return businessIntanceService.findAll(param);
    }

    /**
     * 构造事件信息数据
     * @param alarmEvents
     * @param businessIntances
     * @param eventId
     * @return
     */
    private EventDetailVO getEventDetailVO(List<AlarmEventAttribute> alarmEvents, List<BusinessIntance> businessIntances, String eventId,List<EventCategory> eventCategores,List<User> allUsers) {
        EventDetailVO eventDetailVO = new EventDetailVO();
        AlarmEventAttribute alarmEventAttribute = getAlarmEventAttributeByGuid(eventId,alarmEvents);
        if(null == alarmEventAttribute){
            return null;
        }
        // 事件名称
        eventDetailVO.setEventName(alarmEventAttribute.getEventName());
        // 事件详情
        EventBaseMsgVO eventBaseMsg = new EventBaseMsgVO();
        eventBaseMsg.setEventTypeName(getEventTypeName(eventCategores,alarmEventAttribute.getEventCode()));
        eventBaseMsg.setEventCreattime(alarmEventAttribute.getEventCreattime());
        eventBaseMsg.setEventDetails(alarmEventAttribute.getEventDetails());
        eventDetailVO.setEventBaseMsg(eventBaseMsg);
        // 事件处置信息
        addEventHandleMsg(eventDetailVO,allUsers,alarmEventAttribute.getEventName(),businessIntances,eventId);
        return eventDetailVO;
    }

    private AlarmEventAttribute getAlarmEventAttributeByGuid(String eventId, List<AlarmEventAttribute> alarmEvents) {
        for(AlarmEventAttribute alarmEventAttribute : alarmEvents){
            if(eventId.equals(alarmEventAttribute.getEventId())){
                return alarmEventAttribute;
            }
        }
        return null;
    }

    /**
     * 事件处置信息数据构造
     * @param eventDetailVO
     * @param allUsers
     * @param eventName
     * @param businessIntances
     * @param eventId
     */
    private void addEventHandleMsg(EventDetailVO eventDetailVO, List<User> allUsers, String eventName, List<BusinessIntance> businessIntances,String eventId) {
        BusinessIntance businessIntance = getBusInstancesById(eventId,businessIntances);
        EventHandleMsgVO eventHandleMsg =new EventHandleMsgVO();
        eventHandleMsg.setEventName(eventName);
        if(null == businessIntance){
            eventDetailVO.setEventHandleMsg(eventHandleMsg);
            return ;
        }
        eventHandleMsg.setEventHandleDate(businessIntance.getFinishDate());
        String dealPeoples =  businessIntance.getDealPeoples();
        // |,| 分割数据，获取最后一个人作为处理人
        if(StringUtils.isNotEmpty(dealPeoples)){
            eventHandleMsg.setEventHandleUserName(getEventHandleUserName(dealPeoples,allUsers));
        }
        String busiArgs = businessIntance.getBusiArgs();
        if(StringUtils.isEmpty(busiArgs)){
            eventDetailVO.setEventHandleMsg(eventHandleMsg);
            return ;
        }
        /**
         * 是否误报：is_misreport 0表示是，1表示否
         * 是否造成失密：result_evaluation  1表示造成失泄密 0：表示未造成失泄密
         * 事件成因说明：cause
         * 事件详情过程：event_inquriy
         * 失泄密情况：result_details
         * 案件依据：case_base
         * 整改措施： zjgRevise
         */

        Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
        int isMisreport = map.get("is_misreport") == null? -1 : Integer.parseInt(String.valueOf(map.get("is_misreport")));
        String isMis =  isMisreportTransfer(isMisreport);
        int evaluation = map.get("result_evaluation") == null? -1 : Integer.parseInt(String.valueOf(map.get("result_evaluation")));
        String evaluationStr =  evaluationTransfer(evaluation);
        String cause = map.get("cause") == null?"":String.valueOf(map.get("cause"));
        String eventInquriy = map.get("event_inquriy") == null?"":String.valueOf(map.get("event_inquriy"));
        String resultDetails = map.get("result_details") == null?"":String.valueOf(map.get("result_details"));
        String caseBase = map.get("case_base") == null?"":String.valueOf(map.get("case_base"));
        String zjgRevise = map.get("zjgRevise") == null?"":String.valueOf(map.get("zjgRevise"));
        eventHandleMsg.setIsMisreport(isMis);
        eventHandleMsg.setResultEvaluation(evaluationStr);
        eventHandleMsg.setCause(cause);
        eventHandleMsg.setEventInquriy(eventInquriy);
        eventHandleMsg.setResultDetails(resultDetails);
        eventHandleMsg.setCaseBase(caseBase);
        eventHandleMsg.setZjgRevise(zjgRevise);
        eventDetailVO.setEventHandleMsg(eventHandleMsg);
        return;
    }

    private BusinessIntance getBusInstancesById(String eventId, List<BusinessIntance> businessIntances) {
        for(BusinessIntance businessIntance : businessIntances){
            if(eventId.equals(businessIntance.getGuid())){
                return businessIntance;
            }
        }
        return null;
    }


    /**
     *  是否误报：is_misreport 0表示是，1表示否
     * @param isMisreport
     * @return
     */
    private String isMisreportTransfer(int isMisreport) {
        if(0 == isMisreport){
            return "是";
        }
        if(1 == isMisreport){
            return "否";
        }
        return null;
    }

    /**
     * 是否造成失密：result_evaluation  1表示造成失泄密 0：表示未造成失泄密
     * @param evaluation
     * @return
     */
    private String evaluationTransfer(int evaluation) {
        if(0 == evaluation){
            return "未造成失泄密";
        }
        if(1 == evaluation){
            return "造成失泄密";
        }
        return null;
    }

    /**
     * 处理人处理
     * @param dealPeoples
     * @param allUsers
     */
    private String getEventHandleUserName(String dealPeoples, List<User> allUsers) {
        dealPeoples=  dealPeoples.substring(0,dealPeoples.lastIndexOf("|,|"));
        String[] userIds = dealPeoples.replace("|,|",",").split(",");
        String userId = userIds[userIds.length-1];
        String userName = getUserNameByUserId(userId,allUsers);
        return userName;
    }

    private String getUserNameByUserId(String userId, List<User> allUsers) {
        int usrId = Integer.parseInt(userId);
        for(User user : allUsers){
            if(usrId == user.getId()){
                return user.getName();
            }
        }
        return "";
    }


    /**
     * 查询事件分类
     *
     * @return
     */
    private List<EventCategory> getEventCategories() {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
        conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));
        return eventCategoryService.findAll(conditions);
    }
    /**
     * 获取所有用户信息
     * @return
     */
    private List<User> getAllUser() {
        try{
            VData<List<User>> allUser = adminFeign.getAllUser();
            return allUser.getData();
        }catch (Exception e){
            logger.error("通过Feign接口所有用户信息异常",e);
            throw new AlarmDealException(-1,"通过Feign接口所有用户信息异常");
        }
    }

    /**
     * 获取事件分类名称
     * @param eventCategores
     * @param eventCode
     * @return
     */
    private String getEventTypeName(List<EventCategory> eventCategores, String eventCode) {
        if(CollectionUtils.isEmpty(eventCategores)){
            logger.error("事件分类数据为空，不能获取事件分类名称");
            return "";
        }
        for(EventCategory eventCategory : eventCategores){
            if(eventCode.startsWith(eventCategory.getCodeLevel())){
                return eventCategory.getTitle();
            }
        }
      return "";
    }
}
