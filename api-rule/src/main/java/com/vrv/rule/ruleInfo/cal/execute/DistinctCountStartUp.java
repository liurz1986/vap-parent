package com.vrv.rule.ruleInfo.cal.execute;

import com.vrv.rule.ruleInfo.cal.IdistinctCountCal;
import com.vrv.rule.ruleInfo.cal.impl.DistinctCountCalImpl;
import com.vrv.rule.ruleInfo.cal.params.DistinctCountCalParams;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * distinctCount方法执行类
 * @author wudi
 * @date 2023/6/27 17:05
 */
public class DistinctCountStartUp {

    private static IdistinctCountCal distinctCountCal = new DistinctCountCalImpl();

    public static void calDistinctCount(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String expressField, Integer order,String fieldType){
        Object result = outPutrow.getField(order);
        DistinctCountCalParams distinctCountCalParams = DistinctCountCalParams.builder()
                                                        .result(result).inputFieldInfoVOs(inputFieldInfoVOs)
                                                        .inputRow(inputRow).expressField(expressField).build();
        Map<String, Object> map = new HashMap<>();
        if(fieldType.equals("double")){
            map = distinctCountCal.calDistinctCountByDouble(distinctCountCalParams);
        }else if(fieldType.equals("bigint")){
            map = distinctCountCal.calDistinctCountByLong(distinctCountCalParams);
        }
        outPutrow.setField(order,map);
    }


    /**
     * 初始化去重求和
     * @param outPutrow
     * @param order
     * @param fieldType
     */
    public static void initDistinctCount(Row outPutrow,int order,String fieldType){
        Map<String,Object> map = new HashMap<>();
        if(fieldType.equals("double")){
            map.put("count",0.00d);
        }else if(fieldType.equals("bigint")){
            map.put("count",0L);
        }
        List<String> elements = new ArrayList<>();
        map.put("elements",elements);
        outPutrow.setField(order,map);
    }



}
