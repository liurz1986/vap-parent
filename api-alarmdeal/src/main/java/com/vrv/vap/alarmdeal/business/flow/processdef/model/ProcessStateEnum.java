package com.vrv.vap.alarmdeal.business.flow.processdef.model;

/**
 * 流程状态变更图
 * 特别注意：任何保存操作，状态变更都只针对当前流程定义实例。是否产生新的流程定义实例参考流程不变性流程。
 * @author lijihong
 *
 */
public enum ProcessStateEnum {
	draft("草稿",     "stoped",   "draft",      "realDelete", "draft",    "draft"),    // 草稿
	stoped("已停用",   "stoped",   "used",       "deleted",  "stoped",     "stoped"),        // 停用
	used("启用中",     "used",     "used",       "deleting", "stopping",   "used"),          // 启用
	stopping("停用中", "stopping", "used",       "deleting", "stopping",   "stoped"),        // 停用中
	deleting("删除中", "deleting", "used",       "deleting", "stopping",   "deleted"),       // 删除中
	deleted("已删除",  "stoped",   "used",       "deleted",  "stoped",     "deleted"),       // 已删除
	realDelete("真删除","stoped",  "realDelete",     "realDelete","realDelete", "realDelete"  )     // 真删除，用状态标记，另外如果有必要使用另外的作业删除该数据
	;
	
	private static final int actionNum = 5;  // 拥有的操作个数
	private String[] targets = new String[actionNum];
	private String text;
	
	private ProcessStateEnum(String text, String save, String enable, String delete, String disable, String instanceComplete) {
		this.text = text;
		
		targets[0] = save;
		targets[1] = enable;
		targets[2] = delete;
		targets[3] = disable;
		targets[4] = instanceComplete;
	}
	
	public static ProcessStateEnum fromString(String state) {
		switch (state) {
		case "draft":
			return ProcessStateEnum.draft;
		case "stoped":
			return ProcessStateEnum.stoped;
		case "used":
			return ProcessStateEnum.used;
		case "stopping":
			return ProcessStateEnum.stopping;
		case "deleting":
			return ProcessStateEnum.deleting;
		case "deleted":
			return ProcessStateEnum.deleted;
		case "realDelete":
			return ProcessStateEnum.realDelete;
		default:
			throw new RuntimeException("未实现的ProcessStateEnum状态：" + state);
		}
	}
	
	/**
	 * 当前状态执行保存
	 * @return
	 */
	public ProcessStateEnum save() {
		return fromString(targets[0]);
	}
	
	/**
	 * 执行可用
	 * @return
	 */
	public ProcessStateEnum enable() {
		return fromString(targets[1]);
	}
	
	/**
	 * 执行删除
	 * @return
	 */
	public ProcessStateEnum delete() {
		return fromString(targets[2]);
	}
	
	/**
	 * 执行停用
	 * @return
	 */
	public ProcessStateEnum disable() {
		return fromString(targets[3]);
	}
	
	/**
	 * 执行所有实例完成
	 * @return
	 */
	public ProcessStateEnum instanceComplete() {
		return fromString(targets[4]);
	}

	public String getText() {
		return text;
	}
}
