package com.vrv.vap.monitor.common.model;

import com.vrv.vap.monitor.common.enums.CommandType;

import java.util.Date;

public class CommandInfo {
    /**
     * 命令类型
     */
    private CommandType commandType;
    /**
     * 命令内容
     */
    private String commandBody;
    /**
     * 发送时间
     */
    private Date time;
    /**
     * token信息
     */
    private String token;

    /**
     * ip
     */
    private String ip;
    /*
    * 全部监控任务开启关闭
    * */
    private Boolean open;

    public CommandInfo() {
    }

    @Override
    public String toString() {
        return "CommandInfo{" +
                "commandType=" + commandType +
                ", commandBody='" + commandBody + '\'' +
                ", time=" + time +
                ", token='" + token + '\'' +
                ", ip='" + ip + '\'' +
                ", open=" + open +
                '}';
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getCommandBody() {
        return commandBody;
    }

    public void setCommandBody(String commandBody) {
        this.commandBody = commandBody;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
