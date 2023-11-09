package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 维表表名
 * @author Administrator
 *
 */

@Entity
@Data
@Table(name = "dimension_table")
public class DimensionTableInfo {
    
	@Id
    @Column
	private String guid;
	@Column(name="name")
	private String name;   //维表中文描述
	@Column(name="name_en")
	private String nameEn; //维表英文描述
	@Column(name="description")
	private String description;
	@Column(name="create_time")
	private Date createTime; //维表创建时间


	@Column(name="table_type")
	private String tableType;   //维表类型   baseline 、 base（不可填参数） 、 other


	@Column(name="baseline_index")
	private String baselineIndex;

	@Column(name="days")
	private int days;  // 转存数据天数
	@Column(name="filter_con")
	private String filterCon;  // 条件过滤字段
}
