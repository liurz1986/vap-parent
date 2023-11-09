package com.vrv.vap.admin.vo.supervise.putdata;

import lombok.Data;

/**
 * @author lps 2021/8/4
 */

@Data
public class SuperviseTaskReceiveVo {

    private String id;


    /**
     * 名称
     */
    private String noticeName;

    /**
     * 类型
     */
    private String noticeType;


    /**
     * 任务下发时间
     */
    private String sendTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 描述
     */
    private String noticeDesc;

    /**
     * 附件信息
     */
    private String attachment;


    /**
     * 附件名称
     */
    private String fileName;

    /**
     * 事件id
     */
    private String event_id;

}
