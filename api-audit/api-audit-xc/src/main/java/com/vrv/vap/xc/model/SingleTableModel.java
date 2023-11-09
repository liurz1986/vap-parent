package com.vrv.vap.xc.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleTableModel implements Cloneable {

    private Log log = LogFactory.getLog(SingleTableModel.class);

    private String table;
    private String primaryKey;
    private List<String> columnList;
    private List<String> columnCamelList;
    private Map<String, String> camelToDbMap;
    private String columnString;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    @Override
    public String toString() {
        return "SingleTableModel [table=" + table + ", primaryKey=" + primaryKey + ", columnList=" + columnList + "]";
    }

    public String getColumnString() {
        return columnString;
    }

    public void setColumnString(String columnString) {
        this.columnString = columnString;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public SingleTableModel clones() {
        try {
            SingleTableModel tmp = (SingleTableModel) this.clone();
            List<String> tmpList = new ArrayList<>(this.columnList.size());
            tmpList.addAll(this.columnList);
            tmp.columnList = tmpList;
            return tmp;
        } catch (CloneNotSupportedException e) {
            log.error("", e);
        }
        return null;
    }

    public List<String> getColumnCamelList() {
        return columnCamelList;
    }

    public void setColumnCamelList(List<String> columnCamelList) {
        this.columnCamelList = columnCamelList;
    }

    public Map<String, String> getCamelToDbMap() {
        return camelToDbMap;
    }

    public void setCamelToDbMap(Map<String, String> camelToDbMap) {
        this.camelToDbMap = camelToDbMap;
    }
}
