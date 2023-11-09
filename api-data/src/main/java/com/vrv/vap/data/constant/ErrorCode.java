package com.vrv.vap.data.constant;


import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.Result;

public enum ErrorCode implements ResultAble {

    ERROR_QUERY_WRONG_TYPE(1000, " Elastic 索引进行搜索"),
    ERROR_QUERY_DIFFERENT_TIME(1001, "多索引时间字段不一致"),
    NOT_FOUND_INDEX(1002, "索引信息有误"),

    MYSQL_TIME_FIELD_EMPTY(1006, "仅支持包含时间的数据源进行搜索"),

    ERROR_ROLE_CONDITION(2001, "没有对应的查询条件"),


    SQL_TABLE_NOT_EXISTS(3001, "数据表不存在"),
    SQL_WRONG_SYNTAX(3002, "SQL安全合法性验证失败"),
    SQL_WRONG_WHERE(3001, "条件参数有误"),

    MAINTAIN_TEMPLATE_IO_ERROR(4001, "文件获取失败"),
    MAINTAIN_TEMPLATE_FORMAT_ERROR(4001, "文件读取失败"),
    MAINTAIN_TEMPLATE_ERROR(4001, "模板数据格式有误"),
    MAINTAIN_TEMPLATE_EMPTY(4002, "模板数据为空"),
    MAINTAIN_TEMPLATE_FAIL(4002, "文件解析失败"),
    MAINTAIN_TEMPLATE_CONTENT_ERROR(4003, "模板数据格式有误"),
//    SQL_MAINTAIN_TEMPLATE_ERROR(4001, "模板格式有误"),
//    SQL_MAINTAIN_TEMPLATE_ERROR(4001, "模板格式有误"),

    ;

    private Result result;

    @Override
    public Result getResult() {
        return this.result;
    }

    ErrorCode(int code, String message) {
        this.result = new Result(String.valueOf(code), message);
    }

//    ErrorCode(String code, String message) {
//        this.result = new Result(code, message);
//    }
}
