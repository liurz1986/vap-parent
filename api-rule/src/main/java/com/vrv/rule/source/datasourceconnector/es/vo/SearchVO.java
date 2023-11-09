package com.vrv.rule.source.datasourceconnector.es.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * ES查询参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO {

    private String[] index;  //索引
    private QueryBuilder queryBuilder; //查询条件
    private SortBuilder sortBuilder;   //排序条件
    private Integer size;  //单次查询条数
    private Long time; //超时时间长度



}
