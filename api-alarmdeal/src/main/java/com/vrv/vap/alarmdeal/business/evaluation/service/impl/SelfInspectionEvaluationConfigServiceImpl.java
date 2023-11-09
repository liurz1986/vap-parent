package com.vrv.vap.alarmdeal.business.evaluation.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.evaluation.dao.EvluationDao;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.alarmdeal.business.evaluation.repository.SelfInspectionEvaluationConfigRepository;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationConfigService;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EvaluationUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 自查自评策略配置
 *
 *
 * @Date 2023-09
 * @author liurz
 */
@Service
@Transactional
public class SelfInspectionEvaluationConfigServiceImpl extends BaseServiceImpl<SelfInspectionEvaluationConfig,Integer>  implements SelfInspectionEvaluationConfigService  {
    private static Logger logger = LoggerFactory.getLogger(SelfInspectionEvaluationConfigServiceImpl.class);
    @Autowired
    private SelfInspectionEvaluationConfigRepository selfInspectionEvaluationConfigRepository;
    @Autowired
    private SelfInspectionEvaluationService selfInspectionEvaluationService;
    @Autowired
    private EvluationDao evluationDao;
    @Autowired
    private MapperUtil mapper;
    @Override
    public BaseRepository<SelfInspectionEvaluationConfig, Integer> getRepository() {
        return selfInspectionEvaluationConfigRepository;
    }

    /**
     * 自查自评策略配置展示列表分页查询
     *  分页
     *  检查大类条件查询
     * @param data
     * @return
     */
    @Override
    public PageRes<SelfInspectionEvaluationConfig> getPage(SelfInspectionEvaluationConfigSearchVO data) {
        logger.debug("自查自评策略配置展示列表分页查询:"+ JSON.toJSONString(data));
        PageReq pageReq=new PageReq();
        pageReq.setCount(data.getCount_());
        pageReq.setBy("desc");
        pageReq.setOrder("id");
        pageReq.setStart(data.getStart_());
        String checkType = data.getCheckType();
        Page<SelfInspectionEvaluationConfig> pager = null;
        if(StringUtils.isNotEmpty(checkType)){
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("checkType",data.getCheckType()));
            pager = findAll(conditions,pageReq.getPageable());
        }else{
            pager = findAll(pageReq.getPageable());
        }
        List<SelfInspectionEvaluationConfig> content = pager.getContent();
        PageRes<SelfInspectionEvaluationConfig> pageRes =new PageRes<>();
        long totalElements = pager.getTotalElements();
        pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        pageRes.setList(content);
        pageRes.setTotal(totalElements);
        pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        pageRes.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        return pageRes;
    }

    /**
     * 当推荐策略下存在未处置完的自查自评项
     * @param data
     * @return
     */
    @Override
    public Result<String> editValidate(SelfInspectionEvaluationConfigVO data) {
        logger.debug("自查自评策略配置编辑时校验:"+ JSON.toJSONString(data));
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("refId",data.getId()));
        conditions.add(QueryCondition.eq("status", EvaluationUtil.EV_RESULT_START));
        List<SelfInspectionEvaluation> list = selfInspectionEvaluationService.findAll(conditions);
        if(list.size() > 0){
            return ResultUtil.success("fail");
        }
        return ResultUtil.success("success");
    }

    /**
     * 编辑
     * 目前只能编辑：部门数量、事件频率阈值
     *
     * 部门数量大于1的正整数
     * 事件频率阈值次数：大于0的整数
     * @param data
     * @return
     */
    @Override
    public Result<SelfInspectionEvaluationConfig> edit(SelfInspectionEvaluationConfigVO data) {
        logger.debug("自查自评策略配置编辑:"+ JSON.toJSONString(data));
        SelfInspectionEvaluationConfig oldData= this.getOne(data.getId());
        if(null == oldData){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑的对象不存在，id为："+data.getId());
        }
        // 部门数量是否支持修改(是、否)
        String departmentModify =  oldData.getDepartmentModify();
        if("是".equals(departmentModify)){
            int departmentnum =  data.getDepartmentnum();
            if(departmentnum <= 1){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"部门数量需要大于1");
            }
            oldData.setDepartmentnum(departmentnum);
        }
        // 事件频率阈值次数
        int thresholdCount = data.getThresholdCount();
        if(thresholdCount <= 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"事件频率阀值需要大于0");
        }
        oldData.setThresholdCount(thresholdCount);
        this.save(oldData);
        return ResultUtil.success(oldData);
    }

    /**
     * 检查大类列表
     * @return
     */
    @Override
    public Result<List<ConifgTreeVO>> getTree() {
        List<ConifgTreeVO> datas = evluationDao.getTree();
        return ResultUtil.successList(datas);
    }

    /**
     * 推荐策略详情弹窗页或自查自评推荐策略信息
     *  参数：
     * {"id":12}
     *  备注：自查自评推荐策略信息中对应的ref_id
     * @return
     */
    @Override
    public Result<ConfigDeatilVO> getDetail(int  id) {
        SelfInspectionEvaluationConfig config =this.getOne(id);
        ConfigDeatilVO bean = mapper.map(config,ConfigDeatilVO.class);
        // 推荐条件处理
        String conditons = bean.getSellConditions();
        conditons = conditons.replace("${depNum}",config.getDepartmentnum()+"").replace("${thresholdCont}",config.getThresholdCount()+"");
        bean.setSellConditions(conditons);
        return ResultUtil.success(bean);
    }

    /**
     * 获取策略中所有检查大类和成因类型
     * @return
     */
    @Override
    public Result<Map<String,Object>> getAllCheckTypeAndGeneticType(){
        Map<String,Object> result = new HashMap<>();
        List<SelfInspectionEvaluationConfig> list = this.findAll();
        Map<String,List<SelfInspectionEvaluationConfig>> checkTypeMap= list.stream().collect(Collectors.groupingBy(item -> item.getCheckType()));
        Map<String,List<SelfInspectionEvaluationConfig>> geneticTypeMap = list.stream().collect(Collectors.groupingBy(item -> item.getGeneticType()));
        result.put("checkTypes",new ArrayList<>(checkTypeMap.keySet()));
        result.put("geneticTyps",new ArrayList<>(geneticTypeMap.keySet()));
        return ResultUtil.success(result);
    }
}
