package com.vrv.vap.alarmdeal.business.analysis.enums;

public enum WarnStatus {

    LOW("预备预警", 1), MIDDLE_LOW("待审核预警", 2), MIDDLE("正式预警", 3), MIDDLE_HIGH("已发布预警", 4), HIGH("归档预警", 5);

    // 成员变量
    private String name;

    private int index;

    private WarnStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // 普通方法
    public static String getName(String strIndex) {
        int index = 1;
        if ("".equals(strIndex) || null == strIndex || "1".equals(strIndex)) {
            return LOW.getName();
        } else {
            index = Integer.parseInt(strIndex);
        }
        for (WarnStatus c : WarnStatus.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
}
