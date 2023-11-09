package com.vrv.vap.alarmdeal.business.flow.monitor.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.ProcessStateEnum;
import com.vrv.vap.jpa.struct.tree.ITreeNode;

import java.util.Date;
import java.util.List;

public class MyTicketTreeVO implements ITreeNode<MyTicketTreeVO, String> {

	private String guid;
	private String key;
	private String title;
	private String ticketVersion;
	private ProcessStateEnum ticketStatus;
	private Integer orderNum;
	private String createUser;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyyMMddHHmmss")
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	private String modelId;   // 自定义流程模型guid
	private String deployId;  // 自定义流程发布id
	private Integer count;
	private Boolean used;      // 是否用过，启用过就为true；为true的不能改为false
	private String parentId;
	private List<MyTicketTreeVO> children;
	
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

	public String getTicketVersion() {
		return ticketVersion;
	}

	public void setTicketVersion(String tVersion) {
		this.ticketVersion = tVersion;
	}

	public ProcessStateEnum getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(ProcessStateEnum tStatus) {
		this.ticketStatus = tStatus;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
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
	public List<MyTicketTreeVO> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<MyTicketTreeVO> children) {
		this.children = children;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}
