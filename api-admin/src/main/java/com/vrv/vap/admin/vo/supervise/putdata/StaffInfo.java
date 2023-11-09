package com.vrv.vap.admin.vo.supervise.putdata;

import com.vrv.vap.admin.config.PutField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人员信息
 */
@Data
public class StaffInfo {
	// 姓名
	@PutField("staff_name")
	@ApiModelProperty(value = "姓名")
	String staffName;
	// 账号
	@PutField("staff_account")
	@ApiModelProperty(value = "账号")
	String staffAccount;
	
	@PutField("staff_no")
	@ApiModelProperty(value = "用户编号")
	String staffNo;
	
	// 岗位
	@PutField("staff_post")
	@ApiModelProperty(value = "岗位")
	String staffPost;
	// 部门
	@PutField("staff_department")
	@ApiModelProperty(value = "部门")
	String staffDepartment;
	// 角色
	@PutField("staff_role")
	@ApiModelProperty(value = "角色")
	String staffRole;
	// 密级
	@PutField("staff_level")
	@ApiModelProperty(value = "密级")
	String staffLevel;

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.staffAccount);
        sb.append(this.staffNo);
        char[] charArr = sb.toString().toCharArray();
        int hash = 0;
        for(char c : charArr) {
            hash = hash * 131 + c;
        }
        return hash;
    }
	
}