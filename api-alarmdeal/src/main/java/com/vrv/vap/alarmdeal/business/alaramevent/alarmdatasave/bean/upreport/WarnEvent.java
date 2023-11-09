package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预警事件 type=6
 */
@Data
public class WarnEvent extends AbstractUpEvent{
    private Integer type;
    /**
     * 系统编码，待定
     */
    private String client_id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date update_time;
    private String notice_id;
    /**
     * 附件文件
     */
    private List<CoFile> warn_file = new ArrayList<>();
    private List<AbstractUpData> data;
}
