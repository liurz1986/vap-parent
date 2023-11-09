package com.vrv.rule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.vrv.rule.util.SpringUtil;

@SpringBootApplication
public class ApiRuleApplication {

	public static void main(String[] args) {
		  ApplicationContext app = SpringApplication.run(ApiRuleApplication.class, args);
		  SpringUtil.setApplicationContext(app);
	}
}
