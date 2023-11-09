package com.vrv.vap.admin.model;

import lombok.Data;

import javax.persistence.*;

@Data
public class PlatformStatusInfo {
    /**
     * 在线状态
     */
    private int status;

    private String platformName;

    private String platformId;

    private String cpu;

    private String mem;

    private String disk;
}