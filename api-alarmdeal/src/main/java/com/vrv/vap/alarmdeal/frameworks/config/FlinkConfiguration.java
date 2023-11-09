package com.vrv.vap.alarmdeal.frameworks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月12日 下午3:25:24 
* 类说明 
*/
@Configuration
@Data
@ConfigurationProperties(prefix="flink")
public class FlinkConfiguration {
     private String flink_home_path;   //flink安装路径
     private String flink_jar_path;  //api-rule-1.0.jar包
     private boolean remote_flag; //是否开启远程
     private String remote_ip; //远程IP
     private String remote_user; //远程用户
}
