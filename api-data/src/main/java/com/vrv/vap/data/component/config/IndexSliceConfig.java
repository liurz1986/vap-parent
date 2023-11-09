package com.vrv.vap.data.component.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/7/13
 * @description
 */
@Component
@ConfigurationProperties(prefix = "elk.index")
@EnableConfigurationProperties
public class IndexSliceConfig {


    private String format;
    private String timeFormat;
    private String monthFormat;
    private Date startTime;
    private String[] slice;


    private Map<String, String> alias;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getMonthFormat() {
        return monthFormat;
    }

    public void setMonthFormat(String monthFormat) {
        this.monthFormat = monthFormat;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        try {
            this.startTime = new SimpleDateFormat("yyyy/MM/dd").parse(startTime);
        } catch (ParseException e) {
            this.startTime = new Date(1262275200000l);
        }
    }

    public String[] getSlice() {
        return slice;
    }

    public void setSlice(String[] indices) {
        this.alias = new HashMap<>();
        this.slice = indices;
        for (String id : indices) {
            String ptn[] = id.split(",");
            if (ptn.length > 1) {
                this.alias.put(ptn[0], ptn[1]);
            }
        }
    }

    public String getTimeFormatByIndexName(String index) {
        if (this.alias.containsKey(index)) {
            return this.alias.get(index);
        }
        return this.timeFormat;
    }


    public boolean hasMonthIndex(List<String> indexes) {
        for (String indexName : indexes) {
            String alias = this.getTimeFormatByIndexName(indexName);
            if (alias.length() < 10) {
                return true;
            }
        }
        return false;
    }

}
