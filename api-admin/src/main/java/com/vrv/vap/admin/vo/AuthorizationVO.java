package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 授权数据的实体
 * 
 * @author Administrator
 * 
 */
@ApiModel(value="授权数据")
public class AuthorizationVO {
	@ApiModelProperty(value="产品序列号")
	private String serializableNum; // 产品序列号
	@ApiModelProperty(value="获得已使用的时间")
	private String usedDays; // 获得已使用的时间
	@ApiModelProperty(value="获得剩余的时间")
	private String lastDays; // 获得剩余的时间
	@ApiModelProperty(value="客户的名称")
	private String companyName;// 客户的名称
	@ApiModelProperty(value="截止日期小于30天的提示（不需提供）")
	private String info; // 截止日期小于30天的提示（不需提供）
	@ApiModelProperty(value="版本编码")
	private int versionCode;// 版本编码（1248码）
	@ApiModelProperty(value=" 截止日期")
	private String deadLine;// 截止日期
	@ApiModelProperty(value=" 终端的个数")
	private int terminalCount;// 终端的个数
	@ApiModelProperty(value="授权状态")
	private Boolean status;

	public String getSerializableNum() {
		return serializableNum;
	}

	public void setSerializableNum(String serializableNum) {
		this.serializableNum = serializableNum;
	}

	public String getUsedDays() {
		return usedDays;
	}

	public void setUsedDays(String usedDays) {
		this.usedDays = usedDays;
	}

	public String getLastDays() {
		return lastDays;
	}

	public void setLastDays(String lastDays) {
		this.lastDays = lastDays;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(String deadLine) {
		this.deadLine = deadLine;
	}

	public int getTerminalCount() {
		return terminalCount;
	}

	public void setTerminalCount(int terminalCount) {
		this.terminalCount = terminalCount;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}
