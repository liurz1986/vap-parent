package com.vrv.rule.ruleInfo.cal.impl;

import com.vrv.rule.ruleInfo.cal.ICountCal;

/**
 * @author wudi
 * @date 2023/6/27 15:28
 */
public class CountCalImpl implements ICountCal {

    @Override
    public int calCountByInt(Object countObject) {
        if (countObject == null) {
            return 1;
        } else {
            Integer count = Integer.valueOf(countObject.toString());
            count = count + 1;
            return count;
        }
    }

    @Override
    public double calCountByDouble(Object countObject) {
        if (countObject == null) {
            return 1.00d;
        } else {
            Double count = Double.valueOf(countObject.toString());
            count = count + 1.00d;
            return count;
        }
    }

    @Override
    public Object calCountByInit(Object countObject, String fieldType) {
        if(fieldType.equals("double") && countObject==null){
            countObject = 0.00d;
        }else if(fieldType.equals("int") && countObject==null){
            countObject  = 0;
        }
        return countObject;
    }
}
