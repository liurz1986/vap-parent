package com.vrv.rule.ruleInfo.cal.execute;

import com.vrv.rule.ruleInfo.cal.ICountCal;
import com.vrv.rule.ruleInfo.cal.impl.CountCalImpl;
import org.apache.flink.types.Row;

/**
 * @author wudi
 * @date 2023/6/27 17:58
 */
public class CountStartUp {

    private static ICountCal iCountCal = new CountCalImpl();

    /**
     * 累加count
     *
     * @param outPutrow
     * @param order
     */
    public static void accCount(Row outPutrow, Integer order, Object countValue, String fieldType) {
        if(fieldType.equals("int")){
            int result = iCountCal.calCountByInt(countValue);
            outPutrow.setField(order,result);
        }else if(fieldType.equals("double")){
            double v = iCountCal.calCountByDouble(countValue);
            outPutrow.setField(order,v);
        }
    }

    public static void calCountByInit(Row outPutrow,String fieldType,Integer order){
        Object field2 = outPutrow.getField(order);
        if (field2 == null) {
            ICountCal iCountCal = new CountCalImpl();
            Object initResults = iCountCal.calCountByInit(field2, fieldType);
            outPutrow.setField(order, initResults);
        }
    }


}
