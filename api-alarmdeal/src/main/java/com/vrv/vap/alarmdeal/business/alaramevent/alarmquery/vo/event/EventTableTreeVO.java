package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event;

import java.util.List;

import com.vrv.vap.jpa.struct.tree.ITreeNode;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class EventTableTreeVO implements ITreeNode<EventTableTreeVO,String> {
	private String key;
	private String title;
	private String parentId;
	private List<EventTableTreeVO> children;
	private boolean isTable;
}
