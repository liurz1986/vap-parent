package com.vrv.vap.toolkit.excel;

import com.vrv.vap.toolkit.constant.ExcelEnum;

/**
 * excel 导入导出配置
 *
 * @author xw
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
    public byte[] data;

    public ExcelInfo() {
    }
    /**
     * excel bean
     */
    public ExcelInfo(String filename, String[] columns, String[] columnsCn, String sheetName, boolean skipFirstLine, String filePath, byte[] data) {
        this.filename = filename;
        this.columns = columns;
        this.columnsCn = columnsCn;
        this.sheetName = sheetName;
        this.skipFirstLine = skipFirstLine;
        this.filePath = filePath;
        this.data = data;
    }

    /**
     * 导入导出用
     *
     * @ExcelEnum excelEnum
     * @boolean skipFirstLine
     * @String filePath
     * @byte data
     */
    public ExcelInfo(ExcelEnum excelEnum, boolean skipFirstLine, String filePath, byte[] data) {
        this(excelEnum.getFilename(), excelEnum.getFields(), excelEnum.getFieldsCn(), excelEnum.getSheet(),
                skipFirstLine, filePath, data);
    }

    /**
     * 导入导出用
     *
     * @ExcelEnum excelEnum
     * @boolean skipFirstLine
     * @String filePath
     */
    public ExcelInfo(ExcelEnum excelEnum, boolean skipFirstLine, String filePath) {
        this(excelEnum, skipFirstLine, filePath, null);
    }

    /**
     * 导入时跳过第一行
     *
     * @ExcelEnum excelEnum
     * @String filePath
     */
    public ExcelInfo(ExcelEnum excelEnum, String filePath) {
        this(excelEnum, true, filePath);
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


    /**
     * 导入时跳过第一行
     *
     * @ExcelEnum excelEnum
     * @String filePath
     */
    public ExcelInfo(ExcelEnum excelEnum, String filePath, int mergeColStart, int mergeColEnd, int[] mergerFields, boolean needMerge) {
        this(excelEnum, true, filePath);
        this.mergeColStart = mergeColStart;
        this.mergeColEnd = mergeColEnd;
        this.mergerFields = mergerFields;
        this.needMerge = needMerge;
    }

    private boolean needMerge = false;

    /**
     * 合并起始列数
     */
    private int mergeColStart;

    /**
     * 合并结束列数
     */
    private int mergeColEnd;

    /**
     * 判断合并的字段(列头),如 {2,3} =>以第3列和第4列的数据相同来作为合并行的依据
     */
    private int[] mergerFields;

    public boolean isNeedMerge() {
        return needMerge;
    }

    public void setNeedMerge(boolean needMerge) {
        this.needMerge = needMerge;
    }

    public int getMergeColStart() {
        return mergeColStart;
    }

    public void setMergeColStart(int mergeColStart) {
        this.mergeColStart = mergeColStart;
    }

    public int getMergeColEnd() {
        return mergeColEnd;
    }

    public void setMergeColEnd(int mergeColEnd) {
        this.mergeColEnd = mergeColEnd;
    }

    public int[] getMergerFields() {
        return mergerFields;
    }

    public void setMergerFields(int[] mergerFields) {
        this.mergerFields = mergerFields;
    }
}
