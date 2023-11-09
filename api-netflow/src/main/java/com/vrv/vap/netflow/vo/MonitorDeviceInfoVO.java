package com.vrv.vap.netflow.vo;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wh1107066
 * @date 2023/9/7
 */
@Data
@NoArgsConstructor
public class MonitorDeviceInfoVO implements Serializable {

    private JSONArray cpu;

    private Double memory;

    private Double disk;

    private Date time;

    private Integer did;

    public JSONArray getCpu() {
        return cpu;
    }

    public void setCpu(JSONArray cpu) {
        this.cpu = cpu;
    }

    public Double getMemory() {
        return memory;
    }

    public void setMemory(Double memory) {
        this.memory = memory;
    }

    public Double getDisk() {
        return disk;
    }

    public void setDisk(Double disk) {
        this.disk = disk;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }
}
