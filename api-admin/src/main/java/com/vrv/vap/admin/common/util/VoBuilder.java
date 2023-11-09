package com.vrv.vap.admin.common.util;

import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.common.constant.RetMsgEnum;

/**
 * 返回构造器
 * 
 * @author xw
 *
 * @date 2018年4月12日
 */
public class VoBuilder {

	/**
	 * 自定义返回内容
	 * 
	 * @param rm
	 * @return
	 */
	public static Result result(RetMsgEnum rm) {
		return new Result(rm.getCode(), rm.getMsg());
	}



	/**
	 * 自定义返回内容
	 *
	 * @param result
	 * @return
	 */
	public static Result result(Result result) {
		return result;
	}

	/**
	 * 返回成功
	 * 
	 * @return
	 */
	public static Result success() {
		return result(RetMsgEnum.SUCCESS);
	}

	/**
	 * 返回失败
	 * 
	 * @return
	 */
	public static Result fail() {
		return result(RetMsgEnum.FAIL);
	}

	/**
	 * 返回服务异常
	 * 
	 * @return
	 */
	public static Result error() {
		return result(RetMsgEnum.SERVER_ERROR);
	}

}
