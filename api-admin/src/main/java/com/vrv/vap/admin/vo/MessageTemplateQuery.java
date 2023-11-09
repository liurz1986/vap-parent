package com.vrv.vap.admin.vo;


import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

public class MessageTemplateQuery extends Query {
    private  String guid;
    @ApiModelProperty("编号")
    private  String num;
    @ApiModelProperty("模板内容")
    @QueryLike
    private  String content;
    @ApiModelProperty("模板名称")
    @QueryLike
    private  String name;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
