package com.vrv.vap.alarmdeal.business.flow.core.vo;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import lombok.Data;



@Data
public class FlowMessageVO {

    public static final String CREATE ="create"; // 流程开始

    public static final String END ="end"; // 流程结束

    // 工单名称（比如：事件协办、事件预警等) ,这个涉及到处理队列消息区分事件协办和事件预警，工单名称不能随便改动，改动后会影响队列消息的处理
    private String ticketName;

    private String instanceId; // 流程实例id，business_intance的guid

    private String status ;// create、end(create流程创建、end流程结束)

    public  String busiArgs ;// forms表单的数据，json格式

    public User user; // 当前用户
}
