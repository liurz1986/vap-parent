package com.vrv.vap.line.constants;

/**
 * Created by Administrator on 2018/3/28.
 */
public class PointConstants {

    /**
     * 高频心跳时间
     */
    public static final String POINT_REQUEST_TIME = "point_request_time";
    /**
     * 发送指令的时间刻度
     */
    public static final String POINT_SEND_TIME = "point_send_time";
    /**
     * 当前指令的最大时间
     */
    public static final String COMMAND_MAX_TIME = "commad_max_time";
    /**
     * 当前任务状态
     */
    public static final String POINT_SEND_STATUS = "point_send_status";

    //===========状态常量=========
    /**
     * 停用
     */
    public static final int STATE_STOP = 0;
    /**
     * 启用
     */
    public static final int STATE_START = 1;
    /**
     * 删除
     */
    public static final int STATE_DELETE = 2;

    /* 点对点任务发送状态 pointSendStatus */
    /**
     * 未发送
     */
    public static final String TASK_UNSEND = "0";

    /**
     * 已发送
     */
    public static final String TASK_SEND = "1";

    /* pintTaskStatus */
    /**
     * 完成
     */
    public static final String STATUS_SUCCESS = "0";

    /**
     * 运行中
     */
    public static final String STATUS_RUNNING = "1";

    /**
     * 失败
     */
    public static final String STATUS_ERROR = "2";

    /**
     * client_point_monitor
     * 监控类型 01 监控机器IP， 02 监控PKI信息
     */
    public static final String MONITOR_TYPE_IP = "01";
    public static final String MONITOR_TYPE_PKI = "02";

    //================================点对点指令类型===============================================
    /**
     * 01 截屏
     */
    public static final String PRINT_SCREEN = "01";
    /**
     * 02 录屏
     */
    public static final String VIDEO_SCREEN = "02";

    /**
     * 进程
     */
    public static final String PROCESS = "03";

    /**
     * 04 网络状态
     */
    public static final String NET_STATUS = "04";

    /**
     * 05 软件列表
     */
    public static final String SOFT_LIST = "05";

    /**
     * 06 插入pki、
     */
    public static final String PKI_IN = "06";
    /**
     * 07 拔出PKI
     */
    public static final String PKI_OUT = "07";

    /**
     * 08 开机
     */
    public static final String POWER_ON = "08";

    /**
     * 09 关机
     */
    public static final String POWER_OFF = "09";

    /**
     * 10 打印
     */
    public static final String PRINE_RECORD = "10";

    /**
     * 11 日志
     */
    public static final String LOG_UPLOAD = "11";

    /**
     * 12 锁屏、休眠等...
     */
    public static final String SYS_INFO = "12";

    /**
     * 99 错误返回
     */
    public static final String ERROR_UPLOAD = "99";

    //可接受点对点上报任务类型
    public static final String[] COMMAND_UPLOAD = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST, PKI_IN, PKI_OUT, POWER_ON, POWER_OFF, PRINE_RECORD, LOG_UPLOAD, ERROR_UPLOAD, SYS_INFO};
    //前端点对点可查询的指令类型
    public static final String[] COMMAND_WEB = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST};
    //点对点任务可查询的指令
    public static final String[] COMMAND_TASK = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST, PKI_IN, PKI_OUT, POWER_ON, POWER_OFF, PRINE_RECORD, SYS_INFO};
    //终端可查询的指令类型
    public static final String[] COMMAND_TERMINAL = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST, PKI_IN, PKI_OUT, POWER_ON, POWER_OFF, PRINE_RECORD, LOG_UPLOAD, ERROR_UPLOAD, SYS_INFO};
    //前端点对点可查询的任务类型
    public static final String[] TASK_WEB = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST};
    //前端历史轨迹可查询的任务类型
    public static final String[] TASK_HISTORY = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST, PKI_IN, PKI_OUT, POWER_ON, POWER_OFF, PRINE_RECORD, SYS_INFO};
    //定时任务可查询的任务类型
    public static final String[] TASK_TASK = {PRINT_SCREEN, VIDEO_SCREEN, PROCESS, NET_STATUS, SOFT_LIST, PKI_IN, PKI_OUT, POWER_ON, POWER_OFF, PRINE_RECORD, SYS_INFO};
    //特殊任务类型--查询日志
    public static final String[] LOG_TYPE = {LOG_UPLOAD};


    //=========================点对点任务状态======================================
    //已完成
    public static final String STATUS_OK_0 = "0";
    //运行中
    public static final String STATUS_RUNNING_1 = "1";
    //指令重复
    public static final String STATUS_DUPLI_2 = "2";
    //接收超时
    public static final String STATUS_RTIMEOUT_3 = "3";
    //发送超时
    public static final String STATUS_STIMEOUT_4 = "4";
    //'客户端运行错误'
    public static final String STATUS_RUNERROR_5 = "5";
    //'参数错误'
    public static final String STATUS_CONTENT_6 = "6";
    //'指令终止'
    public static final String STATUS_STOP_7 = "7";
    //'审批不通过'
    public static final String STATUS_APPROVAL_8 = "8";

    //审批状态


    //==========================校验状态========================================
    //唯一返回 fila
    public static final String[] RECEIVE_WITH_FILE = {PRINT_SCREEN, VIDEO_SCREEN};
    //唯一返回 taskId
    public static final String[] RECEIVE_TASK_ONCE = {PROCESS, NET_STATUS, SOFT_LIST};
    //唯一返回,开关机  key+类型+createTime
    public static final String[] RECEIVE_TYPE_TIME = {PKI_IN, PKI_OUT, POWER_ON, POWER_OFF, PRINE_RECORD};

}
