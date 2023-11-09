package com.vrv.rule.ruleInfo.cal.impl;

import com.vrv.rule.ruleInfo.cal.ISumCal;
import com.vrv.rule.ruleInfo.cal.params.SumCalParams;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * @author wudi
 * @date 2023/6/28 11:36
 */
public class SumCalImpl implements ISumCal {
    @Override
    public Long calSumByLong(SumCalParams sumCalParams) {
        Object outRowSumValue = sumCalParams.getOutRowSumValue();
        Row inputRow = sumCalParams.getInputRow();
        List<FieldInfoVO> inputFieldInfoVOs = sumCalParams.getInputFieldInfoVOs();
        String ouputField = sumCalParams.getOuputField();
        Long sum = 0L;
        if (outRowSumValue != null) {
            Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, ouputField);
            Long outRowValue = Long.valueOf(outRowSumValue.toString());
            sum = inputRowValue + outRowValue;
        } else {
            sum = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, ouputField);
        }
        return sum;
    }

    @Override
    public Double calSumByDouble(SumCalParams sumCalParams) {
        Object outRowSumValue = sumCalParams.getOutRowSumValue();
        Row inputRow = sumCalParams.getInputRow();
        List<FieldInfoVO> inputFieldInfoVOs = sumCalParams.getInputFieldInfoVOs();
        String ouputField = sumCalParams.getOuputField();
        Double sum = 0.00d;
        if(outRowSumValue != null){
            Double inputDoubleValue = FieldInfoUtil.getInputFieldDoubleValue(inputRow, inputFieldInfoVOs, ouputField);
            Double outputDoubleValue = Double.parseDouble(outRowSumValue.toString());
            sum = inputDoubleValue + outputDoubleValue;
        }else{
            sum = FieldInfoUtil.getInputFieldDoubleValue(inputRow, inputFieldInfoVOs, ouputField);
        }
        return sum;
    }


}
