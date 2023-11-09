package com.vrv.vap.alarmdeal.frameworks.exception;

public class AlarmDealException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer resultCode;
	
	
	public AlarmDealException(){
		 super();
	}
	
	public AlarmDealException(Integer code, String message) {
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
