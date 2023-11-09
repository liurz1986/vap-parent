package com.vrv.vap.admin.vo;

/**
 * @author lilang
 * @date 2020/8/26
 * @description
 */
public class ExportWidgetVO extends QueryModel {

    private String topList;

    private String[] param;

    public String getTopList() {
        return topList;
    }

    public void setTopList(String topList) {
        this.topList = topList;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }
}
