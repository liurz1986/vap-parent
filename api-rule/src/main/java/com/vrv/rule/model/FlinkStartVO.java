package com.vrv.rule.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/9/26
 */

@Data
public class FlinkStartVO {

    private String codes;

    private Integer parallelism;

    private Map<String,String> codeObj;  //key是策略code；value是规则的code（规则code可以用逗号分隔）

    private Map<String, List<FilterOperator>>  codeFilterObjects;    //key是策略ruleCode；value是规则集合

    private List<FilterOperator> filterOperators;

    private String type; //类型 category or datasource

    private String jobName;
}
