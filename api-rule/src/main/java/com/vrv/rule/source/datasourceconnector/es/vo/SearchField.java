package com.vrv.rule.source.datasourceconnector.es.vo;


import com.vrv.rule.source.datasourceconnector.es.util.ElasticSearchException;
import com.vrv.rule.util.DateUtil;
import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;






/**
 * @author wudi
 * @version 创建时间：2018年7月22日 下午5:21:21
 * @ClassName SearchField
 * @Description 分组查询
 */
@Data
public class SearchField {
	private String fieldName;
	private FieldType fieldType;
	private String timeFormat;
	private long timeSpan = -1;
	private DateHistogramInterval timeInterval;
	private SearchField childField;
	private Integer from; //
	private Integer size; //根据某个字段排序

	/**
	 * 增加一个分组字段
	 * @return
	 */
	public List<SearchField> getChildrenField() {
		List<SearchField> childrenField = new LinkedList<>();
		if (this.childField != null) {
			childrenField.add(this.childField);
		}
		return childrenField;
	}

	/**
	 * String,Object类型不能是count相关类型的子类型，否则抛出异常
	 * @param type
	 * @param child
	 */
	private  void checkChildFieldType(FieldType type, SearchField child){
		if(child!=null){
			if(type==FieldType.NumberAvg ||type==FieldType.NumberMax||type==FieldType.NumberMin||type==FieldType.NumberSum||type==FieldType.ObjectDistinctCount){
				if(child.getFieldType()==FieldType.Date || child.getFieldType()==FieldType.String||child.getFieldType()==FieldType.Object){
					throw new ElasticSearchException(ResultCodeEnum.ERROR.getCode(), "String,Object类型Field不能是count相关类型的子类型,请检查");
				}
			}
		}
	}
	/**
	 * 构造函数一
	 * @param name
	 * @param type
	 * @param child
	 */
	public SearchField(String name, FieldType type, SearchField child) {
		checkChildFieldType(type, child);
		this.setFieldName(name);
		this.setFieldType(type);
		if (type == FieldType.Date) {
			if (timeSpan <= 0) {
				timeSpan = 24 * 3600 * 1000;// 一天
			}
			if (StringUtils.isEmpty(timeFormat)) {
				timeFormat = DateUtil.DEFAULT_DATE_PATTERN;
			}
		}
		this.setChildField(child);
	}
    
	/**
	 * 构造函数-2
	 * @param name
	 * @param type
	 * @param format
	 * @param span
	 * @param child
	 */
	public SearchField(String name, FieldType type, String format, long span, SearchField child) {
		checkChildFieldType(type, child);
		this.setFieldName(name);
		this.setFieldType(type);
		this.setTimeFormat(format);
		this.setTimeSpan(span);
		this.setChildField(child);
	}
	
	public SearchField(String name, FieldType type, String format, long span, SearchField child,Integer from,Integer size) {
		checkChildFieldType(type, child);
		this.setFieldName(name);
		this.setFieldType(type);
		this.setTimeFormat(format);
		this.setTimeSpan(span);
		this.setFrom(from);
		this.setSize(size);
		this.setChildField(child);
	}

	/**
	 * 构造函数-3
	 * @param name
	 * @param type
	 * @param format
	 * @param timeInterval
	 * @param child
	 */
	public SearchField(String name, FieldType type, String format, DateHistogramInterval timeInterval,
			SearchField child) {
		checkChildFieldType(type, child);
		this.setFieldName(name);
		this.setFieldType(type);
		this.setTimeFormat(format);
		this.setTimeInterval(timeInterval);
		this.setChildField(child);
	}
	
	
	/**
	 * 构造函数 - 4
	 * @param name
	 * @param type
	 * @param format
	 * @param timeInterval
	 * @param child
	 * @param from
	 * @param size
	 */
	public SearchField(String name, FieldType type, String format, DateHistogramInterval timeInterval,
			SearchField child,Integer from,Integer size) {
		checkChildFieldType(type, child);
		this.setFieldName(name);
		this.setFieldType(type);
		this.setTimeFormat(format);
		this.setTimeInterval(timeInterval);
		this.from = from;
		this.size = size;
		this.setChildField(child);
	}
	
	/**
	 * SearchField-
	 * @param name
	 * @param type
	 * @param from
	 * @param size 
	 * @param child
	 */
	public SearchField(String name, FieldType type,Integer from,Integer size,SearchField child){
		checkChildFieldType(type, child);
		this.setFieldName(name);
		this.setFieldType(type);
		this.setFrom(from);
		this.setSize(size);
		this.setChildField(child);
	}
	
	
	
}