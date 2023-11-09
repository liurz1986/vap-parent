package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.admin.common.ConvertField;
import com.vrv.vap.admin.common.util.TimeTools;
import io.swagger.annotations.ApiModelProperty;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Arrays;
import java.util.Date;

/**
 * 此类是用来简化es搜索的实体,配合ESTools使用
 * 
 * @author xw
 * @date 2016年3月2日
 */
public class QueryModel {
	/**
	 * 索引
	 */
	@ConvertField
	private String indexName;
	/**
	 * 索引别名
	 */
	@ConvertField
	private String indexNameAlias;
	/**
	 * 索引
	 */
	@ConvertField
	private String[] indexNames;
	/**
	 * 原始的查询字符串
	 */
	@ConvertField
	private String query;
	/**
	 * 数据类型
	 */
	@ConvertField
	private String typeName;
	/**
	 * 查询对象
	 */
	private QueryBuilder queryBuilder;
	/**
	 * 过滤对象
	 */
	private QueryBuilder filterBuilder;
	/**
	 * 统计对象
	 */
	private AbstractAggregationBuilder aggregationBuilder;
	/**
	 * 要查询的字段
	 */
	@ConvertField
	private String[] queryFields;
	/**
	 * 结果需要的字段
	 */
	@ConvertField
	private String[] resultFields;
	/**
	 * 是否直接指定es的返回字段
	 */
	@ConvertField
	private boolean isLimitResultFields = false;
	/**
	 * 是否使用过滤器
	 */
	private boolean useFilter = true;
	/**
	 * 是否使用统计
	 */
	private boolean useAggre = true;

	/**
	 * 是否排序
	 */
	private boolean sort = true;

	/**
	 * 是否设置时间范围
	 */
	private boolean useTimeRange = true;
	/**
	 * 起始条数
	 */
	@ConvertField
	private int start_;
	/**
	 * 要查多少条
	 */
	@ConvertField
	private int count_;
	/**
	 * 开始时间
	 */
	@ConvertField
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	/**
	 * 结束时间
	 */
	@ConvertField
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	/**
	 * 指定字段分类统计
	 */
	@ConvertField
	private String[] sortFields;
	/**
	 * 本次探索的字段对应在redis的key值
	 */
	private String thisKey;

	/**
	 * 排序方式
	 */
	private SortOrder sortOrder;

	@ApiModelProperty("排序方式，asc/desc")
	private String orderType;


	/**
	 * start+count是否超过es最大返回值
	 */
	private boolean isOverFlow = false;

	/**
	 * 是否需要原始值 _source
	 */
	@ConvertField
	private boolean needSource = false;

	/**
	 * 聚合字段
	 */
	private String aggFieldName;

	/**
	 * es时间字段
	 * 
	 * @return
	 */
	@ConvertField
	private String timeField = "@timestamp";

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

	public int getStart_() {
		return start_;
	}

	public void setStart_(int start_) {
		this.start_ = start_;
	}

	public int getCount_() {
		return count_;
	}

	public void setCount_(int count_) {
		this.count_ = count_;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getStartTimeUtc() {
		return TimeTools.getUtcTimeString(startTime);
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getEndTimeUtc() {
		return TimeTools.getUtcTimeString(endTime);
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

	public AbstractAggregationBuilder getAggregationBuilder() {
		return aggregationBuilder;
	}

	public void setAggregationBuilder(AbstractAggregationBuilder aggregationBuilder) {
		this.aggregationBuilder = aggregationBuilder;
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

	@Override
	public String toString() {
		return "QueryModel [indexName=" + indexName + ", indexNames=" + Arrays.toString(indexNames) + ", query=" + query
				+ ", typeName=" + typeName + ", queryBuilder=" + queryBuilder + ", filterBuilder=" + filterBuilder
				+ ", aggregationBuilder=" + aggregationBuilder + ", queryFields=" + Arrays.toString(queryFields)
				+ ", resultFields=" + Arrays.toString(resultFields) + ", isLimitResultFields=" + isLimitResultFields
				+ ", useFilter=" + useFilter + ", useAggre=" + useAggre + ", sort=" + sort + ", useTimeRange="
				+ useTimeRange + ", start=" + start_ + ", count=" + count_ + ", startTime=" + startTime + ", endTime="
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

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getAggFieldName() {
		return aggFieldName;
	}

	public void setAggFieldName(String aggFieldName) {
		this.aggFieldName = aggFieldName;
	}
}
