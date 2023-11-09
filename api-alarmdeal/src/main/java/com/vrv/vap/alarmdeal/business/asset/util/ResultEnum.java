package com.vrv.vap.alarmdeal.business.asset.util;

import com.vrv.vap.jpa.web.IResultCode;

/**
 * 统一的返回成功和未知错误定义
 * 其他模块可以自定义自己的枚举范围，只需要实现IResultCode即可。各模块的code可以重复
 * @author find
 *
 */
public enum ResultEnum implements IResultCode {

	ASSET_SNO_NAME_REPEAT(5001,"品牌型号名称重复"),
	ASSET_GROUPNAME_REPEAT(5002,"一级类型名称重复"),
	ASSET_UNIQUECODE_EMPTY(5003,"唯一编码不能为空"),
	ASSET_UNIQUECODE_HORIZONTAL_LINE(5004,"唯一编码不能包含横线"),
	ASSET_SNO_TREECODE_REPEAT(5005,"品牌型号树结构编码重复"),
	ASSET_SNO_UNIQUECODE_REPEAT(5006,"品牌型号唯一编码重复"),
	ASSET_SNO_ENGLISH_NAME_REPEAT(5007,"品牌型号英文名称重复"),
	
	
	LOGIN_USER_EMPTY(5009,"获取用户登陆信息失败！"),
	
	TREECODE_EMPTY(5010,"树结构编码不能为空"),
	
	
	;

	private Integer code;
	private String msg;
	
	ResultEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return msg;
	}
	
}
