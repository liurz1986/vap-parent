package com.vrv.vap.alarmdeal.business.flow.core.model;

public enum BusinessInstanceStatEnum {
	dealing("处理中"),
	end("完成"),          // 成功完成
	endFalse("失败完成"),  // 失败完成，表示不同意，或者驳回等情况
	endCanceled("取消"),   // 取消完成。表示流程被取消了
	endError("错误完成"),// 发生错误导致流程完成
	expatriating("外派中"),
	pending("挂起");
	
	private String text;

	private BusinessInstanceStatEnum(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public boolean toBoolean() {
		if(this == BusinessInstanceStatEnum.end) {
			return true;
		}
		if(this==BusinessInstanceStatEnum.endError){
			return true;
		}
		if(this==BusinessInstanceStatEnum.endCanceled){
			return true;
		}
		if(this==BusinessInstanceStatEnum.endFalse){
			return true;
		}
		return false;
	}
}
