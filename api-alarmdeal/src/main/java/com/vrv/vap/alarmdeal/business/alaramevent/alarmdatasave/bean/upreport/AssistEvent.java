package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zzf
 * 协办上报事件
 * 1 如果是协办申请上报 type=4
 * 2 如果是协办反馈上报 type=5
 */
@Data
public class AssistEvent extends AbstractUpEvent{

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
    private List<CoFile> co_file = new ArrayList<>();
    private List<AbstractUpData> data;

}
