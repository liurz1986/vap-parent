package com.vrv.vap.alarmdeal.business.flow.core.constant;

public class FlowConstant {
	public static final String BUSI_ARG = "busiArg";  // 业务对象
	public static final String ARG = "Arg";   // 额外的参数对象
	public static final String ACTION = "action"; // 操作，默认一个任务都是同一个操作，直接往下，但有时可以有多个选项的操作
	public static final String ADVICE = "advice"; //建议。每个流程过程中，都可以输入的一段文字
    public static final String USERID = "userId"; //执行用户ID
	public static final String INSTANCE = "instance";
	public static final String PARAMS = "PARAMS";  // 业务对象
	public static final String PROCESSRESULT = "process_result";   // 用来存放流程是执行成功还是失败
	public static final String CONTEXTKEY = "contextKey";
	public static final String CONTEXTID = "contextId";
	public static final String PASS_COUNT = "passCount"; //通过数
	public static final String TOTAL_COUNT = "totalCount"; //总数
	public static final String NO_PASS_COUNT = "noPassCount";  //否定数
	public static final String PASS = "审核通过";
	public static final String NO_PASS = "驳回";
	public static final String NROFINSTANCES = "nrOfInstances";
	public static final String NROFCOMPLETEDINSTANCES = "nrOfCompletedInstances";
	public static final String NROFACTIVEINSTANCES = "nrOfActiveInstances";
	public static final String CURRENTUSER = "currentuser"; // 存放当前用户
	public static final String PROCESSINSTANCESTATE = "processInstanceState"; //流程实例状态
}
