package com.vrv.vap.data.vo;

import io.swagger.annotations.ApiModel;

/**
 * 抽象类，实际的查询条件为
 * WhereGroup : WhereGroup 相当于括号   ( ) ,
 * WhereItem  : 相当于括号里面的内容 如 name = '曹操'
 * */
@ApiModel(value = "SQL 查询条件")
public abstract class WhereCondition {

    /**
     * RANGE 支持 AND / OR / NOT
     * ITEM  支持 < > <= >= <> in not_in not_null between(逗号)
     */
    private String operation;

    public WhereCondition(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public abstract String toString();

}
