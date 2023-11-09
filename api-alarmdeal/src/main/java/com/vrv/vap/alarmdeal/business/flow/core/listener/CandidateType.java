package com.vrv.vap.alarmdeal.business.flow.core.listener;

public class CandidateType {
	public static final String USER = "user"; //流程内置用户
	public static final String ROLE = "role"; //流程内置角色
	public static final String ORG = "org"; //用户内置组织结构
	public static final String SEC = "sec"; //用户内置安全域
	public static final String ORG_ROLE="org_role"; //用户上级机构成员
	
	public static final String R_SEC_ARG = "r_sec_arg"; //自定义安全域
	public static final String R_ARG="r_arg";    // 根据制定的业务对象中的属性来，该属性一定是用户的逗号分隔值。配置时使用属性名
	public static final String R_ROLE_ARG = "r_role_arg"; //自定义ROLE,获得对应的用户
	public static final String R_ORG = "r_org";    // 相关组织结构，可能需要更细的分类
	public static final String R_ORG_ROLE = "r_org_role"; // 相关组织结构角色
	public static final String R_ORG_LEADER = "r_org_leader"; //组织结构领导
	public static final String R_ORG_UP_LEADER="r_org_up_leader"; //指定用户上级机构领导
	public static final String R_ORG_UP_ROLE= "r_org_up_role"; //指定用户上级机构成员
	public static final String F_LASTACTIONUSER = "last_action_user";     // 上一步动作的用户
	public static final String F_VARI = "flow_vari";
	public static final String F_CREATE_USER="flow_create_user";    //流程创建人
	public static final String ASSGIN_TASK_PERSION="assgin_task_person";   //指定某一节点的处理人
	public static final String BUSINESTYPE="bussinessType";  //业务触发

	public static final String USERORROLE = "user_role"; //用户和角色   2021-09-10
}
