package com.vrv.vap.server.push.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="全站在线用户的消息提示")
public class Tip {


    @ApiModelProperty(value="消息标题", required = true)
    private String title;

    @ApiModelProperty(value="消息内容，支持HTML格式", required = true)
    private String content;

    @ApiModelProperty(value="消息详情URL")
    private String url;


    @ApiModelProperty(hidden = true)
    private Date sendtime;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getSendtime() {
        return sendtime;
    }

    public void setSendtime(Date sendtime) {
        this.sendtime = sendtime;
    }
}
