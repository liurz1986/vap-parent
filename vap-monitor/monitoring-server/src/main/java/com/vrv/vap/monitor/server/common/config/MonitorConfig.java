package com.vrv.vap.monitor.server.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "vap.monitor")
public class MonitorConfig {
	private boolean systemMonitor;
	private databaseMonitor databaseMonitor;
	private kafkaMonitor kafkaMonitor;
	private boolean collectorMonitor;
	private receiveMonitor receiveMonitor;
	private Map<String,Map<String,Boolean>> analyseMonitor;
	private Map<String,Map<String,Boolean>> webMonitor;
	private redisMonitor redisMonitor;
	private Flume flume;
    @Data
	public static class redisMonitor {
    	private boolean moni;
		private boolean deal;
	}
	@Data
	public static class databaseMonitor {
		private boolean moni;
		private boolean deal;
	}
    @Data
	public static class kafkaMonitor {
		private boolean moni;
		private boolean deal;
	}
    @Data
	public static class receiveMonitor {
		private boolean moni;
		private boolean deal;
	}
    @Data
	public static class Flume {
		private boolean moni;
		private boolean deal;
		private List<String> cids;
	}
}
