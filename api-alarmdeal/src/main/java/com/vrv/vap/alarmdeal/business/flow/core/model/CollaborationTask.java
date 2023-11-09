package com.vrv.vap.alarmdeal.business.flow.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 上午11:00:49 
* 类说明 
*/
@Entity
@Data
@Table(name = "collaboration_task")
public class CollaborationTask {
	@Id
    @Column
	private String guid;
    @Column(name="task_type")
	private String taskType; //任务类型
    @Column(name="assign")
	private String assign; //任务指派人
    @Column(name="assgin_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date assignTime; //任务指派时间
    @Column(name="task_status")
    private String taskStatus; //任务状态(未处理，处理中，处理完成)
    @Column(name="ticket_content")
    private String ticketContent; //工单内容
    @Column(name="map_region_info")
    private String mapRegionInfo; //级联区域信息
    @Column(name="ticket_id")
    private String ticketId; //上级工单Id
    @Column(name="collaboration_ticket_id")
    private String collaborationTicketId; //协同工单ID（processInstanceID）
    @Column(name="up_ip")
    private String upIp;   //上级的ip
}
