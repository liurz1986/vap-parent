package com.vrv.vap.line.model;

import java.util.List;

public class SysMeter {

    private String meterName;

    List<SysMeterAttached> meterInfo;

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public List<SysMeterAttached> getMeterInfo() {
        return meterInfo;
    }

    public void setMeterInfo(List<SysMeterAttached> meterInfo) {
        this.meterInfo = meterInfo;
    }
}
