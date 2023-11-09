package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import lombok.Data;

/**
 * @author 梁国露
 * @date 2021年11月02日 16:46
 */
@Data
public class NotHaveLogBean {
    /**
     * 日志ID数组
     */
    private String[] logIds;

    /**
     * 告警规则对象
     */
    private EventTable eventTable;

    /**
     * 索引
     */
    private String indexName;

    /**
     * 告警对象
     */
    private AlarmEventAttribute doc;

    /**
     * 构造方法
     *
     * @param logIds
     * @param eventTable
     * @param indexName
     * @param doc
     */
    public NotHaveLogBean(String[] logIds, EventTable eventTable, String indexName, AlarmEventAttribute doc) {
        this.logIds = logIds;
        this.eventTable = eventTable;
        this.indexName = indexName;
        this.doc = doc;
    }
}
