package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit;

import lombok.Data;

/**
 * @author wudi
 * @date 2022/4/20 15:48
 */
@Data
public class StaticsList {
    private String time; //时间
    private String type;//告警名称
    private String dev; //设备IP
    private String user; //用户
    private String org; //财务部
}
