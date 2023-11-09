package com.vrv.rule.resource;

public interface ResourceRef {
    /**
     *资源匹配
     * @param fieldValue
     * @param opt 0代表等于，1代表不等于
     */
    boolean computer(Object fieldValue,Boolean opt);
}
