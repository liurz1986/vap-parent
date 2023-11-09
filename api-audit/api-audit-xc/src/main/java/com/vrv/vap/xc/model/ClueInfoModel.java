package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class ClueInfoModel {
    private int clueInfoTotal;
    private int newlyAddedClueInfo;

    public ClueInfoModel(int clueInfoTotal, int newlyAddedClueInfo) {
        this.clueInfoTotal = clueInfoTotal;
        this.newlyAddedClueInfo = newlyAddedClueInfo;
    }
}
