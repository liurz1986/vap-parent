package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event;

import java.util.List;

import com.vrv.vap.jpa.struct.tree.ITreeNode;

import lombok.Data;
@Data
public class EventCategoryVRVTreeVO implements ITreeNode<EventCategoryVRVTreeVO, String> {

	private String key;
	private String parentId;
	private String title;
	private String icon;
	private String codeLevel;
	private Boolean isLeaf;
	private String threadSummary; //事件分类摘要
	private  String dealAdvice;
	private  String priorityLevel; //威胁等级
	private  String type;  //定义类型
	private Integer orderNum;
	private  List<EventCategoryVRVTreeVO> children;  
	
	
}
