package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;

public class MessageTemplate {
    @Id
    @Column(name = "guid")
    @ApiModelProperty(value = "模板ID")
    private  String guid;
    @Column(name = "num")
    @ApiModelProperty(value = "模板编号")
    private  String num;
    @Column(name = "name")
    @ApiModelProperty(value = "模板名称")
    private String name;
    @Column(name = "content")
    @ApiModelProperty(value = "消息内容")
    private  String content;
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
