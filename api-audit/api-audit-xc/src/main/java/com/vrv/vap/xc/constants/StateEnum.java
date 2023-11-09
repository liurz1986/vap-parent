package com.vrv.vap.xc.constants;

/**
 * 状态.
 *
 * @author shenwenjun
 * @date 2016-12-27
 */
public enum StateEnum {
    /**
     * 停用
     */
    STOP,
    /**
     * 启用
     */
    START,
    /**
     * 删除
     */
    DELETE;

    public static final String[] OPTIONS = new String[]{"停用", "启用", "删除"};
}
