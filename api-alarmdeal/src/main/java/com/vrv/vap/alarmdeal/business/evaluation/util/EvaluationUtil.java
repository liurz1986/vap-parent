package com.vrv.vap.alarmdeal.business.evaluation.util;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationProcess;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * 自查自评工具
 *
 * 2023-09-06
 * @author liurz
 */
public class EvaluationUtil {
    private static Logger logger = LoggerFactory.getLogger(EvaluationUtil.class);
    public static String roleName="无要求";

    public static int departmentnum= 0; // 全部

    public static String isModify= "是";

    public static String dimension_dep="dep";

    /**
     * 自查自评结果表状态 0:未开始,1:已自查自评
     */
    public static int EV_RESULT_START=0;
    /**
     * 自查自评结果表状态 0:未开始,1:已自查自评
     */
    public static int EV_RESULT_END=1;


    /**
     * 涉事人员所属部门
     */
    public final static String CHCK_DEP_1="涉事人员所属部门";
    /**
     * 信息化工作机构
     */
    public final static String CHCK_DEP_2="信息化工作机构";
    /**
     * 保密工作机构
     */
    public final static String CHCK_DEP_3="保密工作机构";
    /**
     * 所有部门
     */
    public final static String CHCK_DEP_4="机关单位内所有部门";


    /**
     *  检查大类监管事件统计:checktype
     * 成因类型监管事件统计:geneticType
     *  信息化工作机构成因类型事件统计: inforOrg
     * 非信息化工作机构成因类型事件统计:noInforOrg
     */
    public final static String statistics_checktype="checktype";
    public final static String statistics_geneticType="geneticType";
    public final static String statistics_inforOrg="inforOrg";
    public final static String statistics_noInforOrg="noInforOrg";


    public final static String GEN_1="未履行审批手续";
    public final static String GEN_2="人员误操作";
    public final static String GEN_3="人员故意违规";
    public final static String GEN_4="人员不熟悉相关规定";
    public final static String GEN_5="违反保密法律法规行为查处";

    /**
     * 策略中待查部门转化成自查自评结果中待查部门
     *
     * 策略中待查部门：映射到展示时的待查部门
     * 涉事人员所属部门---事件中具体部门
     * 信息化工作机构--信息化工作机构
     * 保密工作机构--保密工作机构
     * 机关单位内所有部门--所有部门
     * @param configDep 策略配置中待查部门
     * @param eventDep 展示时的待查部门
     * @return
     */
    public static String checkdepTansfer(String configDep,String eventDep){
        switch (configDep){
            case  CHCK_DEP_1:
                return eventDep;
            case CHCK_DEP_2:
            case CHCK_DEP_3:
                return configDep;
            case CHCK_DEP_4:
                return "所有部门";
            default:
                return configDep;
        }
    }

    /**
     * 策略命中判断
     *
     * @param list 成因类型、检查大类下所有中间表数据
     * @param config 策略中事件频率阀值
     * @return
     */
    public static boolean determine(List<SelfInspectionEvaluationProcess> list, SelfInspectionEvaluationConfig config) {
        logger.debug("执行策略命中判断开始：当前策略："+ JSON.toJSONString(config));
        logger.debug("执行策略命中判断开始：当前数据："+ JSON.toJSONString(list));
        // 事件频率阀值
        int thresholdCount = config.getThresholdCount();
        // 事件部门数量
        int departmentnum = config.getDepartmentnum();
        // 部门数量是否支持修改(是、否)
        String departmentModify = config.getDepartmentModify();
        // 策略维度
        String dimension = config.getDimension();
        // 部门维度
        if(dimension_dep.equals(dimension)){
            logger.debug("部门维度处理");
            return depDimensionProcess(departmentnum,list,thresholdCount,departmentModify);
        }else{
            // 人员维度(涉密人员管理--人员故意违规--涉事人员所属部门)
            logger.debug("人员维度处理");
            return userDimensionProcess(list,config);
        }
    }



