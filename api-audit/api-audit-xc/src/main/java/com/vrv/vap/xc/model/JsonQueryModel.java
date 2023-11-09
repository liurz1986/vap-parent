package com.vrv.vap.xc.model;

import java.util.Arrays;
import java.util.Map;

/**
 * 前台传入json反序列化
 *
 * @author xw
 * @date 2018年3月28日
 */
public class JsonQueryModel {
    private Map<String, Object> and;
    private Map<String, Object> or;
    private Map<String, Object> not;
    private String[] miss;
    private String[] notMiss;

    public Map<String, Object> getAnd() {
        return and;
    }

    public String[] getNotMiss() {
        return notMiss;
    }

    public void setNotMiss(String[] notMiss) {
        this.notMiss = notMiss;
    }

    public void setAnd(Map<String, Object> and) {
        this.and = and;
    }

    public Map<String, Object> getOr() {
        return or;
    }

    public void setOr(Map<String, Object> or) {
        this.or = or;
    }

    public Map<String, Object> getNot() {
        return not;
    }

    public void setNot(Map<String, Object> not) {
        this.not = not;
    }

    public String[] getMiss() {
        return miss;
    }

    public void setMiss(String[] miss) {
        this.miss = miss;
    }

    @Override
    public String toString() {
        return "JsonQueryBean [and=" + and + ", or=" + or + ", not=" + not + ", miss=" + Arrays.toString(miss)
                + ", notMiss=" + Arrays.toString(notMiss) + "]";
    }
}
