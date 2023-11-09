package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.Message;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="消息推送实体")
public class MessageVo extends Message {

    @ApiModelProperty(value="发送邮件地址,不填写则使用用户邮箱")
    private String sendEmailTo;

    @ApiModelProperty(value="发送类型，填写默认发送全部，1 只发送报警 2只发送邮件")
    private String sendType;

    public String getSendEmailTo() {
        return sendEmailTo;
    }

    public void setSendEmailTo(String sendEmailTo) {
        this.sendEmailTo = sendEmailTo;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }
}
