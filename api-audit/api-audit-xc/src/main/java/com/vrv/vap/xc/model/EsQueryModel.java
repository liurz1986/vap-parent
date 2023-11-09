package com.vrv.vap.xc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 此类是用来简化es搜索的实体,配合ESTools使用
 *
 * @author xw
 * @date 2016年3月2日
 */
@ApiModel("es查询实体类")
public class EsQueryModel {

    @ApiModelProperty("索引")
    private String indexName;

    @ApiModelProperty("索引别名")
    private String indexNameAlias;

    @ApiModelProperty("多个索引")
    private String[] indexNames;

    @ApiModelProperty("原始的查询字符串")
    private String query;

    @ApiModelProperty("数据类型,默认_doc")
    @Value("${typeName:_doc}")
    private String typeName;

    @ApiModelProperty("查询对象")
    @JsonIgnore
    private QueryBuilder queryBuilder;

    @ApiModelProperty("过滤对象")
    @JsonIgnore
    private QueryBuilder filterBuilder;

    @ApiModelProperty("统计对象")
    @JsonIgnore
    private AbstractAggregationBuilder<?> aggregationBuilder;

    @ApiModelProperty("统计对象(同级多个)")
    @JsonIgnore
    private List<AbstractAggregationBuilder<?>> mulAggregationBuilders;

    @ApiModelProperty("要查询的字段")
    private String[] queryFields;

    @ApiModelProperty("结果需要的字段")
    private String[] resultFields;

    @ApiModelProperty("是否直接指定es的返回字段")
    private boolean isLimitResultFields = false;

    @ApiModelProperty("是否使用过滤器")
    private boolean useFilter = true;

    @ApiModelProperty("是否使用统计")
    private boolean useAggre = true;

    @ApiModelProperty("是否排序")
    private boolean sort = false;

    @ApiModelProperty("是否设置时间范围")
    private boolean useTimeRange = true;

    @ApiModelProperty("起始条数")
    @JsonProperty(value = "start_", access = JsonProperty.Access.WRITE_ONLY)
    @Value("${start:0}")
    private int start;

    @ApiModelProperty("要查多少条,默认10")
    @JsonProperty(value = "count_", access = JsonProperty.Access.WRITE_ONLY)
    @Value("${count:10}")
    private int count;

    @ApiModelProperty("开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty("指定字段分类统计")
    private String[] sortFields;

    @ApiModelProperty("本次探索的字段对应在redis的key值")
    private String thisKey;

    @ApiModelProperty("排序方式")
    private SortOrder sortOrder;

    @ApiModelProperty("排序方式，asc/desc")
    private String orderType;

    @ApiModelProperty("start+count是否超过es最大返回值")
    private boolean isOverFlow = false;

    @ApiModelProperty("是否需要原始值 _source")
    private boolean needSource = false;

    @ApiModelProperty("es时间字段")
    @Value("${timeField:time}")
    private String timeField;

    @ApiModelProperty("是否需要时间格式化")
    private boolean needTimeFormat = false;

    @ApiModelProperty("es时间字段格式")
    private String timeFormat;

    @ApiModelProperty("分页id")
    private String scrollId;

    public boolean isNeedTimeFormat() {
        return needTimeFormat;
    }

    public void setNeedTimeFormat(boolean needTimeFormat) {
        this.needTimeFormat = needTimeFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(String[] queryFields) {
        this.queryFields = queryFields;
    }

    public String[] getResultFields() {
        return resultFields;
    }

    public void setResultFields(String[] resultFields) {
        this.resultFields = resultFields;
    }

    public AbstractAggregationBuilder<?> getAggregationBuilder() {
        return aggregationBuilder;
    }

    public void setAggregationBuilder(AbstractAggregationBuilder<?> aggregationBuilder) {
        this.aggregationBuilder = aggregationBuilder;
    }

    public List<AbstractAggregationBuilder<?>> getMulAggregationBuilders() {
        return mulAggregationBuilders;
    }

    public void setMulAggregationBuilders(List<AbstractAggregationBuilder<?>> mulAggregationBuilders) {
        this.mulAggregationBuilders = mulAggregationBuilders;
    }

    public void addMulAggregationBuilders(AbstractAggregationBuilder<?> mulAggregationBuilder) {
        if (this.mulAggregationBuilders == null) {
            this.mulAggregationBuilders = new ArrayList<>();
        }
        this.mulAggregationBuilders.add(mulAggregationBuilder);
    }

    public boolean isUseFilter() {
        return useFilter;
    }

    public void setUseFilter(boolean useFilter) {
        this.useFilter = useFilter;
    }

    public boolean isUseAggre() {
        return useAggre;
    }

    public void setUseAggre(boolean useAggre) {
        this.useAggre = useAggre;
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public boolean isUseTimeRange() {
        return useTimeRange;
    }

    public void setUseTimeRange(boolean useTimeRange) {
        this.useTimeRange = useTimeRange;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String[] getSortFields() {
        return sortFields;
    }

    public void setSortFields(String[] sortFields) {
        this.sortFields = sortFields;
    }

    public String getThisKey() {
        return thisKey;
    }

    public void setThisKey(String thisKey) {
        this.thisKey = thisKey;
    }

    public String[] getIndexNames() {
        return indexNames;
    }

    public void setIndexNames(String[] indexNames) {
        this.indexNames = indexNames;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        if (!sort) {
            sort = true;
        }
    }

    public QueryBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public void setFilterBuilder(QueryBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
    }

    public boolean isOverFlow() {
        return isOverFlow;
    }

    public void setOverFlow(boolean isOverFlow) {
        this.isOverFlow = isOverFlow;
    }

    public boolean isLimitResultFields() {
        return isLimitResultFields;
    }

    public void setLimitResultFields(boolean isLimitResultFields) {
        this.isLimitResultFields = isLimitResultFields;
    }

    public boolean isNeedSource() {
        return needSource;
    }

    public void setNeedSource(boolean needSource) {
        this.needSource = needSource;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "QueryModel [indexName=" + indexName + ", indexNames=" + Arrays.toString(indexNames) + ", query=" + query
                + ", typeName=" + typeName + ", queryBuilder=" + queryBuilder + ", filterBuilder=" + filterBuilder
                + ", aggregationBuilder=" + aggregationBuilder + ", queryFields=" + Arrays.toString(queryFields)
                + ", resultFields=" + Arrays.toString(resultFields) + ", isLimitResultFields=" + isLimitResultFields
                + ", useFilter=" + useFilter + ", useAggre=" + useAggre + ", sort=" + sort + ", useTimeRange="
                + useTimeRange + ", start=" + start + ", count=" + count + ", startTime=" + startTime + ", endTime="
                + endTime + ", sortFields=" + Arrays.toString(sortFields) + ", thisKey=" + thisKey + ", sortOrder="
                + sortOrder + ", isOverFlow=" + isOverFlow + ", needSource=" + needSource + ", timeField=" + timeField
                + "]";
    }

    public String getIndexNameAlias() {
        return indexNameAlias;
    }

    public void setIndexNameAlias(String indexNameAlias) {
        this.indexNameAlias = indexNameAlias;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }
}
