package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import com.vrv.vap.jpa.web.page.PageReqVap;
import lombok.Data;

/**
 * @author lps 2021/8/4
 */

@Data
public class SuperviseTaskQueryVo extends PageReqVap {

    /**
     * 名称
     */
    private String noticeName;

    /**
     * 类型
     */
    private String  noticeType;

    /**
     * 协办内容
     */
    private String noticeDesc;

    /**
     * 处理状态
     */
    private String dealStatus;

    /**
     * 发起开始时间
     */
    private String createStartTime;

    /**
     * 发起结束时间
     */
    private String createEndTime;
    /**
     * 响应开始时间
     */
    private String responseStartTime;
    /**
     *响应结束时间
     * */
    private String responseEndTime;


    private String taskCreate;


}
