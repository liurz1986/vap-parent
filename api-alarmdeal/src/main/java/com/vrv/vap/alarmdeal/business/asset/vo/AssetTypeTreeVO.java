package com.vrv.vap.alarmdeal.business.asset.vo;


import com.vrv.vap.alarmdeal.business.asset.util.ITreeNode;
import lombok.Data;

import java.util.List;
@Data
public class AssetTypeTreeVO implements ITreeNode<AssetTypeTreeVO,String> {
	private String key;
	private String title;
	private String guid;
	private int type;
	private boolean isTable;
	private String state;
	private String uniqueCode;
	private String treeCode;
	private String parentId;
	private String iconCls;
	private List<AssetTypeTreeVO> children;
}
