package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums;

public enum StrategyEnum{

    TIMESTRATEGY("timeSpan",1), //时间策略
    STATUSSTRATEGY("statusEnum",2), //状态策略
    NGONESTRATEGY("normalEnum",3); //一般策略


    // 成员变量
    private String name;
    private int index;

    StrategyEnum(String name, int index)
    {
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static StrategyEnum getStrategyEnumByName(String name) {
        if(name.isEmpty()) {
            return null;
        }
        StrategyEnum[] values = StrategyEnum.values();
        for(StrategyEnum strategyEnum : values) {
            if(name.equals(strategyEnum.name)) {
                return strategyEnum;
            }
        }
        return null;
    }


}
