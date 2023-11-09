package com.vrv.vap.alarmdeal.frameworks.contract.mail;

import lombok.Data;
/**
 * 返回数据
 * @author wd-pc
 *
 * @param <T>
 */
@Data
public class ResultData<T> {
      
	/**
	 * 200-成功
	 */
	public final static String CODE_SUCCESS = "200";

	/**
	 *	301-代表永久性转移
     */
	public final static String CODE_PERMANENTLY_MOVED = "301";

	/**
	 * 500-业务逻辑错误
	 */
	public final static String CODE_ERROR_SERVICE = "500";

	/**
	 * 501-功能不完善，无对应方法
	 */
	public final static String CODE_ERROR_FUNCTION = "501";

	/**
	 * 502-网络异常
	 */
	public final static String CODE_ERROR_WEB = "502";
	/**
	 * 503-未知其它
	 */
	public final static String CODE_ERROR_OTHER = "503";
	
	public final static String RETURN_SUCCESS = "SUCCESS"; //成功
	
	
	private String code; //返回编号
	private String message; //返回信息
	private T data;
	
	
	public ResultData(){
		
	}
	
	public ResultData(String code,String message){
		this.code = code;
		this.message = message;
	}
	
	public ResultData(String code,String message,T data){
		this.code = code;
		this.message = message;
		this.data =data;
	} 
	
	
	
}