    /**
     * 部门维度: 策略命中判断
     * @param departmentnum
     * @param list
     * @param thresholdCount
     * @param departmentModify
     * @return
     */
    private static boolean depDimensionProcess(int departmentnum, List<SelfInspectionEvaluationProcess> list, int thresholdCount, String departmentModify) {
        //策略中事件部门设置值 :0表示全球
        if(EvaluationUtil.departmentnum == departmentnum){
            logger.debug("策略中事件部门设置值为全部的处理");
            return getDepartmentAll(list,thresholdCount);
        }else{ // 为数字的情况
            logger.debug("策略中事件部门设置值为非全部的处理");
            return getDepartmentNum(list,thresholdCount,departmentnum,departmentModify);
        }
    }

    /**
     *  角色维度: 策略命中判断
     *
     *  目前针对 ：涉密人员管理--人员故意违规--涉事人员所属部门,其他不处理
     *  两种场景满足一个就可以
     *   1. 同一个部门中同一个人员的次数大于事件频率阀值
     *   2. 同一个部门人员个数大于事件频率阀值
     * @param list
     * @param config
     * @return
     */
    private static boolean userDimensionProcess(List<SelfInspectionEvaluationProcess> list,SelfInspectionEvaluationConfig config) {
       int thresholdCount =  config.getThresholdCount();
        String checkType = config.getCheckType();
        String geneticType = config.getGeneticType();
        if("涉密人员管理".equals(checkType)&& "人员故意违规".equals(geneticType)&&"涉事人员所属部门".equals(config.getCheckdepartment())){
            logger.debug("涉密人员管理-人员故意违规-涉事人员所属部门涉及策略处理");
            // 同一个部门中同一个人员的次数大于事件频率阀值场景处理
            boolean result1 = oneUserNameProcess(list,thresholdCount);
            if(result1){
                logger.debug("同一个部门中同一个人员的次数大于事件频率阀值，命中成功");
                return true;
            }
            // 同一个部门人员个数大于事件频率阀值场景处理
            boolean result2 = userNamesProcess(list,thresholdCount);
            if(result2){
                logger.debug(" 同一个部门人员个数大于事件频率阀值，命中成功");
                return true;
            }
            return false;
        }
        return false;
    }



