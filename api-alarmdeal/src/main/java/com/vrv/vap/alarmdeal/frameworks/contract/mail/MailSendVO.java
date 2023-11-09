package com.vrv.vap.alarmdeal.frameworks.contract.mail;

import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
public class MailSendVO {

    private String id;
    private Map<String,Object> sendTo; //发送目的地址
    private String title; //标题
    private String content; //发送内容
    private Map<String,Object> params; //模板参数
    private String tag; //模板标题
    private List<Pair<String, File>> attachments; //附件
}
