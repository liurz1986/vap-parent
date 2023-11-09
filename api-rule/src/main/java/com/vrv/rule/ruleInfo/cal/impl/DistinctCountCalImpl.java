package com.vrv.rule.ruleInfo.cal.impl;

import com.vrv.rule.ruleInfo.cal.IdistinctCountCal;
import com.vrv.rule.ruleInfo.cal.params.DistinctCountCalParams;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.types.Row;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * distinctCount相关实现
 * @author wudi
 * @date 2023/6/27 16:25
 */
public class DistinctCountCalImpl implements IdistinctCountCal {


    @Override
    public Map<String, Object> calDistinctCountByDouble(DistinctCountCalParams distinctCountCalParams) {
        Object result =distinctCountCalParams.getResult();
        Row inputRow = distinctCountCalParams.getInputRow();
        List<FieldInfoVO> inputFieldInfoVOs = distinctCountCalParams.getInputFieldInfoVOs();
        String expressField = distinctCountCalParams.getExpressField();
        Map<String,Object> map = new HashMap<>();
        if(result==null){
            Double count = 1.00d;
            map.put("count",count);
            String value = FieldInfoUtil.getInputFieldValueByString(inputRow, inputFieldInfoVOs, expressField);
            List<String> elements = new ArrayList<>();
            elements.add(value);
            map.put("elements",elements);
        }else{
            if(result instanceof Map<?,?>){
                map = (Map<String, Object>) result;
                List<String> elements = (List<String>)map.get("elements");
                String inputValue = FieldInfoUtil.getInputFieldValueByString(inputRow, inputFieldInfoVOs, expressField);
                if(!elements.contains(inputValue)){
                    Double count = (Double) map.get("count");
                    count+=1.00d;
                    elements.add(inputValue);
                    map.put("count",count);
                    map.put("elements",elements);
                }
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> calDistinctCountByLong(DistinctCountCalParams distinctCountCalParams) {
        Object result =distinctCountCalParams.getResult();
        Row inputRow = distinctCountCalParams.getInputRow();
        List<FieldInfoVO> inputFieldInfoVOs = distinctCountCalParams.getInputFieldInfoVOs();
        String expressField = distinctCountCalParams.getExpressField();
        Map<String,Object> map = new HashMap<>();
        if(result==null){
            Long count = 1L;
            map.put("count",count);
            String value = FieldInfoUtil.getInputFieldValueByString(inputRow, inputFieldInfoVOs, expressField);
            List<String> elements = new ArrayList<>();
            elements.add(value);
            map.put("elements",elements);
        }else{
            if(result instanceof Map<?,?>){
                map = (Map<String, Object>) result;
                List<String> elements = (List<String>)map.get("elements");
                String inputValue = FieldInfoUtil.getInputFieldValueByString(inputRow, inputFieldInfoVOs, expressField);
                if(!elements.contains(inputValue)){
                    Long count = (Long) map.get("count");
                    count+=1L;
                    elements.add(inputValue);
                    map.put("count",count);
                    map.put("elements",elements);
                }
            }
        }
        return map;


    }
}
