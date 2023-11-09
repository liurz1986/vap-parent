package com.vrv.vap.data.constant;

/**
 * 返回信息
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
public enum RetMsgEnum {
	/**
	 * 成功
	 */
	SUCCESS("成功", "0"),
	/**
	 * 系统繁忙，请稍后再试
	 */
	FAIL("系统繁忙，请稍后再试！", "9999"),
	/**
	 * 服务异常
	 */
	SERVER_ERROR("服务异常", "500003"),
	/**
	 * 未查询到结果
	 */
	EMPTY_RET("未查询到结果", "500004"),
	/**
	 * 非法查询
	 */
	ERROR_ILLEGAL("非法查询", "500005"),
	/**
	 * 参数错误
	 */
	ERROR_PARAM("参数错误", "500006"),
	/**
	 * 参数为空
	 */
	EMPTY_PARAM("参数为空", "500007"),
	/**
	 * 身份证已存在
	 */
	UNIQUE_IDCARD_ERROR("身份证已存在", "500008"),
	/**
	 * 警员号已存在
	 */
	UNIQUE_POLICECODE_ERROR("警员号已存在", "500009"),
	/**
	 * 导入失败,请检查excel数据
	 */
	IMPORT_ERROR("导入失败,请检查excel数据", "500010");

	private String msg;
	private String code;

	private RetMsgEnum(String msg, String code) {
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	private void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	private void setCode(String code) {
		this.code = code;
	}
}
