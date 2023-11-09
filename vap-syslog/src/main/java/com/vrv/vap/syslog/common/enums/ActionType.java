package com.vrv.vap.syslog.common.enums;

/**
 * @author wh1107066
 * @date 创建时间 2019/2/13
 */
public enum ActionType {
    /**
     * 日志操作类型枚举
     */
    LOGIN("登录"),
    SELECT("查询"),
    ADD("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    EXPORT("导出"),
    IMPORT("导入"),
    DOWNLOAD("下载"),
    UPLOAD("上传"),
    VIEW("浏览"),
    LOGOUT("退出"),
    AUTO("");
    private String name;

    ActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ActionType{" +
                "name='" + name + '\'' +
                '}';
    }
}
