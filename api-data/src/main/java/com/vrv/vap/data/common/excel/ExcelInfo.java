package com.vrv.vap.data.common.excel;

/**
 * excel 导入导出配置
 * 
 * @author xw
 *
 * @date 2018年4月2日
 */
public class ExcelInfo {
	/**
	 * 文件名
	 */
	private String filename;

	/**
	 * 英文字段名
	 */
	private String[] columns;

	/**
	 * 中文字段名
	 */
	private String[] columnsCn;

	/**
	 * sheet名
	 */
	private String sheetName;

	/**
	 * 是否跳过第一行
	 */
	private boolean skipFirstLine = true;

	/**
	 * 导入时的文件路径
	 */
	private String filePath;

	/**
	 * 导出的数据
	 */
	private byte[] data;

	public ExcelInfo() {
	}

	public ExcelInfo(String filename, String[] columns, String[] columnsCn, String sheetName, boolean skipFirstLine,
                     String filePath, byte[] data) {
		this.filename = filename;
		this.columns = columns;
		this.columnsCn = columnsCn;
		this.sheetName = sheetName;
		this.skipFirstLine = skipFirstLine;
		this.filePath = filePath;
		this.data = data;
	}


	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String[] getColumnsCn() {
		return columnsCn;
	}

	public void setColumnsCn(String[] columnsCn) {
		this.columnsCn = columnsCn;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public boolean isSkipFirstLine() {
		return skipFirstLine;
	}

	public void setSkipFirstLine(boolean skipFirstLine) {
		this.skipFirstLine = skipFirstLine;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
