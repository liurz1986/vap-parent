package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 职员映射bean
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffInfoSupervise {
	/**
	 * 姓名
	 */
	@SerializedName(value = "staff_name",alternate = {"staffName"})
	private String staff_name;
	/**
	 * 账号
	 */
	@SerializedName(value = "staff_account",alternate = {"staffAccount"})
	private String staff_account;
	/**
	 * 岗位
	 */
	@SerializedName(value = "staff_post",alternate = {"staffPost"})
	private String staff_post;
	/**
	 * 部门
	 */
	@SerializedName(value = "staff_department",alternate = {"staffDepartment"})
	private String staff_department;
	/**
	 * 角色
	 */
	@SerializedName(value = "staff_role",alternate = {"staffRole"})
	private String staff_role;
	/**
	 * 密级
	 */
	@SerializedName(value = "staff_level",alternate = {"staffLevel"})
	private String staff_level;
}
