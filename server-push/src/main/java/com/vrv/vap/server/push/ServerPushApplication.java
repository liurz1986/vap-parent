package com.vrv.vap.server.push;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages={"com.vrv.vap"})
@EnableEncryptableProperties
public class ServerPushApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerPushApplication.class, args);
	}
}
