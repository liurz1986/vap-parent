package com.vrv.vap.netflow.common.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义流量数据的日志类型字典，在 nacos 中进行配置，通过Configuration进行字段绑定
 * @author wh1107066
 */
@ConfigurationProperties(prefix = "vap.zjg.data-batch")
@Configuration
public class BatchQueueProperties {
    private Integer subFolderSize=100;
    private String fileFolder = "/opt/SecAudit/vrv/vap/data/netflow";
    private String fileSuffix="txt";
    private String tmpFileSuffix="tmp";
    private String logTypeDic="1,DT012;2,DT013;3,DT014;4,DT015;5,DT016;6,DT017;7,DT018;9,DT019;10,DT020;11,DT021;";
    private String devTypeDic="用户终端,0;服务器,1;安全保密产品,2;应用,3;网络设备,4;通用办公设备,5;运维终端,6";
    private String levelTypeDic="绝密,0;机密,1;秘密,2;内部,3;公开,4;";
    private Integer maxQueueSize = 100000;
    private String ignoreFilterIps="114.114.114.114";

    public String getIgnoreFilterIps() {
        return ignoreFilterIps;
    }

    public void setIgnoreFilterIps(String ignoreFilterIps) {
        this.ignoreFilterIps = ignoreFilterIps;
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

    public Map<String,String> generateTypeDic(String dicStr){
        Map<String,String> dic = new HashMap<>();
        if(StringUtils.isNotEmpty(dicStr)){
            String[] dicArr = dicStr.split(";");
            Arrays.stream(dicArr).forEach(p->{
                if(StringUtils.isNotEmpty(p)){
                    String[] values = p.split(",");
                    if(values.length==2){
                        dic.put(values[0],values[1]);
                    }
                }
            });
        }
        return dic;
    }

    public Map<String,Integer> generateIntTypeDic(String dicStr){
        Map<String,Integer> dic = new HashMap<>();
        if(StringUtils.isNotEmpty(dicStr)){
            String[] dicArr = dicStr.split(";");
            Arrays.stream(dicArr).forEach(p->{
                if(StringUtils.isNotEmpty(p)){
                    String[] values = p.split(",");
                    if(values.length==2){
                        dic.put(values[0],Integer.valueOf(values[1]));
                    }
                }
            });
        }
        return dic;
    }

    public Integer getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(Integer maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

}
