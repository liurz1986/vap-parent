package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.License;

/**
 * @author lilang
 * @date 2021/10/28
 * @description
 */
public class LicenseVO extends License {

    private int terminalCount;

    public int getTerminalCount() {
        return terminalCount;
    }

    public void setTerminalCount(int terminalCount) {
        this.terminalCount = terminalCount;
    }
}
