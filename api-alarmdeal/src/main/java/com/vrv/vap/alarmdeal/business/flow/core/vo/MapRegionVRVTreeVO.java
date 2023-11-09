package com.vrv.vap.alarmdeal.business.flow.core.vo;

import com.vrv.vap.jpa.struct.tree.ITreeNode;
import lombok.Data;

import java.util.List;

/**
 * MapRegion的树节点
 * @author wd-pc
 *
 */

@Data
public class MapRegionVRVTreeVO implements ITreeNode<MapRegionVRVTreeVO, String> {
	private String key;
	private String parentId;
	private String title;
	private String icon;
	private String codeLevel;
	private boolean isLeaf;
	private String ip;
	private  List<MapRegionVRVTreeVO> children;  
}
