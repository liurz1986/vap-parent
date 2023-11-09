package com.vrv.vap.admin.common.constant;

import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.Result;

public enum ErrorCode implements ResultAble {

	INVAIDERD("1000", "用户名或者密码错误"),
	USER_IS_LOGIN("1001", "用户已经登录"),
	USER_NOT_LOGIN("1002","用户未登录"),
	TOKEN_INVALIDATE("1101","无效的TOKEN"),
    TOKEN_OUT_DATE("1102","TOKEN 已经失效"),
    TOKEN_USED("1103","TOKEN 已经被使用过了"),
	QSL_NULL("1104","语句为空或不合法"),

	PARAM_NULL("1201","参数不合法"),
	CRON_NULL("1202","时间不合法"),

	REPORT_NULL("1301","无报表信息"),
	;

	private Result result;

	public Result getResult() {
		return this.result;
	}

	ErrorCode(String code, String message) {
		this.result = new Result(code, message);
	}
}
