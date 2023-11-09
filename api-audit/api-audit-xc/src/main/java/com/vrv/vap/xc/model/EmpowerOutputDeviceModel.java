package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class EmpowerOutputDeviceModel {
    private String name;
    private int printNumber;
    private int burnNumber;

    public EmpowerOutputDeviceModel() {
    }

    public EmpowerOutputDeviceModel(String name, int printNumber, int burnNumber) {
        this.name = name;
        this.printNumber = printNumber;
        this.burnNumber = burnNumber;
    }

    public int getTotal() {
        return printNumber + burnNumber;
    }

    public int setTotal(int printNumber, int burnNumber) {
        return printNumber + burnNumber;
    }
}
