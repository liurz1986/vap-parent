package com.vrv.rule.model.filter;

import java.util.List;

import lombok.Data;

@Data
public class Tables {
      
	private String id;
	private String name;
	private String label;
	private List<Column> column;
	private List<Attach> attachs; //表附着属性
	private String type ; //eventtable(事件表) or filter（过滤器的引用） or innertable（转换过程中生成的）
    private String eventTableId; //eventtable和filter字段的的id(filter or eventtable)
	private String topicName;
	private String dataType; //数据源类型 1:es 2:mysql


}
