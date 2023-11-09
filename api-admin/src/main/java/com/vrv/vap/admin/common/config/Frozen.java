package com.vrv.vap.admin.common.config;

/**
 * @author lilang
 * @date 2020/4/26
 * @description 冻结列配置
 */
public class Frozen {

    /**
     * 是否开启
     */
    private boolean enabled;

    /**
     * 冻结列数
     */
    private Integer columns;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }
}
