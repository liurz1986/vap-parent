package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;

import lombok.Data;

@Entity
@Table(name = "event_category")
@Data
public class EventCategory implements Serializable {
	public static final int PREDEFINE_TYPE=1 ; //预定义

	public static final int CUSTOM_TYPE=0 ; //自定义

	private static final long serialVersionUID = 1L;
	@Id
	@Column
	@ExcelField(title = "威胁编号", order = 1)
	private String id; //主键guid
	@Column
	@ExcelField(title = "威胁名称", order = 2)
	private String title; //名称
	@Column(name="priority_level")
	private Integer priorityLevel; //威胁等级
	@Column(name="event_desc")
	private String eventDesc; //描述
	@Column
	private String code; //标识
	@Column(name="code_level")
	private String codeLevel; //标识等级
	@Column(name="created_time")
	private String createdTime; //创建时间
	@Column(name="modified_time")
	private String modifiedTime; //修改时间
	@Column(name="parent_id")
	private String parentId;
	@Column(length=10)
	private Integer status;
	@Column(length=10,name="order_num")
	private Integer orderNum;
	@Column
	private String weight;
	@Column(name="attack_flag")
	private String attackFlag;  //攻击类型字段
	
	
	
	
	@Column(name="threat_source")
	@ExcelField(title = "威胁来源", order = 3)
	private String threatSource; //威胁来源
	
	@Column(name="threat_desc")
	@ExcelField(title = "威胁描述", order = 3)
	private String threatDesc; //威胁描述
	
	@Column(name="threat_classification")
	@ExcelField(title = "威胁分类", order = 4)
	private String threatClassification; //威胁分类
	
    @Column(name="motivate_desc")
	@ExcelField(title = "动机描述", order = 5)
	private String motivateDesc; //动机描述
	
	@Column(name="motivate_assignment")
	@ExcelField(title = "动机赋值", order = 6)
	private Integer motivateAssignment; //动机赋值
	
	@Column(name="ability_desc")
	@ExcelField(title = "能力描述", order = 7)
	private String abilityDesc; //能力描述
	
	
	@Column(name="ability_assignment")
	@ExcelField(title = "能力赋值", order = 8)
	private Integer abilityAssignment; //能力赋值
	
	@Column(name="effect_target")
	@ExcelField(title = "作用目标", order = 9)
	private String effectTarget; //作用目标
	
	@Column(name="relate_vulnerability")
	@ExcelField(title = "关联漏洞", order = 10)
	private String relateVulnerability; //关联漏洞



	@Column(name = "thread_summary")
	private  String threadSummary;  //威胁摘要

	private String harm;	//危害

	private  String principle;	//原理

	@Column(name = "deal_advice")
	private  String dealAdvice;	//处置建议

	private Integer type;  //预定义|自定义   0--自定义，1--预定义


	

//	@Override
//	public int hashCode()
//	{
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(this.id);
//        char[] charArr = sb.toString().toCharArray();
//        int hash = 0;
//        for(char c : charArr) {
//            hash = hash * 131 + c;
//        }
//        return hash;
//	}
	
	

	
}
