package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import com.vrv.vap.jpa.struct.tree.ITreeNode;

import java.util.ArrayList;
import java.util.List;

public class FlowInnerFormTreeVO implements ITreeNode<FlowInnerFormTreeVO, String> {

	private String key;// 对应id
	private String parentId;// 对应parentId
	private String title;// 对应text
	private String img;// 对应iconCls
	private String value;
	// 是否含有子节点
	private boolean hasChildren;
	// 是否加载完毕
	private boolean complete;
	private List<FlowInnerFormTreeVO> childNodes;
	private String processDesc;
	private String formInfosGuid;
	private String formType;  // inner, template
	private String formVersion; //表单版本
	

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public List<FlowInnerFormTreeVO> getChildNodes() {
		return childNodes == null ? new ArrayList<>() : childNodes;
	}

	public void setChildNodes(List<FlowInnerFormTreeVO> childNodes) {
		this.childNodes = childNodes;
	}

	public String getProcessDesc() {
		return processDesc;
	}

	public void setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
	}

	public String getFormInfosGuid() {
		return formInfosGuid;
	}

	public void setFormInfosGuid(String formInfosGuid) {
		this.formInfosGuid = formInfosGuid;
	}

	

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public List<FlowInnerFormTreeVO> getChildren() {
		return childNodes;
	}

	@Override
	public void setChildren(List<FlowInnerFormTreeVO> children) {
		this.childNodes = children;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getFormVersion() {
		return formVersion;
	}

	public void setFormVersion(String formVersion) {
		this.formVersion = formVersion;
	}
}
