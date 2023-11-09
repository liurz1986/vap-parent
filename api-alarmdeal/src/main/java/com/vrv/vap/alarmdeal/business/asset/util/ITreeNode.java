package com.vrv.vap.alarmdeal.business.asset.util;

import java.util.List;

public interface ITreeNode<T extends ITreeNode<?, ID>, ID> {
	public ID getKey();
	public void setKey(ID id);
	public ID getParentId();
	public void setParentId(ID parentId);
	public List<T> getChildren();
	public void setChildren(List<T> children);
}
