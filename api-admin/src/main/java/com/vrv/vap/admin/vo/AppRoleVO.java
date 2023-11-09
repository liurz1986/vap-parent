package com.vrv.vap.admin.vo;

import java.util.List;

import com.vrv.vap.admin.model.AppRole;

public class AppRoleVO {
    
	private int roleId;
	
	private List<AppRole> appRoles;

	public List<AppRole> getAppRoles() {
		return appRoles;
	}

	public void setAppRoles(List<AppRole> appRoles) {
		this.appRoles = appRoles;
	}
	
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

}
