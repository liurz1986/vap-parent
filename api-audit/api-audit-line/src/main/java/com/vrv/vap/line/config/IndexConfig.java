package com.vrv.vap.line.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 索引配置实体类
 *
 * @author xw
 * @date 2018年3月28日
 */
@Component
@ConfigurationProperties(prefix = ConfigPrefix.VAP_ES_INDEX)
public class IndexConfig {
    /**
     * 索引格式
     */
    private String indexFormat;

    /**
     * 启动需要缓存的索引
     */
    private String[] cache;

    /**
     * 是否每次启动都重新构造索引缓存
     */
    private boolean refresh;

    /**
     * 索引的最早时间,eg:2010.10.10
     */
    private Date startTime;

    /**
     * 索引按天日期格式
     */
    private String timeForamt;

    /**
     * 索引按月日期格式
     */
    private String timeFormatMonth;

    /**
     * 最大返回数据条数
     */
    private int resultTotal;

    /**
     * 导出返回的最大数据条数
     */
    private int resultMax;

    /**
     * 按天的索引名
     */
    private String[] dayIndex;

    /**
     * 按月的索引名
     */
    private String[] monthIndex;

    private Map<String, String> timeField;

    public String getIndexFormat() {
        return indexFormat;
    }

    public void setIndexFormat(String indexFormat) {
        this.indexFormat = indexFormat;
    }

    public String[] getCache() {
        return cache;
    }

    public void setCache(String[] cache) {
        this.cache = cache;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = TimeTools.parseDate(startTime, "yyyy.MM.dd");
    }

    public String getTimeForamt() {
        return timeForamt;
    }

    public void setTimeForamt(String timeForamt) {
        this.timeForamt = timeForamt;
    }

    public String getTimeFormatMonth() {
        return timeFormatMonth;
    }

    public void setTimeFormatMonth(String timeFormatMonth) {
        this.timeFormatMonth = timeFormatMonth;
    }

    public int getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(int resultTotal) {
        this.resultTotal = resultTotal;
    }

    public int getResultMax() {
        return resultMax;
    }

    public void setResultMax(int resultMax) {
        this.resultMax = resultMax;
    }

    @Override
    public String toString() {
        return "IndexConfig [indexFormat=" + indexFormat + ", cache=" + Arrays.toString(cache) + ", refresh=" + refresh
                + ", startTime=" + startTime + ", timeForamt=" + timeForamt + ", timeFormatMonth=" + timeFormatMonth
                + ", resultTotal=" + resultTotal + ", resultMax=" + resultMax + "]";
    }

    public String[] getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(String[] dayIndex) {
        this.dayIndex = dayIndex;
    }

    public String[] getMonthIndex() {
        return monthIndex;
    }

    public void setMonthIndex(String[] monthIndex) {
        this.monthIndex = monthIndex;
    }

    public Map<String, String> getTimeField() {
        return timeField;
    }

    public void setTimeField(Map<String, String> timeField) {
        this.timeField = timeField;
    }
}
