package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;

@ApiModel(value="角色或人员信息")
public class RolePersonQuery extends Query {
    private String roleOrPerson;

    public String getRoleOrPerson() {
        return roleOrPerson;
    }

    public void setRoleOrPerson(String roleOrPerson) {
        this.roleOrPerson = roleOrPerson;
    }
}
