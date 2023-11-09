package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/9/26
 */

@Data
@Builder
public class FlinkStartVO {
    // 对单个规则启动
    private String codes;

    private List<FilterOperator> filterOperators;

    //key是策略code；value是规则的code（规则code可以用逗号分隔）
    private Map<String,String> codeObj;

    private Integer parallelism;

    private Map<String, List<FilterOperator>> codeFilterObjects;

    private String jobName;

    // datasource 为按照数据源启动
    // category 为按照分类启动
    private String type;
}
