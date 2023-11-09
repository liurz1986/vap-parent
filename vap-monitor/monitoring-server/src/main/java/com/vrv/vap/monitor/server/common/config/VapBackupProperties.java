package com.vrv.vap.monitor.server.common.config;

import com.vrv.vap.monitor.server.vo.BackupTableVO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "vap.backup")
public class VapBackupProperties {
    private List<BackupTableVO> nacos;
    private List<BackupTableVO> sys;
    private String restartCmd;
    private String exchangePath;

    public List<BackupTableVO> getNacos() {
        return nacos;
    }

    public void setNacos(List<BackupTableVO> nacos) {
        this.nacos = nacos;
    }

    public List<BackupTableVO> getSys() {
        return sys;
    }

    public void setSys(List<BackupTableVO> sys) {
        this.sys = sys;
    }

    public String getRestartCmd() {
        return restartCmd;
    }

    public void setRestartCmd(String restartCmd) {
        this.restartCmd = restartCmd;
    }

    public String getExchangePath() {
        return exchangePath;
    }

    public void setExchangePath(String exchangePath) {
        this.exchangePath = exchangePath;
    }
}
