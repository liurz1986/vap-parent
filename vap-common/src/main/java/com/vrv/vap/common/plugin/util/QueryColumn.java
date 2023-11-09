package com.vrv.vap.common.plugin.util;



import java.lang.reflect.Method;

class QueryColumn {


    private String fieldName;

    private QueryTypes condition;

    private String symbol = null;

    private Method getter;


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public QueryTypes getCondition() {
        return condition;
    }

    public void setCondition(QueryTypes condition) {
        this.condition = condition;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }
}
