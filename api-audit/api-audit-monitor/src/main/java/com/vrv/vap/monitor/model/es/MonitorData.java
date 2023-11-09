package com.vrv.vap.monitor.model.es;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorData {

    @JsonProperty("dev_id")
    private String devId;

    @JsonProperty("dev_ip")
    private String devIp;

    @JsonProperty("dev_type")
    private String devType;

    @JsonProperty("event_time")
    private String eventTime;

    private String sno;

    @JsonProperty("tips")
    private Map<String, String> tip = new HashMap<>();

    @JsonProperty("units")
    private Map<String, String> unit = new HashMap<>();

    @JsonProperty("monitor_data")
    @JsonInclude
    private Map<String, String> monitorData;
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private Map<String, String> source = new HashMap<>();

/*    @JsonAnyGetter
    public Map<String, String> getSource() {
        return source;
    }

    @JsonAnySetter
    public void setSource(String name, String value) {
        this.source.put(name, value);
    }*/

/*    public void setTips(Map<String, String> tips) {
        this.tips = tips;
    }

    public void setUnits(Map<String, String> units) {
        this.units = units;
    }*/
}
