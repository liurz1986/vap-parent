package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class PrintModel {
    private String name;
    private int superSecret;
    private int confidential;
    private int secret;
    private int internal;
    private int open;

    public int getTotal() {
        return superSecret + confidential + secret + internal + open;
    }

    public int setTotal(int superSecret, int confidential, int secret, int internal, int open) {
        return superSecret + confidential + secret + internal + open;
    }
}
