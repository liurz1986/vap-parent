package com.vrv.vap.alarmdeal.business.evaluation.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.evaluation.dao.EvluationDao;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.alarmdeal.business.evaluation.repository.SelfInspectionEvaluationRepository;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationConfigService;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EvaluationUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自查自评结果
 *
 * @Date 2023-09
 * @author liurz
 */
@Service
@Transactional
public class SelfInspectionEvaluationServiceImpl extends BaseServiceImpl<SelfInspectionEvaluation,String>  implements SelfInspectionEvaluationService {
    private static Logger logger = LoggerFactory.getLogger(SelfInspectionEvaluationServiceImpl.class);
    @Autowired
    private SelfInspectionEvaluationRepository selfInspectionEvaluationRepository;
    @Autowired
    private EvluationDao evluationDao;
    @Autowired
    private SelfInspectionEvaluationConfigService selfInspectionEvaluationConfigService;

    @Override
    public BaseRepository<SelfInspectionEvaluation, String> getRepository() {
        return selfInspectionEvaluationRepository;
    }

    /**
     * 列表展示：
     * 检查大类、成因类型、自查自评状态、自查自评项产生时间及自查自评开展时间的时间范围进行条件查询；同时支持根据待查部门名称进行模糊匹配查询
     * @param data
     * @return
     */
    @Override
    public PageRes<SelfInspectionEvaluation> getPage(SelfInspectionEvaluationSearchVO data) {
        logger.debug("自查自评结果展示列表分页查询:"+ JSON.toJSONString(data));
        PageReq pageReq=new PageReq();
        pageReq.setCount(data.getCount_());
        String order = data.getOrder_()==null?"createTime":data.getOrder_();
        String by = data.getBy_()==null?"desc":data.getBy_();
        pageReq.setBy(by);
        pageReq.setOrder(order);
        pageReq.setStart(data.getStart_());
        List<QueryCondition> conditions = getConditions(data);
        Page<SelfInspectionEvaluation> pager =findAll(conditions,pageReq.getPageable());
        List<SelfInspectionEvaluation> content = pager.getContent();
        PageRes<SelfInspectionEvaluation> pageRes =new PageRes<>();
        long totalElements = pager.getTotalElements();
        pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        pageRes.setList(content);
        pageRes.setTotal(totalElements);
        pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        pageRes.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        return pageRes;
    }

