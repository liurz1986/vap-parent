package com.vrv.vap.alarmdeal;



import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableFeignClients(basePackages = "com.vrv.vap")
@EnableWebMvc
@SpringBootApplication(exclude =
		{SecurityAutoConfiguration.class})
@ComponentScan({"com.vrv.vap.jpa.spring","com.vrv.vap.jpa.quartz","com.vrv.vap.alarmModel","com.vrv.vap.utils.dozer","com.vrv.vap.alarmdeal","com.vrv.vap.common","com.vrv.vap.swagger2","com.vrv.vap.es","com.vrv.vap.jpa.req","com.vrv.vap.jpa.http","org.activiti.rest","com.vrv.vap.syslog.*","com.vrv.vap.jpa.req","com.vrv.vap.jpa.http","com.vrv.vap.alarmdeal.business.kafkadeal.disruptor"})//"com.vrv.vap.syslog.*",
@EnableRedisHttpSession
@EnableJpaRepositories({"com.vrv.vap"})
@EnableDiscoveryClient
@EntityScan("com.vrv.vap")
public class ApiAlarmdealApplication {

	private static Logger logger = LoggerFactory.getLogger(ApiAlarmdealApplication.class);

	public static void main(String[] args) {
		logger.info("启动告警模块！！！");
		SpringApplication.run(ApiAlarmdealApplication.class, args);
	}

}

