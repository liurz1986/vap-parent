package com.vrv.vap.netflow.common.enums;

import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.Result;

public enum ErrorCode implements ResultAble {

	OFFLINE_TEMPLATE_TYPE_ERROR("10001", "文件导入失败，失败原因：当前导入文件格式不对。"),
	OFFLINE_CONTENT_NOT_MATCH("10002","文件导入失败，失败原因：当前导入文件内容格式与模板不匹配。"),
	OFFLINE_CONTENT_PARSE_ERROR("10003","文件导入失败，失败原因：数据格式异常。"),
	OFFLINE_TEMPLATE_NOT_EXIST("10004","文件导入失败，失败原因：模板不存在。"),
	OFFLINE_TEMPLATE_NO_DATA_ACCESS("10005","文件导入失败，失败原因：模板未关联事件接入任务。"),
	OFFLINE_ACCESS_NOT_RUNNING("10006","文件导入成功，数据发送失败，失败原因：接收任务未启动。"),
	OFFLINE_NO_ACCESS("10007","文件导入失败，失败原因：模板关联的接入任务不存在。")
	;

	private Result result;

	public Result getResult() {
		return this.result;
	}

	ErrorCode(String code, String message) {
		this.result = new Result(code, message);
	}
}