    /**
     * 执行自查自评
     * 1. 自查自评结果执行状态为已自查自评
     * 2. 删除对应的自查自评中间表关联数据
     * @param data
     * @return
     */
    @Override
    public Result<String> execute(SelfInspectionEvaluationVO data) {
        logger.debug("执行自查自评:"+ JSON.toJSONString(data));
        String id = data.getId();
        SelfInspectionEvaluation bean = this.getOne(id);
        if(null == bean){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评结果对象不存,id:"+id);
        }
        String evResult = data.getEvResult();
        if(StringUtils.isEmpty(evResult)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评结果不能为空");
        }
        bean.setEvResult(evResult);
        String rectification = data.getRectification();
        if(StringUtils.isEmpty(rectification)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "整改情况说明不能为空");
        }
        bean.setRectification(rectification);
        bean.setEvTime(new Date());
        bean.setEvUserCode(SessionUtil.getCurrentUser().getOrgCode());
        bean.setEvUserName(SessionUtil.getCurrentUser().getName());
        //自查自评结果执行状态为：已自查自评
        bean.setStatus(EvaluationUtil.EV_RESULT_END);
        this.save(bean);
        // 删除对应的自查自评中间表关联数据
        String processIds = bean.getRefProcessId();
        String[] procIds = processIds.split(",");
        List<String> ids = Arrays.asList(procIds);
        logger.info("执行自查自评后删除关联中间表数据，关联id为："+JSON.toJSONString(ids));
        evluationDao.deleteProcessIds(ids);
        return ResultUtil.success("success");
    }

    /**
     *  检查大类、成因类型、自查自评状态、自查自评项产生时间及自查自评开展时间的时间范围进行条件查询；同时支持根据待查部门名称进行模糊匹配查询
     * @param data
     * @return
     */
    private List<QueryCondition> getConditions(SelfInspectionEvaluationSearchVO data) {
        List<QueryCondition> conditions = new ArrayList<>();
        // 检查大类
        String checkType = data.getCheckType();
        if(StringUtils.isNotEmpty(checkType)){
            conditions.add(QueryCondition.eq("checkType",checkType));
        }
        // 成因类型
        String geneticType = data.getGeneticType();
        if(StringUtils.isNotEmpty(geneticType)){
            conditions.add(QueryCondition.eq("geneticType",geneticType));
        }
        // 自查自评状态
        String status = data.getStatus();
        if(StringUtils.isNotEmpty(status)){
            conditions.add(QueryCondition.eq("status",Integer.parseInt(status)));
        }
        // 自查自评项产生时间
        Date createTimeStart = data.getCreateTimeStart();
        Date createTimeEnd = data.getCreateTimeEnd();
        if(null != createTimeStart && null!=createTimeEnd){
            conditions.add(QueryCondition.between("createTime",createTimeStart,createTimeEnd));
        }
        // 自查自评开展时间
        Date evTimeStart = data.getEvTimeStart();
        Date evTimeEnd = data.getEvTimeEnd();
        if(null != evTimeStart && null!=evTimeEnd){
            conditions.add(QueryCondition.between("evTime",evTimeStart,evTimeEnd));
        }
        //待查部门名称糊查询
        String orgName = data.getOrgName();
        if(StringUtils.isNotEmpty(orgName)){
            conditions.add(QueryCondition.like("orgName",orgName));
        }
        return conditions;
    }
    /**
     * 自查自评结果状态统计
     * 环形图展示所有机构的自查自评项总数、已自查自评项总数、未进行自查自评项总数
     * @return
     */
    @Override
    public Result<Map<String, Object>> statusStatistics() {

        return ResultUtil.success(evluationDao.statusStatistics());
    }

    /**
     * 自查自评结果按成因类型统计分类，统计每个成因类型下的每个待查部门的数据
     *
     * 堆叠柱状图展示所有机构自查自评项成因占比，支持横向滚动显示。
     * @return
     */
    @Override
    public Result<Map<String,Object>> depAndGeneticStatistics() {
        Map<String,Object> returnRes = new HashMap<>();
        // 按成因类型、待查部门分组汇总
        List<Map<String,Object>> list = evluationDao.depAndGeneticStatistics();
        if(CollectionUtils.isEmpty(list)){
            returnRes.put("dataList",null);
            returnRes.put("depNames",null);
            return ResultUtil.success(returnRes);
        }
        // 数据按成因类型分组
        Map<String,List<Map<String,Object>>> groups = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("geneticType"))));
        Set<String> geneticTypes = groups.keySet();
        List<DepAndGeneticStatisticsVO> result = new ArrayList<>();
        DepAndGeneticStatisticsVO data = null;
        for(String key  : geneticTypes){
            data = new DepAndGeneticStatisticsVO();
            data.setGeneticType(key);
            List<Map<String,Object>> deps =  groups.get(key);
            List<KeyValueVO> types = getTypes(deps);
            data.setDatas(types);
            result.add(data);
        }
        // 图形数据
        returnRes.put("dataList",result);
        // 数据按部门分组,获取现有数据所有待查部门
        Map<String,List<Map<String,Object>>> depNamMap = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("orgName"))));
        // 现有数据所有待查部门
        returnRes.put("depNames",depNamMap.keySet());
        return ResultUtil.success(returnRes);
    }

    @Override
    public Result<EvaluationResultDeatilVO> getDetailById(String id) {
        SelfInspectionEvaluation bean = this.getOne(id);
        if(null == bean){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "自查自评结果对象不存,id:"+id);
        }
        EvaluationResultDeatilVO vo = new EvaluationResultDeatilVO();
        vo.setId(bean.getId());
        vo.setCheckType(bean.getCheckType());
        vo.setGeneticType(bean.getGeneticType());
        vo.setEvResult(bean.getEvResult());
        vo.setRectification(bean.getRectification());
        // 获取对应策略中推荐原因
        int refId = bean.getRefId();
        SelfInspectionEvaluationConfig configBean = selfInspectionEvaluationConfigService.getOne(refId);
        String sellReason = configBean.getSellReason();
        vo.setPrompt(sellReason);
        return ResultUtil.success(vo);
    }
    private List<KeyValueVO> getTypes(List<Map<String, Object>> deps) {
        List<KeyValueVO> results = new ArrayList<>();
        KeyValueVO keyValueVO = null;
        for(Map<String, Object> map : deps){
            keyValueVO = new KeyValueVO();
            keyValueVO.setName(String.valueOf(map.get("orgName")));
            keyValueVO.setNum(Integer.parseInt(String.valueOf(map.get("number"))));
            results.add(keyValueVO);
        }
        return results;
    }





}
