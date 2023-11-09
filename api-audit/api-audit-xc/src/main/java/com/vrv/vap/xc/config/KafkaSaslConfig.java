package com.vrv.vap.xc.config;

import com.vrv.vap.xc.model.KafkaHostInfo;
import com.vrv.vap.xc.model.ZooHostInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "kafka-sasl")
public class KafkaSaslConfig {

    private String baseHome;

    private List<KafkaHostInfo> hostInfo;

    private int kafkaPort;

    private String jaasPath;

    private List<ZooHostInfo> zookeeperInfo;

    private int zooPort;

    private boolean zookeeperMode;

    private String zookeeperHome;

    private String hiveJdbcIp;

    private String nameNode;

    public String getBaseHome() {
        return baseHome;
    }

    public void setBaseHome(String baseHome) {
        this.baseHome = baseHome;
    }

    public String getJaasPath() {
        return jaasPath;
    }

    public void setJaasPath(String jaasPath) {
        this.jaasPath = jaasPath;
    }

    public List<KafkaHostInfo> getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(List<KafkaHostInfo> hostInfo) {
        this.hostInfo = hostInfo;
    }

    public List<ZooHostInfo> getZookeeperInfo() {
        return zookeeperInfo;
    }

    public void setZookeeperInfo(List<ZooHostInfo> zookeeperInfo) {
        this.zookeeperInfo = zookeeperInfo;
    }

    public String getHiveJdbcIp() {
        return hiveJdbcIp;
    }

    public void setHiveJdbcIp(String hiveJdbcIp) {
        this.hiveJdbcIp = hiveJdbcIp;
    }

    public String getNameNode() {
        return nameNode;
    }

    public boolean isZookeeperMode() {
        return zookeeperMode;
    }

    public void setZookeeperMode(boolean zookeeperMode) {
        this.zookeeperMode = zookeeperMode;
    }

    public String getZookeeperHome() {
        return zookeeperHome;
    }

    public void setZookeeperHome(String zookeeperHome) {
        this.zookeeperHome = zookeeperHome;
    }

    public void setNameNode(String nameNode) {
        this.nameNode = nameNode;
    }

    public int getKafkaPort() {
        return kafkaPort;
    }

    public void setKafkaPort(int kafkaPort) {
        this.kafkaPort = kafkaPort;
    }

    public int getZooPort() {
        return zooPort;
    }

    public void setZooPort(int zooPort) {
        this.zooPort = zooPort;
    }

}
