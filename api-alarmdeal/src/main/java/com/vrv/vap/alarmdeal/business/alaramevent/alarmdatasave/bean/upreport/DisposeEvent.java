package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 处置事件type=2
 */
@Data
public class DisposeEvent extends AbstractUpEvent{
    private Integer type=2;
    /**
     * 系统编码，待定
     */
    private String client_id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date update_time;


    private List<AbstractUpData> data;

}
