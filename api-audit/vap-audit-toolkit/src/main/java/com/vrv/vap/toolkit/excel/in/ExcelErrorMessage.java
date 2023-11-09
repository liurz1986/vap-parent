package com.vrv.vap.toolkit.excel.in;

public class ExcelErrorMessage {

    private int sheetIndex;
    private String sheetName;
    private int row;
    private int col;
    private String colLetter;
    private String name;
    private String message;

    public ExcelErrorMessage(int sheetIndex, String sheetName, int row, int col, String colLetter, String name, String message) {
        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;
        this.row = row;
        this.col = col;
        this.colLetter = colLetter;
        this.name = name;
        this.message = message;
    }

    public ExcelErrorMessage(int sheetIndex, String sheetName, int row, int col, String colLetter, String name) {
        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;
        this.row = row;
        this.col = col;
        this.colLetter = colLetter;
        this.name = name;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getColLetter() {
        return colLetter;
    }

    public void setColLetter(String colLetter) {
        this.colLetter = colLetter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
