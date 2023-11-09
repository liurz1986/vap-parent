package com.vrv.vap.alarmdeal.business.evaluation.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationProcess;
import com.vrv.vap.alarmdeal.business.evaluation.service.EventDataService;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationConfigService;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationProcessService;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EvaluationUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.EventResultVO;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 自查自评结果生成
 *
 * 事件处置完后数据处理
 * @Date 2023-09
 * @author liurz
 */
@Service
public class EventDataServiceImpl implements EventDataService {
    private static Logger logger = LoggerFactory.getLogger(EventDataServiceImpl.class);

    @Autowired
    private SelfInspectionEvaluationConfigService selfInspectionEvaluationConfigService;

    @Autowired
    private SelfInspectionEvaluationProcessService selfInspectionEvaluationProcessService;

    @Autowired
    private SelfInspectionEvaluationService selfInspectionEvaluationService;

    /**
     * 事件处置完后数据处理
     * @param data
     */
    @Override
    @Transactional
    public void process(EventResultVO data) {
        try{
            logger.info("事件处置完后数据-自查自评处理开始，数据为："+ JSON.toJSONString(data));
            // 数据必填校验
            Map<String,Object> mustRes =  isMust(data);
            if(!Boolean.parseBoolean(String.valueOf(mustRes.get("status")))){
                logger.error("事件处置完后数据-自查自评处理-数据不处理："+mustRes.get("msg"));
                return;
            }
            // 数据帅选,根据涉事人员角色筛选符合的策略信息
            Map<String,Object> dataScreenRes =  dataScreen(data);
            if(!Boolean.parseBoolean(String.valueOf(dataScreenRes.get("status")))){
                logger.error("事件处置完后数据-自查自评处理-数据不处理："+dataScreenRes.get("msg"));
                return;
            }
            // 当前成因类型对应的相关策略信息
            List<SelfInspectionEvaluationConfig> configs = (List<SelfInspectionEvaluationConfig>)dataScreenRes.get("data");
            logger.info("当前成因类型对应的相关策略信息(经过筛选后的)："+JSON.toJSONString(configs));
            // 中间数据处理
            evaluationProcessHandle(configs,data);
            // 根据策略判断数据入自查自评结果库
            excEvaluationResult(configs);
            logger.info("事件处置完后数据-自查自评处理结束");
        }catch (Exception e){
            logger.error("事件处置完后数据-自查自评处理异常:{}",e);
            throw new AlarmDealException(-1,"事件处置完后数据-自查自评处理异常");
        }
    }