    /**
     * 同一个部门中同一个人员的次数大于事件频率阀值场景处理
     * @param list
     * @param thresholdCount
     * @return
     */
    private static boolean oneUserNameProcess(List<SelfInspectionEvaluationProcess> list, int thresholdCount) {
        // 按部门分组
        Map<String, List<SelfInspectionEvaluationProcess>> map = list.stream().collect(Collectors.groupingBy(item -> item.getOrgName()));
        Set<String> keys = map.keySet();
        for(String key : keys){
            List<SelfInspectionEvaluationProcess> depDatas =  map.get(key);
            // 同一个部门，同一个人员的次数大于 事件频率阀值
            for(SelfInspectionEvaluationProcess process : depDatas){
                int userCount = process.getEventCount();
                if(userCount > thresholdCount){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 同一个部门人员个数大于事件频率阀值场景处理
     * @param list
     * @param thresholdCount
     * @return
     */
    private static boolean userNamesProcess(List<SelfInspectionEvaluationProcess> list, int thresholdCount) {
        // 按部门分组
        Map<String, List<SelfInspectionEvaluationProcess>> map = list.stream().collect(Collectors.groupingBy(item -> item.getOrgName()));
        Set<String> keys = map.keySet();
        for(String key : keys){
            List<SelfInspectionEvaluationProcess> depDatas =  map.get(key);
            // 按用户分组
            Map<String, List<SelfInspectionEvaluationProcess>> userNaemMap = depDatas.stream().collect(Collectors.groupingBy(item -> item.getUserName()));
            // 一个部门下人员个数
            int userCount = userNaemMap.keySet().size();
            if(userCount > thresholdCount){
                return true;
            }
        }
        return false;
    }



    /**
     * 策略中事件部门设置值为“全部‘
     * 1. 成因类型、检查大类下所有中间表所有事件次数总和  > 策略中事件频率阀值
     * @param list
     * @param thresholdCount
     * @return
     */
    private static boolean getDepartmentAll(List<SelfInspectionEvaluationProcess> list, int thresholdCount) {
        int sum= 0;
        for(SelfInspectionEvaluationProcess data : list){
            int eventCount = data.getEventCount();
            sum = sum + eventCount;
        }
        if(sum > thresholdCount){
            return true;
        }
        return false;
    }

    /**
     * 策略中事件部门设置值 :为具体数字
     * 1. 部门数量是否支持修改 ：为是的情况
     *  1. 部门个数判断：统计部门个数，判断部门个数 > 策略中事件部门数量
     *  2.(事件数量大于策略中事件频率阀值的次数)与(策略中事件部门数量)判断： (事件数量大于策略中事件频率阀值的次数) >(策略中事件部门数量)
     *     备注：事件部门数量如果设置为2 ，就是存在以上两个部门，并且事件数量大于策略中事件频率阀值
     * 2. 部门数量是否支持修改 ：为否的情况
     *  其中一个部门的事件数量 > 事件频率阀值默认值
     * @param list
     * @param thresholdCount
     * @return
     */
    private static boolean getDepartmentNum(List<SelfInspectionEvaluationProcess> list, int thresholdCount,int departmentnum,String departmentModify) {
        if(departmentnum < 1){
            throw new AlarmDealException(-1,"策略中事件部门数量值不能小于1");
        }
        // 部门数量是否支持修改 :是、否
        if(isModify.equals(departmentModify)){
            logger.debug(" 部门数量是否支持修改,为是的情况");
            return departmentModifyHandle(list,thresholdCount,departmentnum);
        }else{
            logger.debug(" 部门数量是否支持修改,为否的情况");
            return departmentModifyNoHandle(list,thresholdCount);
        }
    }



    /**
     *部门数量是否支持修改 ：为是的情况
     *  部门个数判断：统计部门个数，判断部门个数 > 策略中事件部门默认数量
     *  (单个部门事件数量大于策略中事件频率阀值的次数)与(策略中事件部门默认数量)判断： (事件数量大于策略中事件频率阀值的部门次数) >(策略中事件部门默认数量)
     *  备注：事件部门数量如果设置为2 ，就是存在以上两个部门，并且事件数量大于策略中事件频率阀值
     * @param list
     * @param thresholdCount
     * @param depNum
     * @return
     */
    private static boolean departmentModifyHandle(List<SelfInspectionEvaluationProcess> list, int thresholdCount, int depNum) {
        // 判断部门个数 > 策略中事件部门默认数量
        Map<String, List<SelfInspectionEvaluationProcess>> map = list.stream().collect(Collectors.groupingBy(item -> item.getOrgName()));
        int curDepCount = map.keySet().size();
        if(curDepCount <= depNum){
            return false;
        }
        // 存在多少部门，单个部门中事件数量 > 策略中事件频率阀值
        int count = 0;
        Set<String> keys = map.keySet();
        for(String key : keys){
            // 单个部门中事件数量 > 策略中事件频率阀值
            List<SelfInspectionEvaluationProcess> depDatas =  map.get(key);
            int eventCount = getDepEventCount(depDatas);
            if(eventCount > thresholdCount){
                count++;
            }
        }
        // (单个部门事件数量大于策略中事件频率阀值的次数) >(策略中事件部门默认数量)
        if(count > depNum){
            return true;
        }
        return false;
    }

    private static int getDepEventCount(List<SelfInspectionEvaluationProcess> depDatas) {
        int sum = 0;
        for(SelfInspectionEvaluationProcess data : depDatas){
            int eventCount = data.getEventCount();
            sum = sum +eventCount;
        }
        return sum;
    }

    /**
     * 部门数量是否支持修改 ：为否的情况
     * @param list
     * @param thresholdCount
     * @return
     */
    private static boolean departmentModifyNoHandle(List<SelfInspectionEvaluationProcess> list, int thresholdCount) {
        Map<String, List<SelfInspectionEvaluationProcess>> map = list.stream().collect(Collectors.groupingBy(item -> item.getOrgName()));
        Set<String> keys = map.keySet();
        for(String key : keys){
            // 单个部门中事件数量 > 策略中事件频率阀值
            List<SelfInspectionEvaluationProcess> depDatas =  map.get(key);
            int eventCount = getDepEventCount(depDatas);
            if(eventCount > thresholdCount){
                return true;
            }
        }
        return false;
    }
}
