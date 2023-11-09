package com.vrv.vap.alarmdeal.business.flow.processdef.exception;

public class FlowException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer resultCode;
	
	
	public FlowException(){
		 super();
	}
	
	public FlowException(Integer code,String message) {
		super(message);
		this.resultCode = code;
	}

	public Integer getResultCode() {
		return resultCode;
	}

	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}
	
	
	
	
	
	
}
