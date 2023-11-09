package com.vrv.rule.ruleInfo.cal.execute;

import com.vrv.rule.ruleInfo.cal.ISumCal;
import com.vrv.rule.ruleInfo.cal.impl.SumCalImpl;
import com.vrv.rule.ruleInfo.cal.params.SumCalParams;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * @author wudi
 * @date 2023/6/28 14:01
 */
public class SumStartUp {

    private static ISumCal iSumCal = new SumCalImpl();


    public static void accSum(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String ouputField,
                              Integer outRowOrder, Object outRowSumValue, String fieldType) {

        SumCalParams sumCalParams = SumCalParams.builder()
                .inputRow(inputRow)
                .inputFieldInfoVOs(inputFieldInfoVOs)
                .outRowSumValue(outRowSumValue)
                .ouputField(ouputField).build();
        if (fieldType.equals("double")) {
            Double sumvalue = iSumCal.calSumByDouble(sumCalParams);
            outPutrow.setField(outRowOrder, sumvalue);
        } else if (fieldType.equals("bigint")) {
            Long longsum = iSumCal.calSumByLong(sumCalParams);
            outPutrow.setField(outRowOrder, longsum);
        }



    }


    public static void calSumByInit(Row outPutrow,Integer outRowOrder, String fieldType) {
        Object field2 = outPutrow.getField(outRowOrder);
        if (field2 == null) {
            if(fieldType.equals("double")){
                outPutrow.setField(outRowOrder, 0.00d);
            }else if(fieldType.equals("bigint")) {
                outPutrow.setField(outRowOrder, 0L);
            }
        }
    }

}
