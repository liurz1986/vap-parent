package com.vrv.rule.source.datasourceparam.impl;

import com.vrv.rule.source.datasourceparam.DataSourceParamsAbs;
import com.vrv.rule.source.datasourceconnector.es.util.QueryCondition_ES;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ES数据源参数
 */
@Builder
@Data
public class EsDatasourceParam extends DataSourceParamsAbs implements Serializable {

    private static final long serialVersionUID = 1L;
    private String hostArrays; //es数据源地址
    private String userName;  //用户名称
    private String password;  //用户密码
    private String indexName; //索引名称
    private Long time;   //游标时间长度
    private  Integer size; //游标大小
    private String key; //排序字段
    private String sort; //字段类型 asc or desc
    private List<QueryCondition_ES> conditions;


}
