package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.Attach;
import lombok.Data;

@Data
public class Tables {
      
	private String id;
	private String name;
	private String label;
	private List<Column> column;
	private List<Attach> attachs; //表附着属性
	private String type ; //eventtable(事件表) or filter（过滤器的引用） or innertable（转换过程中生成的）
	private String eventTableId; //事件表Id字段
	private String newEventTableId;
	private String topicName;// topicname
	private String dataType; //数据源类型
}
