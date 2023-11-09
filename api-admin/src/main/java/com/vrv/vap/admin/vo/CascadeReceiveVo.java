package com.vrv.vap.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class CascadeReceiveVo {

    private String id;
    private String create_time;
    private String notice_type;
    private String notice_name;
    private String notice_desc;
    private String send_time;
    private List<String> event;
    private List<String> attachment;
    private String apply_unit;
    private String assist_unit;
    private String event_description;
    private String conclusion;

}
