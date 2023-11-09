package com.vrv.vap.xc.model;

import org.apache.kafka.common.protocol.types.Field;

public class LineSaveModel {
    private String src;
    private String dest;
    private String type;
    private String aggType; //(计算类型：1分组 4均值 6均值+最大和最小值 5直接取值)
    private Integer level = 9999; //层级
    private String description;
    private boolean isMediate;
    private boolean isCount;
    private boolean isMain;

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getAggType() {
        return aggType;
    }

    public void setAggType(String aggType) {
        this.aggType = aggType;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LineSaveModel() {
        this.isMediate = true;
    }

    public LineSaveModel(String src, String dest, String type, String description,String aggType,Integer level) {
        this.src = src;
        this.dest = dest;
        this.type = type;
        this.description = description;
        this.isMediate = true;
        this.aggType = aggType;
        this.level = level;
    }

    public LineSaveModel(String src, String dest, String type, String description) {
        this.src = src;
        this.dest = dest;
        this.type = type;
        this.description = description;
        this.isMediate = true;
    }


    public LineSaveModel(String src, String dest, String type, String description,Boolean isMediate) {
        this.src = src;
        this.dest = dest;
        this.type = type;
        this.description = description;
        this.isMediate = isMediate;
    }

    public LineSaveModel(String src, String dest, String type, String description, Boolean isMediate, Boolean isCount) {
        this.src = src;
        this.dest = dest;
        this.type = type;
        this.description = description;
        this.isMediate = isMediate;
        this.isCount = isCount;
    }

    public boolean isMediate() {
        return isMediate;
    }

    public void setMediate(boolean mediate) {
        isMediate = mediate;
    }

    public boolean isCount() {
        return isCount;
    }

    public void setCount(boolean count) {
        isCount = count;
    }
}
