package com.vrv.vap.line.tools;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class StandardTool {
    public static void main(String[] args) {
        standardValue(new double[]{1,1},1);
    }

    /**
     * 正态分布计算得分
     * @param values
     * @param x
     * @return
     */
    public static float standardValue(double[] values,double x){
        Mean mean = new Mean();
        double avg = mean.evaluate(values);
        StandardDeviation sd = new StandardDeviation();
        double evaluate = sd.evaluate(values);
        if(evaluate == 0){
            return defaultValue(values[0],x);
        }
        NormalDistribution nd = new NormalDistribution(avg, evaluate);
        double u = Math.abs(avg - x);
        return new Double(1-nd.probability(avg-u, avg+u)).floatValue();
    }

    /**
     * 两数距离计算得分
     * @param a
     * @param b
     * @return
     */
    public static float defaultValue(double a,double b){
        double avg = (a+b);
        if(avg == 0){
            return 0f;
        }
        double abs = Math.abs(a - b);
        return new Double(1- (abs/avg)).floatValue();
    }


}
