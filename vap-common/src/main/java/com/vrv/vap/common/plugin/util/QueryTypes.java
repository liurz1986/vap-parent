package com.vrv.vap.common.plugin.util;

enum QueryTypes {
    EQ,             // 默认 等于
    IN,             // in
    LIKE,           // 模糊匹配
    LIKE_LEFT,      // 模糊左匹配
    LIKE_RIGHT,     // 模糊右匹配
    MORE_THAN,      // 大于
    LESS_THAN,      // 小于
    BETWEEN,        // 范围匹配
    NOT_EMPTY,        // 不为空
}
