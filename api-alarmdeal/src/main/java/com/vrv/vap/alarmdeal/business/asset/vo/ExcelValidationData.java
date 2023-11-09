package com.vrv.vap.alarmdeal.business.asset.vo;

import org.apache.poi.hssf.usermodel.DVConstraint;

public class ExcelValidationData {

	int firstRow=0;
	int lastRow =0;
	int firstCol=0;
	int lastCol=0;

	String errorTitle;
 
	String errorMsg;
	
	String promptTitle;
	 
	String promptContent;
	
	DVConstraint dvConstraint ;
	
	public String getPromptTitle() {
		return promptTitle;
	}
	public void setPromptTitle(String promptTitle) {
		this.promptTitle = promptTitle;
	}
	public String getPromptContent() {
		return promptContent;
	}
	public void setPromptContent(String promptContent) {
		this.promptContent = promptContent;
	}

	public String getErrorTitle() {
		return errorTitle;
	}
	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
	public int getFirstRow() {
		return firstRow;
	}
	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}
	public int getLastRow() {
		return lastRow;
	}
	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}
	public int getFirstCol() {
		return firstCol;
	}
	public void setFirstCol(int firstCol) {
		this.firstCol = firstCol;
	}
	public int getLastCol() {
		return lastCol;
	}
	public DVConstraint getDvConstraint() {
		return dvConstraint;
	}
	public void setDvConstraint(DVConstraint dvConstraint) {
		this.dvConstraint = dvConstraint;
	}
	public void setLastCol(int lastCol) {
		this.lastCol = lastCol;
	}

	
	public ExcelValidationData(int colIndex,String[] explicitListValues)
	{
		this.firstRow=1;
		this.lastRow=1024000;
		this.firstCol=colIndex;
		this.lastCol=colIndex; 
		this.dvConstraint=DVConstraint.createExplicitListConstraint(explicitListValues);
	}
	
	
	public ExcelValidationData(int colIndex,String[] explicitListValues,String promptTitle,String promptContent)
	{
		this.firstRow=1;
		this.lastRow=1024000;
		this.firstCol=colIndex;
		this.lastCol=colIndex;
		this.dvConstraint=DVConstraint.createExplicitListConstraint(explicitListValues);
		 
		this.promptTitle=promptTitle;
		this.promptContent=promptContent;
	}
	/**
	 * formula
	 * @param colIndex
	 * @param formula
	 * @param errorTitle
	 * @param errorMsg
	 * @param promptTitle
	 * @param promptContent
	 */
	public ExcelValidationData(int colIndex,String formula,String errorTitle,String errorMsg,String promptTitle,String promptContent)
	{
		this.firstRow=1;
		this.lastRow=1024000;
		this.firstCol=colIndex;
		this.lastCol=colIndex;
		this.dvConstraint=DVConstraint.createCustomFormulaConstraint(formula);
		this.errorTitle=errorTitle;
		this.errorMsg=errorMsg;
		this.promptTitle=promptTitle;
		this.promptContent=promptContent;
	}
	
	/**
	 * TEXT_LENGTH
	 * @param colIndex
	 * @param textMinLenth
	 * @param textMaxLenth
	 * @param errorTitle
	 * @param errorMsg
	 * @param promptTitle
	 * @param promptContent
	 */
	public ExcelValidationData(int colIndex,int  textMinLenth,int  textMaxLenth,String errorTitle,String errorMsg,String promptTitle,String promptContent)
	{
		this.firstRow=1;
		this.lastRow=1024000;
		this.firstCol=colIndex;
		this.lastCol=colIndex;
		this.dvConstraint= DVConstraint.createNumericConstraint(  
		            DVConstraint.ValidationType.TEXT_LENGTH,  
		            DVConstraint.OperatorType.BETWEEN,Integer.toString(textMinLenth) , Integer.toString(textMaxLenth));  
 
		this.errorTitle=errorTitle;
		this.errorMsg=errorMsg;
		this.promptTitle=promptTitle;
		this.promptContent=promptContent;
	}
}
