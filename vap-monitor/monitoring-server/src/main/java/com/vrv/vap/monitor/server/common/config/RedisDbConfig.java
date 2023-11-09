package com.vrv.vap.monitor.server.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "redisdbinfo")
public class RedisDbConfig {
	private String  host;
	private String  port;
	private String  password;
	private String  mysqlPwd;
}
