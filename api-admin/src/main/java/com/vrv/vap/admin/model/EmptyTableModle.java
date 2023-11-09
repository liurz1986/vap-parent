package com.vrv.vap.admin.model;

public class EmptyTableModle {
    private String titles;
    private int cols;
    private int rows;

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public EmptyTableModle() {
        this.setCols(20);
        this.setRows(5);
    }
}
