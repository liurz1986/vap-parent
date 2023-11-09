package com.vrv.vap.admin.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vap.zjg.data-batch")
public class BatchQueueProperties {
    private Integer batchTime = 60000;
    private Integer batchCapability = 100;
    private Integer subFolderSize=100;
    private String fileFolder = "/opt/SecAudit/vrv/vap/data/netflow";
    private String fileSuffix="txt";
    private String tmpFileSuffix="tmp";
    private String logTypeDic="1,DT012;2,DT013;3,DT014;4,DT015;5,DT016;6,DT017;7,DT018;9,DT019;10,DT020;11,DT021;";
    private String devTypeDic="终端,0;服务器,1;安全保密产品,2;应用,3;网络设备,4;通用办公设备,5;";
    private String levelTypeDic="绝密,0;机密,1;秘密,2;内部,3;公开,4;";


    public Integer getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(Integer batchTime) {
        this.batchTime = batchTime;
    }

    public Integer getBatchCapability() {
        return batchCapability;
    }


    public void setBatchCapability(Integer batchCapability) {
        this.batchCapability = batchCapability;
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public void setFileFolder(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getTmpFileSuffix() {
        return tmpFileSuffix;
    }

    public void setTmpFileSuffix(String tmpFileSuffix) {
        this.tmpFileSuffix = tmpFileSuffix;
    }

    public String getLogTypeDic() {
        return logTypeDic;
    }

    public void setLogTypeDic(String logTypeDic) {
        this.logTypeDic = logTypeDic;
    }

    public String getDevTypeDic() {
        return devTypeDic;
    }

    public void setDevTypeDic(String devTypeDic) {
        this.devTypeDic = devTypeDic;
    }

    public Integer getSubFolderSize() {
        return subFolderSize;
    }

    public void setSubFolderSize(Integer subFolderSize) {
        this.subFolderSize = subFolderSize;
    }

    public String getLevelTypeDic() {
        return levelTypeDic;
    }

    public void setLevelTypeDic(String levelTypeDic) {
        this.levelTypeDic = levelTypeDic;
    }
}
