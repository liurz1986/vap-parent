package com.vrv.vap.toolkit.plugin;

/**
 * 查询枚举
 * Created by lizj on 2021/5/13
 */
public enum  QueryWapperEnum {
    LIKE(1),
    EQ(2),
//    BETWEEN(3),
    IN(4),
    LIKE_LEFT(5),
    LIKE_RIGHT(6),
    IGNORE(7),
    NOT_EMPTY(8),
    MORE_THAN(9),
    LESS_THAN(10),
    TIME_RANGE(11)
    ;

    private final int value;

    QueryWapperEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
