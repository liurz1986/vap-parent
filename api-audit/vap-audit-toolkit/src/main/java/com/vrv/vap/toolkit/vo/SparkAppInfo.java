package com.vrv.vap.toolkit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spark任务参数
 *
 * @author xw
 * @date 2018年6月13日
 */
@ApiModel("spark任务参数实体")
public class SparkAppInfo {

    /**
     * 未知
     */
    public static final String UNKNOW = "UNKNOW";

    /**
     * 消息分隔符
     */
    public static final String MSG_SEP = "###";

    /**
     * track host
     */
    public static final String HOST = "host";

    /**
     * track port
     */
    public static final String PORT = "port";

    /**
     * track url
     */
    public static final String URL = "url";

    /**
     * 提示
     */
    public static final String MARK = "mark";

    /**
     * cmd
     */
    public static final String CMD = "cmd";

    /**
     * 任务名称
     */
    @ApiModelProperty("应用名称,eg:网络会话Top")
    private String name;

    /**
     * 应用名称,eg:rpt.NetflowTop
     */
    @ApiModelProperty("应用名称,eg:rpt.NetflowTop")
    private String app;

    /**
     * 任务执行类
     */
    @ApiModelProperty("任务执行类")
    private String clazz;

    /**
     * 任务参数
     */
    @ApiModelProperty("任务参数")
    private List<String> paramList = new ArrayList<>();

    /**
     * 可选配置,eg:--driver-class-path /path/to/driver
     */
    @ApiModelProperty("可选配置,eg:--driver-class-path /path/to/driver")
    private List<String> optionList = new ArrayList<>();

    /**
     * 可执行的命令,该值不为空时,只接调用该命令
     */
    @ApiModelProperty("可执行的命令,该值不为空时,只接调用该命令")
    private String cmd;

    /**
     * 执行的jar包存放路径
     */
    @ApiModelProperty("执行的jar包存放路径")
    private String path;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }

    public List<String> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String buildSubmitMessage(String host, String port, String url, String mark, String cmd) {
        return new StringBuilder().append(host).append(SparkAppInfo.MSG_SEP).append(port).append(SparkAppInfo.MSG_SEP)
                .append(url).append(SparkAppInfo.MSG_SEP).append(mark).append(SparkAppInfo.MSG_SEP).append(cmd).toString();
    }

    public Map<String, Object> getSubmitMessage(Result result) {
        Map<String, Object> kv = new HashMap<>();
        kv.put("name", this.getName());
        kv.put("app", this.getApp());
        String[] msg = result.getMessage().split(SparkAppInfo.MSG_SEP);
        if (msg.length > 4) {
            kv.put(HOST, msg[0]);
            kv.put(PORT, msg[1]);
            kv.put(URL, msg[2]);
            kv.put(MARK, msg[3]);
            kv.put(CMD, msg[4]);
        } else {
            kv.put(HOST, SparkAppInfo.UNKNOW);
            kv.put(PORT, SparkAppInfo.UNKNOW);
            kv.put(URL, SparkAppInfo.UNKNOW);
            kv.put(MARK, result.getMessage());
        }
        return kv;
    }

    @Override
    public String toString() {
        return "SparkAppInfo [name=" + name + ", app=" + app + ", clazz=" + clazz + ", paramList=" + paramList
                + ", optionList=" + optionList + ", cmd=" + cmd + ", path=" + path + "]";
    }
}
