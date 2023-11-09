package com.vrv.vap.xc.model;

import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

/**
 * es查询数据交换实体
 */
@Data
public class ExchangeDTO {
    private String aggField;
    private String dateField;
    private String dateFieldKey;
    private int aggSize;
    private DateHistogramInterval interval;
    private String dateFormat;
    private int offset;
    private String keyField;
    private String valueField;
    private boolean calc;
    private String sumAddField;
    private String[] sumAggFields;
}
