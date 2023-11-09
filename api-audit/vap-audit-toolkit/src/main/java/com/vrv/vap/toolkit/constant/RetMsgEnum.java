package com.vrv.vap.toolkit.constant;

/**
 * 返回信息
 *
 * @author xw
 * @date 2018年4月3日
 */
public enum RetMsgEnum {
    /**
     * 成功
     */
    SUCCESS("成功", "0"),

    /**
     * 重点关注成功
     */
    SUCCESS2("成功", "200"),
    /**
     * 系统繁忙，请稍后再试
     */
    FAIL("系统繁忙，请稍后再试！", "9999"),
    /**
     * 服务异常
     */
    SERVER_ERROR("服务异常", "500003"),
    /**
     * 未查询到结果
     */
    EMPTY_RET("未查询到结果", "500004"),
    /**
     * 非法查询
     */
    ERROR_ILLEGAL("非法查询", "500005"),
    /**
     * 参数错误
     */
    ERROR_PARAM("参数错误", "500006"),
    /**
     * 参数为空
     */
    EMPTY_PARAM("参数为空", "500007"),
    /**
     * 身份证已存在
     */
    UNIQUE_IDCARD_ERROR("身份证已存在", "500008"),
    /**
     * 警员号已存在
     */
    UNIQUE_POLICECODE_ERROR("警员号已存在", "500009"),
    /**
     * 导入失败,请检查excel数据
     */
    IMPORT_ERROR("导入失败,请检查excel数据", "500010"),
    /**
     * 导入解压zip失败,请检查zip密码是否正确
     */
    IMPORT_ZIP_ERROR("导入解压zip失败,请检查zip密码是否正确", "500011"),
    /**
     * 未获取到session，请重新登录
     */
    ERROR_NO_SESSION("未获取到session，请重新登录", "500012"),

    /**
     * 备份文件与记录不匹配
     */
    ERROR_NO_MATCH("备份文件与记录不匹配", "500013");

    private String msg;
    private String code;

    RetMsgEnum(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
