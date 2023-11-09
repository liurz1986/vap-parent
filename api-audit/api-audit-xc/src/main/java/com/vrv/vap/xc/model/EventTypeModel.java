package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class EventTypeModel {
    /**
     * 0:待处置
     */
    private int untreated;
    /**
     * 2:已处置
     */
    private int processed;

    private String name;

    public EventTypeModel() {
    }

    public EventTypeModel(int untreated, int processed, String name) {
        this.untreated = untreated;
        this.processed = processed;
        this.name = name;
    }

    public int getTotal(){
        return untreated  + processed;
    }

    public int setTotal(int untreated, int processed){
        return untreated + processed;
    }
}
