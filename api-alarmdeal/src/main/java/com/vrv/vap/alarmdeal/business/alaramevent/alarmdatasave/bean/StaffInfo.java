package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人员信息
 */
@Data
public class StaffInfo {
    // 姓名
    @EsField("staff_name")
    @ApiModelProperty(value = "姓名")
    String staffName;
    // 账号
    @EsField("staff_account")
    @ApiModelProperty(value = "账号")
    String staffAccount;

    @EsField("staff_no")
    @ApiModelProperty(value = "用户编号")
    String staffNo;

    // 岗位
    @EsField("staff_post")
    @ApiModelProperty(value = "岗位")
    String staffPost;
    // 部门
    @EsField("staff_department")
    @ApiModelProperty(value = "部门")
    String staffDepartment;
    // 角色
    @EsField("staff_role")
    @ApiModelProperty(value = "角色")
    String staffRole;
    // 密级
    @EsField("staff_level")
    @ApiModelProperty(value = "密级")
    String staffLevel;

    @ApiModelProperty(value = "是否关联")
    Integer isRelation;

    @ApiModelProperty(value = "人员公司")
    String staffCompany;
    @ApiModelProperty(value = "人员类型")
    String staffType;

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        //sb.append(this.staffAccount);
        sb.append(this.staffNo);
        char[] charArr = sb.toString().toCharArray();
        int hash = 0;
        for (char c : charArr) {
            hash = hash * 131 + c;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        // 对于任何非null的引用值x，x.equals(null)必须返回false
        if (obj == null) {
            return false;
        }
        // TODO 核心域比较
        return this.hashCode() == obj.hashCode();
    }


}