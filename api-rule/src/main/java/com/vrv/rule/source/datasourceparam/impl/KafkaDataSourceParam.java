package com.vrv.rule.source.datasourceparam.impl;

import com.vrv.rule.source.datasourceparam.DataSourceParamsAbs;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * kafka数据源参数
 */
@Data
@Builder
public class KafkaDataSourceParam extends DataSourceParamsAbs implements Serializable{
    private static final long serialVersionUID = 1L;

    private String kafkaUrl;   //kafka连接地址
    private String kafkaPort; //kafka端口
    private String topicName; //主题名称
    private String kafkaAuthUserName;  //认证用户名
    private String kafkaAuthPassword; //认证密码
    private String groupId;  //用户组ID



}
