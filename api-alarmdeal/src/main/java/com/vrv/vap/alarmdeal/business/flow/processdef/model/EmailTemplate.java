package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 邮件内容模板
 */
@Entity
@Data
@Table(name="flow_email_template")
public class EmailTemplate {
    @Id
    private String key; // 邮件模板唯一标识

    private String name; //邮件模板名称

    private String title; // 邮件模板标题

    private String content; // 邮件模板内容


}
