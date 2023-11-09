package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class EventSortModel {
    private String name;
    private int count;

    public EventSortModel() {
    }

    public EventSortModel(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