    /**
     * 根据策略判断数据入自查自评结果库
     * @param configs
     */
    private void excEvaluationResult(List<SelfInspectionEvaluationConfig> configs ) {
        List<SelfInspectionEvaluation> evresults = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        String geneticType = null;
        for(SelfInspectionEvaluationConfig config : configs){
            // 获取单个策略下所有中间表数据
            geneticType = config.getGeneticType();
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("refId",config.getId()));
            List<SelfInspectionEvaluationProcess> list =  selfInspectionEvaluationProcessService.findAll(conditions);
            // 策略命中判断
            boolean isResult = EvaluationUtil.determine(list,config);
            if(isResult){
                evresults.addAll(getEvResults(list));
                ids.addAll(list.stream().map(item -> item.getId()).collect(Collectors.toList()));
                logger.info("策略命中，策略id为:{}",config.getId());
            }
        }
        if (evresults.size() > 0){
            batchSaveEvResult(evresults,geneticType);
        }
    }

    /**
     * 保存自查自评结果信息
     *
     * @param evresults
     */
    private void batchSaveEvResult(List<SelfInspectionEvaluation> evresults ,String geneticType) {
        // 数据根据待查部门、检查大类、成因类型、策略ID唯一生成新数据(发生数量、关联中间表id、事件id)进行汇总
        List<SelfInspectionEvaluation> resultDatas = getStructureResult(evresults);
        int total = resultDatas.size();
        logger.info("生成自查自评记录个数："+total);
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("geneticType",geneticType));
        conditions.add(QueryCondition.eq("status",EvaluationUtil.EV_RESULT_START));
        List<SelfInspectionEvaluation> list =  selfInspectionEvaluationService.findAll(conditions);
        if(CollectionUtils.isEmpty(list)){
            logger.info("新增自查自评记录个数："+total);
            selfInspectionEvaluationService.save(resultDatas);
            return;
        }
        // 对存在的数据进行更新
        int count=0;
        for(SelfInspectionEvaluation result : resultDatas){
            SelfInspectionEvaluation oldData = getOldData(result,list);
            if(null != oldData){
                result.setId(oldData.getId());
                result.setCreateTime(oldData.getCreateTime());
                count ++;
            }
        }
        logger.info("新增自查自评记录个数："+(total-count)+",更新自查自评记录个数："+count);
        selfInspectionEvaluationService.save(resultDatas);
    }

    /**
     * 根据待查部门、检查大类、成因类型、策略重新组装数据
     *
     *  发生数量、关联中间表id、事件id 进行汇总
     * @param evresults
     * @return
     */
    private List<SelfInspectionEvaluation> getStructureResult(List<SelfInspectionEvaluation> evresults) {
        Map<String,SelfInspectionEvaluation> maps = new HashMap<>();
        for(SelfInspectionEvaluation ev : evresults){
            String checkType = ev.getCheckType();
            String depName = ev.getOrgName();
            String genetic = ev.getGeneticType();
            int refId = ev.getRefId();
            String key = checkType+"-"+depName+"-"+genetic+"-"+refId;
            if(maps.containsKey(key)){
                SelfInspectionEvaluation oldData= maps.get(key);
                // 关联中间表Id
                String processId = oldData.getRefProcessId();
                processId = processId +","+ev.getRefProcessId();
                // 发生次数
                int count = oldData.getOccurCount();
                count = count+ev.getOccurCount();
                // 关联事件id
                String eventId = oldData.getEventIds();
                eventId = eventId+","+ev.getEventIds();
                oldData.setRefProcessId(processId);
                oldData.setOccurCount(count);
                oldData.setEventIds(eventId);
                maps.put(key,oldData);
            }else{
                maps.put(key,ev);
            }
        }
        List<SelfInspectionEvaluation> newDatas = new ArrayList<>(maps.values());
        return newDatas;
    }

    private SelfInspectionEvaluation getOldData(SelfInspectionEvaluation result, List<SelfInspectionEvaluation> list) {
        String checkType = result.getCheckType();
        String depName = result.getOrgName();
        String genetic = result.getGeneticType();
        int refId = result.getRefId();
        for(SelfInspectionEvaluation data : list){
            String checkTypeO = data.getCheckType();
            String depName0 = data.getOrgName();
            String genetic0 = data.getGeneticType();
            int refId0 = data.getRefId();
            if(checkType.equals(checkTypeO)&&depName.equals(depName0)&&genetic.equals(genetic0)&&(refId == refId0)){
                return data;
            }
        }
        return null;
    }


    private List<SelfInspectionEvaluation> getEvResults(List<SelfInspectionEvaluationProcess> list) {
        List<SelfInspectionEvaluation> evresults = new ArrayList<>();
        SelfInspectionEvaluation ev = null;
        for(SelfInspectionEvaluationProcess data : list){
            ev = new SelfInspectionEvaluation();
            ev.setId(UUIDUtils.get32UUID());
            ev.setCreateTime(new Date());
            ev.setStatus(EvaluationUtil.EV_RESULT_START);
            ev.setCheckType(data.getCheckType());
            ev.setGeneticType(data.getGeneticType());
            ev.setEventIds(data.getEventIds());
            ev.setOccurCount(data.getEventCount());
            ev.setOrgName(data.getCheckDep());
            ev.setRefProcessId(data.getId());
            ev.setRefId(data.getRefId());
            evresults.add(ev);
        }
        return evresults;
    }

    /**
     * 中间数据处理
     * @param configs
     * @param data
     * @return
     */
    private void evaluationProcessHandle(List<SelfInspectionEvaluationConfig> configs, EventResultVO data){
        String orgName = data.getOrgName();
        String eventIds = data.getEventGuid();
        String userName = data.getUserName();
        List<SelfInspectionEvaluationProcess> list = new ArrayList<>();
        for(SelfInspectionEvaluationConfig config : configs) {
            SelfInspectionEvaluationProcess newprocess = new SelfInspectionEvaluationProcess();
            String ckeckType = config.getCheckType();
            String geneticType = config.getGeneticType();
            String checkdepartment = config.getCheckdepartment();
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("geneticType",geneticType));
            conditions.add(QueryCondition.eq("checkType",ckeckType));
            conditions.add(QueryCondition.eq("orgName",orgName));
            conditions.add(QueryCondition.eq("userName",userName));
            conditions.add(QueryCondition.eq("refId",config.getId()));
            List<SelfInspectionEvaluationProcess> process =  selfInspectionEvaluationProcessService.findAll(conditions);
            if(CollectionUtils.isEmpty(process)){
                newprocess.setId(UUIDUtils.get32UUID());
                newprocess.setEventCount(1);
                newprocess.setEventIds(eventIds);
                newprocess.setCheckType(ckeckType);
                newprocess.setGeneticType(geneticType);
                newprocess.setOrgName(orgName);
                newprocess.setUserName(userName);
                newprocess.setCheckDep(EvaluationUtil.checkdepTansfer(checkdepartment,orgName));
                newprocess.setRefId(config.getId());
                list.add(newprocess);
            }else{ // 更新操作：事件id、事件数量累加处理
                SelfInspectionEvaluationProcess processOld =  process.get(0);
                processOld.setEventCount(processOld.getEventCount()+1);
                String enventIds = processOld.getEventIds();
                enventIds = enventIds+","+eventIds;
                processOld.setEventIds(enventIds);
                list.add(processOld);
            }
        }
        // 保存或更新中间表数据
        if(CollectionUtils.isNotEmpty(list)){
            selfInspectionEvaluationProcessService.save(list);
        }
        return ;
    }


    /**
     * 数据帅选：
     * 1.成因类型在策略表中
     * 2.成因类型对应的检查大类，对涉事责任人有求进行筛选
     *    事件责任人角色名称符合策略中成因类型涉事责任人角色名称
     * @param data
     * @return
     */
    private Map<String,Object> dataScreen(EventResultVO data){
        Map<String,Object> restult = new HashMap<>();
        String geneticType = data.getGeneticType();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("geneticType",geneticType));
        List<SelfInspectionEvaluationConfig> list =  selfInspectionEvaluationConfigService.findAll(conditions);
        if(CollectionUtils.isEmpty(list)){
            restult.put("status",false);
            restult.put("msg","成因类型在策略表中不存在，当前成因类型为："+geneticType);
            return restult;
        }
       // 成因类型对应的检查大类，对涉事责任人有要求的进行筛选
        List<SelfInspectionEvaluationConfig> configs = new ArrayList<>();
        String eventRoleName = data.getRoleName();
        for(SelfInspectionEvaluationConfig config : list){
            String roleName = config.getRoleName();
            // 策略中配置的为 无要求，不筛选处理
            if(EvaluationUtil.roleName.equals(roleName)){
                configs.add(config);
                continue;
            }
            if(roleName.equals(eventRoleName)){
                configs.add(config);
            }else{
                logger.info("当前成因类型"+geneticType+"、检查大类"+config.getCheckType()+"、涉事角色要求为："+roleName+",当前数据实际角色为："+eventRoleName+",当前数据不符合当前策略");
            }
        }
        if(CollectionUtils.isEmpty(configs)){
            restult.put("status",false);
            restult.put("msg","没有匹配上策略，当前成因类型为："+geneticType);
            return restult;
        }
        restult.put("status",true);
        restult.put("data",configs);
        return restult;
    }

    /**
     * 数据校验：
     * 1.必填 ：事件Id,事件成因类型、部门名称、事件处理人名称
     * @param data
     * @return
     */
    private Map<String,Object> isMust(EventResultVO data){
        Map<String,Object> restult = new HashMap<>();
        String geneticType = data.getGeneticType();
        if(StringUtils.isEmpty(geneticType)){
            restult.put("status",false);
            restult.put("msg","成因类型不能为空");
            return restult;
        }
        String dataEventGuid = data.getEventGuid();
        if(StringUtils.isEmpty(dataEventGuid)){
            restult.put("status",false);
            restult.put("msg","事件Id不能为空");
            return restult;
        }
        String orgName = data.getOrgName();
        if(StringUtils.isEmpty(orgName)){
            restult.put("status",false);
            restult.put("msg","部门名称不能为空");
            return restult;
        }
        String userName = data.getUserName();
        if(StringUtils.isEmpty(userName)){
            restult.put("msg","事件责任人名称不能为空");
            restult.put("status",false);
            return restult;
        }
        restult.put("status",true);
        return restult;
    }
}
